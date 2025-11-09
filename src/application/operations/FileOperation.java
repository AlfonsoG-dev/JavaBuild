package application.operations;


import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import application.builders.ScriptBuilder;
import application.utils.FileUtils;

public class FileOperation {

    private FileUtils fileUtils;
    private String localPath;
    private ScriptBuilder scriptBuilder;
    private ExecutorOperation executor;

    public FileOperation(String nLocalPath) {
        localPath = nLocalPath;
        this.fileUtils = new FileUtils(nLocalPath);
        scriptBuilder = new ScriptBuilder(nLocalPath);
        executor = new ExecutorOperation();
    }

    public FileOperation(String nLocalPath, FileUtils fileUtils) {
        localPath = nLocalPath;
        this.fileUtils = fileUtils;
        scriptBuilder = new ScriptBuilder(nLocalPath);
        executor = new ExecutorOperation();
    }
    /**
     * list of the directories inside the source folder that at least have one .java file
     * @param source where the .java files are
     * @return the list of directories
     */
    public List<String> listSourceDirs(String source) {
        return executor.executeConcurrentCallableList(fileUtils.listDirectoryNames(source));
    }
    /**
     * list of the files inside the lib folder
     * @return the list of lib files
     */
    public List<String> listLibFiles() {
        List<String> names = new ArrayList<>();
        File lf = fileUtils.resolvePaths(localPath, "lib");
        if(lf.listFiles() != null) {
            names = fileUtils.listLimitNestedFiles(lf.getPath(), 2)
            .stream()
            .map(Path::toString)
            .toList();
        }
        return names;
    }

    /**
     * main class of the project
     * @return main class file name or empty
     */
    public String getMainClass(String source) {
        String mainName = "";
        String root = source.split("\\" + File.separator)[0];
        if(!new File(source).exists()) return "";
        List<Path> files = fileUtils.listLimitNestedFiles(source, 2);
        outter:for(Path f: files) {
            if(Files.isRegularFile(f) && !f.getFileName().toString().equals("TestLauncher.java")) {
                String[] lines = fileUtils.readFileLines(f.toString()).split("\n");
                for(String l: lines) {
                    if(l.contains("public static void main")) {
                        mainName = f.toString().replace(root + File.separator, "").replace(".java", "").replace(File.separator, ".");
                        break outter;
                    }
                }
            }
        }
        return mainName;
    }
    /**
     * if the main class is empty use the project name as main class
     * @param source where the .java files are
     * @return the project name
     */
    public String getProjectName() {
        String name = "";
        try {
            String localParent = new File(localPath).getCanonicalPath(), localName = new File(localParent).getName();
            name = localName;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return name;
    }
    public String getTestClass(String source, String root) {
        String l = "";
        File f = new File(source);
        if(f.listFiles() != null) {
            for(File mf: f.listFiles()) {
                if(mf.isFile() && mf.getName().equals("TestLauncher.java")) {
                    l = mf.getPath().replace(root + File.separator, "").replace(".java", "").replace(File.separator, ".");
                    break;
                }
            }
        }
        return l;
    }
    /**
     * to verify if the manifesto is present in the project
     * @return true if exists, false otherwise
     */
    public boolean haveManifesto() {
        return fileUtils.fileExists(fileUtils.resolvePaths(localPath, "Manifesto.txt").getPath());
    }
    public void createIgnoreFile(String fileName) {
        String ignoreFiles = "";
        String[] files = {
            "**bin",
            "**lib",
            "**extractionFiles",
            "**Manifesto.txt",
            "**Session.vim",
            "**.jar",
            "**.exe"
        };
        for(String f: files) {
            ignoreFiles += f + "\n";
        }
        fileUtils.writeToFile(ignoreFiles, fileName);
    }
    /**
     * create the manifesto file
     * @param source where the .java files are
     * @param author the author of the project
     * @param extract if to include or not the .jar dependencies of lib
     */
    public void createManifesto(String source, String author, boolean extract) {
        StringBuffer libJars = new StringBuffer();
        if(extract == false) {
            List<String> jars = listLibFiles()
                .stream()
                .filter(p -> p.contains(".jar"))
                .toList();
            libJars.append(jars
                .stream()
                .map(e -> e + ";")
                .collect(Collectors.joining())
            );
        }
        String cleanLibsJars = libJars.toString();
        if((libJars.length()-1) > 0) {
            cleanLibsJars = cleanLibsJars.substring(0, cleanLibsJars.length()-1);
        }
        scriptBuilder.writeManifesto(
            cleanLibsJars, author, getMainClass(source), extract
        );
    }
    /**
     * create the main class inside the source directory
     * @param source where .jar files are
     * @param fileName the name of the main class
     */
    public void createMainClass(String source, String fileName) {
        String mainClassLines = "";
        String main = getProjectName();
        mainClassLines = "class " + main + " {\n" +
            "    public static void main(String[] args) {\n" + 
            "        System.out.println(\"Hellow from " + main + "\");" + "\n" +
            "    }\n" + 
            "}";
        String targetSource = fileUtils.resolvePaths(localPath, source).getPath();
        fileUtils.writeToFile(mainClassLines, fileUtils.resolvePaths(targetSource, fileName).getPath());
    }
    /**
     * create the build script .ps1 or .sh
     * @param source where the .java files are
     * @param target where the .class files are
     * @param fileName the name of the script
     * @param extract if to include or not the .jar lib files
     */
    public void createScript(String source, String target, String fileName, boolean extract) {
        // write build script lines
        scriptBuilder.writeBuildFile(
            fileName,
            getProjectName(),
            source,
            target,
            listSourceDirs(source)
                .stream()
                .map(n -> new File(n))
                .filter(n -> fileUtils.validateContent(n))
                .map(n -> n.getPath() + File.separator + "*.java ")
                .toList(),
            listLibFiles()
                .stream()
                .filter(p -> p.contains(".jar"))
                .toList(),
            extract
        );
    }
    /**
     * verify if a extractFiles directory exists
     * @return true if exists, false otherwise
     */
    public boolean extractionDirContainsPath(String libJarPath) {
        boolean containsPath = false;
        File extractionFile = fileUtils.resolvePaths(localPath, "extractionFiles");
        File myFile = new File(libJarPath);
        String jarLibParent = myFile.getParent();
        if(jarLibParent != null && extractionFile.listFiles() != null) {
            String jarNameParent = new File(jarLibParent).getName();
            for(File f: extractionFile.listFiles()) {
                String extractionDirName = new File(f.getPath()).getName();
                if(extractionDirName.equals(jarNameParent)) {
                    containsPath = true;
                    break;
                }
            }
        }
        return containsPath;
    }
    public Set<String> dependFiles(List<File> sources, String packageName) {
        Set<String> files = new HashSet<>();
        // TODO: maybe use indexation of files to speed up this process
        for(File f: sources) {
            String[] lines = fileUtils.readFileLines(f.getPath()).split("\n");
            for(String l: lines) {
                l = l.trim().replace(";", "");
                String packageDir = packageName;
                if(l.startsWith("import") && l.contains(packageName)) {
                    files.add(f.getPath());
                } else if (l.startsWith("import") && l.contains(packageDir + "*")) {
                    files.add(f.getPath());
                }
            }
        }
        return files;
    }
    /**
     * copy the content of one path to another
     * @param sourceFilePath where you store the content to copy
     * @param targetFilePath where to put the copied files
     */
    public void copyFilesfromSourceToTarget(String sourceFilePath, String targetFilePath) {
        File sf = new File(sourceFilePath);
        if(sf.isFile()) {
            String sourceFileName = sf.getName();
            String sourceParent = sf.getParent();
            String sourceParentName = new File(sourceParent).getName();
            File tf = new File(targetFilePath + File.separator + sourceParentName + File.separator + sourceFileName);
            fileUtils.createParentFile(tf.getPath(), tf.getParent());
            try {
                System.out.println(
                    Files.copy(sf.toPath(), tf.toPath(), StandardCopyOption.COPY_ATTRIBUTES)
                );
            } catch(IOException er) {
                er.printStackTrace();
            }
        }
        if(sf.isDirectory()) {
            List<File> copiedFiles = executor.executeConcurrentCallableList(fileUtils.listFilesFromPath(sourceFilePath))
                .stream()
                .filter(e -> !e.getPath().contains("git"))
                .toList();
            if(copiedFiles.size() > 0) {
                copiedFiles
                    .parallelStream()
                    .forEach( e -> {
                        try {
                            String n = fileUtils.createTargetFromParentPath(sourceFilePath, e.getCanonicalPath());
                            File targetFile = new File(targetFilePath + File.separator + n);
                            fileUtils.createParentFile(targetFilePath, targetFile.getParent());
                            System.out.println(
                            Files.copy(
                                    e.toPath(), targetFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES
                                )
                            );
                        } catch(IOException err) {
                            err.printStackTrace();
                        }
                    });
            }
        }
    }
}

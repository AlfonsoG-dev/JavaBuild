package application.utils;

import java.io.File;
import java.nio.file.Path;

import application.operations.FileOperation;


public class CommandUtils {

    private FileOperation fileOperation;

    public CommandUtils(String localPath) {
        fileOperation = new FileOperation(localPath);
    }
    /**
     * extract parent from a nested path
     * @param source the path to extract the parent
     * @return the parent of the path
     */
    public Path parentFromNesting(Path source) {
        Path p = source;
        int n = source.getNameCount();
        for(int i=n-2; i > 0; --i) {
            p = p.getParent();
        }
        return p;
    }

    /**
     * validate if the file should be re-compile or not
     * @param filePath the file to evaluate
     * @param source the source path where the .java files are
     * @param target the path where the .class files are stored
     * @return true if the file should be re-compile, false otherwise
     */
    public boolean recompileFiles(Path filePath, Path source, Path target) {
        source = source.normalize();
        String root = "." + File.separator + source.toString().split("\\" + File.separator, 2)[0] + File.separator;
        String relative = filePath.toString().replace(root, "");
        String classFilePath = target.resolve(relative.replace(".java", ".class")).toString();

        File javaFile = filePath.toFile();
        File classFile = new File(classFilePath);
        return !classFile.exists() || javaFile.lastModified() > classFile.lastModified();
    }

    /**
     * evaluate the type of format to set the compile, -d if you don't have lib files, -cp if you have lib files
     * @param libFiles the project .jar dependencies
     * @param target where the .class files are stored
     * @param flags the compile flags
     * @param release the java jdk version
     * @return the format -d or -cp in the javac command
     */
    public String compileFormatType(String libFiles, String target, String flags, int release) {
        StringBuffer compile = new StringBuffer();
        compile.append("javac --release " + release + " " + flags + " -d ." + File.separator);
        if(target.isEmpty()) {
            target = "bin";
        }
        compile.append(target + File.separator + " ");
        if(!libFiles.isEmpty()) {
            compile.append(" -cp ");
        }
        return compile.toString();
    }
    /**
     * evaluate the type of format to set the jar file creation, -c with -fm, -f, -fe,
     * @param jarFileName the project jar file name
     * @return the jar file command format
     */
    private String appendJarFormat(StringBuilder build, String mainClass) {
        boolean presentManifesto = fileOperation.haveManifesto();
        if(presentManifesto){
            build.append("m");
            return"m";
        } else if(!mainClass.isEmpty()) {
            build.append("e");
            return "e";
        }
        return "";
    }
    /**
     * @param directory: where the lib files are
     * @param source: directory where .class files are
     * @param target: directory where .java files are, this will serve to find the mainClass
     */
    public String jarTypeUnion(String directory, String source, String target, boolean includeLibs) {
        StringBuilder build = new StringBuilder();

        String jarFileName      = fileOperation.getProjectName() + ".jar";
        String mainClassName = fileOperation.getMainClass(target);

        // create and file
        build.append("jar -cvf");
        // m when there is manifesto and e when manifesto is not present
        String prefix = appendJarFormat(build, mainClassName);
        build.append(" ");

        // add jar file
        build.append(jarFileName);
        build.append(" ");

        // add manifesto or entry point
        switch (prefix) {
            case "m" -> build.append("Manifesto.txt");
            case "e" -> build.append(mainClassName);
        }

        build.append(" ");
        // add .class files
        build.append("-C ");
        build.append(".");
        build.append(File.separator);
        build.append(source);
        build.append(File.separator);
        build.append(" .");

        // add libs
        if(includeLibs) {
            build.append(directory);
        }

        return build.toString();
    }
}

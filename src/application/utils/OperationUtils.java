package application.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.List;

import application.operations.FileOperation;

public class OperationUtils {
    private static final String CONSOL_FORMAT = "%s%n";
    private static final boolean OS_NAME_WINDOWS = System.getProperty("os.name").contains("Windows");

    private FileOperation fileOperation;
    private String localPath;

    public OperationUtils(String nLocalPath, FileOperation fileOperation) {
        this.fileOperation = fileOperation;
        localPath = nLocalPath;
    }
    /**
     * helper function to allow command line process execution 
     * pwsh for windows or /bin/sh for linux.
     * @param command the command to execute
     */
    public void executeCommand(String command) {
        Process p = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            String localFULL = new File(localPath).getCanonicalPath();
            File local = new File(localFULL);
            if(command == null || command.isEmpty()) {
                System.console().printf(CONSOL_FORMAT, "[Warning] Empty command");
                command = "echo Happy-Day";
            } 
            System.console().printf(CONSOL_FORMAT, "[Command] " + command);
            if(OS_NAME_WINDOWS) {
                builder.command("pwsh", "-NoProfile", "-Command", command);
            } else {
                builder.command("/bin/sh", "-c", command);
            }
            builder.directory(local);
            p = builder.start();
            if(p.getErrorStream() != null) {
                commandOutputError(p.getErrorStream());
            }
            if(p.getInputStream() != null) {
                commandOutput(p.getInputStream());
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(p != null) {
                try {
                    p.waitFor();
                    p.destroy();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                p = null;
            }
        }
    }
    /**
     * helper function to show the error when the process execution fails.
     * @param miCmdStream the stream of the process execution
     */
    public void commandOutputError(InputStream miCmdStream) {
        try (BufferedReader miReader = new BufferedReader(new InputStreamReader(miCmdStream))) {
            String line = "";
            while(true) {
                line = miReader.readLine();
                if(line == null) {
                    break;
                }
                System.console().printf(CONSOL_FORMAT, line);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * helper function to show the result of the execution when the process is successful
     * @param miCmdStream the stream of the process execution
     */
    public void commandOutput(InputStream miCmdStream) {
        try (BufferedReader miReader = new BufferedReader(new InputStreamReader(miCmdStream))) {
            String line = "";
            while(true) {
                line = miReader.readLine();
                if(line == null) {
                    break;
                }
                System.console().printf(CONSOL_FORMAT, line);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * create the project files like manifesto or gitignore
     * @param author the name of the project author
     * @param source where the .java files are
     * @param target where to store the .class files
     */
    public void createProjectFiles(String author, String source, String target)  {
        try {
            String mainDirName = new File(localPath).getCanonicalPath();
            String mainClass   = new File(mainDirName).getName();
            System.console().printf(CONSOL_FORMAT, "[Info] Creating files...");
            fileOperation.createIgnoreFile(".gitignore");
            fileOperation.createManifesto(source, author, false);
            fileOperation.createMainClass(source, mainClass + ".java");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * list the .jar files to extract its content
     * @param jars
     */
    public void createExtractionFiles(List<String> jars) {
        File extraction = new File(localPath + File.separator + "extractionFiles");
        if(!extraction.exists() && extraction.mkdir()) {
            System.console().printf(CONSOL_FORMAT, "[Info] Creating extractions files...");
        }
        jars
            .parallelStream()
            .forEach(e -> fileOperation.copyFilesfromSourceToTarget(e, extraction.getPath()));
    }
    /**
     * helper function to copy the content of a directory and add it as a project dependency
     * @param jarFilePath the .jar file to add as dependency
     * @return true if the .jar file is added, false otherwise
     */
    public boolean addJarDependency(String jarFilePath) throws IOException {
        System.console().printf(CONSOL_FORMAT, "[Info] adding jar dependency in process ...");
        String sourceFilePath = "";
        boolean isAdded = false;
        File jarFile = new File(jarFilePath);
        if(!jarFile.exists()) {
            throw new IOException("[Error] Jar file not found");
        }
        if(jarFile.isFile()) {
            sourceFilePath = jarFile.getParent();
        } else {
            sourceFilePath = jarFile.getPath();
        }
        String externalJarName = new File(sourceFilePath).getName();
        File libFile = new File(localPath + File.separator + "lib" + File.separator+ externalJarName);
        if(!libFile.exists()) {
            fileOperation.copyFilesfromSourceToTarget(
                sourceFilePath,
                new File(localPath + File.separator + "lib").getPath()
            );
            isAdded = true;
        } else {
            System.console().printf(CONSOL_FORMAT, "[Info] DEPENDENCY ALREADY INSIDE THE PROJECT");
        }
        return isAdded;
    }
    /**
     * helper function to allow the program to decide witch name to give the build script depending of the OS
     * @param fileName the name provided by the user, if its empty `build` is used.
     * @return the script name.
     */
    public String getBuildFileName(String fileName) {
        String name = "";
        if(OS_NAME_WINDOWS) {
            name = fileName + ".ps1";
        } else {
            name = fileName + ".sh";
        }
        return name;
    }
}

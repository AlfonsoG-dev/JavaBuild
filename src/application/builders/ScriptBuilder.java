package application.builders;

import java.io.Console;
import java.io.File;

import java.util.List;
import java.util.stream.Collectors;

import application.utils.FileUtils;

public class ScriptBuilder {
    private static final Console consol = System.console();
    private static final String CONSOL_FORMAT = "%s%n";

    private static final boolean OS_NAME_WINDOWS = System.getProperty("os.name").contains("Windows");

    private String localPath;
    private FileUtils fileUtils;

    public ScriptBuilder(String nLocalPath) {
        localPath = nLocalPath;
        fileUtils = new FileUtils(nLocalPath);
    }

    /**
     * run command for script file
     * @return the command
     */
    private String getRunScriptCommand() {
        String command = "";
        if(OS_NAME_WINDOWS) {
            command = "$runCommand = " + "\"$compile\" +" + " \" && \" +" + " \"$createJar\" " +
                "+ \" && \" +" + "\"$javaCommand\"" + "\n";
        }
        return command;
    }
    /**
     * java jar command for script file
     * @param mainClass the class to name the jar file
     * @return the command
     */
    private String getJavaScriptCommand(String mainClass) {
        String command = "";
        if(OS_NAME_WINDOWS) {
            command = "$javaCommand = \"java -jar " + mainClass + ".jar\""  + "\n";
        } else {
            command = "java -jar " + mainClass + ".jar\n";
        }
        return command;
    }
    /**
     * union of command to build the project
     * @return the command
     */
    private String getBuildScriptCommand() {
        String command = "";
        if(OS_NAME_WINDOWS) {
            command = "$runCommand = " + "\"$compile\" +" + " \" && \" +" + " \"$createJar\" \n";
        }
        return command;
    }
    /**
     * the compile command
     * @param target where to put the .class files
     * @param libFiles if your project has .jar dependencies
     * @param flags the compile flags
     * @param release the java jdk version
     * @return the command
     */
    private String getCompileCommand(String target, String libFiles, String flags, int release) {
        String command = "javac --release " + release + " " + flags + " -d ." + File.separator + target  + File.separator;
        if(!libFiles.isEmpty()) {
            command += " -cp '$libFiles' $srcClasses";
        } else {
            command += " $srcClasses";
        }
        return command;
    }
    /**
     * union of the command for the script file build.
     * @param srcClasses the .java files
     * @param libFiles the .jar files
     * @param compile the compile command
     * @param extractJar the extraction command
     * @param runJar the run jar command
     * @param runCommand the union of commands
     * @return the script lines.
     */
    public String getScriptLines(String srcClasses, String libFiles, String compile, String extractJar, String runJar, String runCommand) {
        
        StringBuilder sb = new StringBuilder();
        if(OS_NAME_WINDOWS) {
            sb.append("$srcClasses = \"" + srcClasses + "\"\n");
            sb.append("$libFiles = \"" + libFiles + "\"\n");
            sb.append("$compile = \"" + compile + "\"\n");
            sb.append("$createJar = " + "\"" + extractJar + "\"" + "\n");
            sb.append(runJar + runCommand + "Invoke-Expression $runCommand \n");
        } else {
            sb.append("srcClasses=" + "\"" + srcClasses + "\"\n");
            sb.append("libFiles=" + "\"" + libFiles + "\"\n");
            sb.append(compile + "\n");
            sb.append(extractJar + "\n");
            sb.append(runJar);
        }
        return sb.toString();
    }

    /**
     * create manifesto
     * @param libFiles the .jar files
     * @param authorName the name of the person who is the author of the project
     * @param mainClass the name of the main class
     * @param extract if you include or nor the .jar files inside the build process.
     */
    public void writeManifesto(String libFiles, String authorName, String mainClass, boolean extract) {
        String author = authorName.trim();

        StringBuilder m = new StringBuilder();

        m.append("Manifest-Version: 1.0");
        m.append("\n");

        if(!author.isEmpty()) {
            m.append("Created-By: ");
            m.append(author);
            m.append("\n");
        }
        if(!mainClass.isEmpty()) {
            m.append("Main-Class: ");
            m.append(mainClass);
            m.append("\n");
        }
        if(!libFiles.isEmpty() && !extract) {
            consol.printf(CONSOL_FORMAT, "[Warning] Adding `.jar` dependency to the manifesto excluding its content");
            m.append("Class-Path: ");
            m.append(libFiles);
            m.append("\n");
        }

        // write lines to file
        fileUtils.writeToFile(m.toString(), fileUtils.resolvePaths(localPath, "Manifesto.txt").getPath());
    }
    /**
     * create the sentences for the build script
     * @param fileName: path where the build script is created
     * @param mainClass: main class name
     * @param extract: true if you want to include the lib files as part of the jar file, false otherwise
     */
    public void writeBuildFile(String fileName, String mainClass, String source, String target, List<String> dirNames,
    List<String> libNames, boolean extract) {
        CommandBuilder cBuilder = new CommandBuilder(localPath);

        StringBuilder sourceFiles = new StringBuilder();
        StringBuilder libFiles = new StringBuilder();
        String compile = "";
        String runJar = "";
        String runCommand = "";

        sourceFiles.append(
            dirNames
            .stream()
            .collect(Collectors.joining())
        );


        libFiles.append(libNames
            .stream()
            .map(e -> e + ";")
            .collect(Collectors.joining())
        );

        compile = getCompileCommand(target, libFiles.toString(), "-Xlint:all -Xdiags:verbose", 23);

        if(!mainClass.isEmpty()) {
            runJar = getJavaScriptCommand(mainClass);
            runCommand = getRunScriptCommand();
        } else {
            runCommand = getBuildScriptCommand();
        }
        String lines = getScriptLines(
            sourceFiles.toString(), libFiles.toString(), compile,
            cBuilder.getJarFileCommand(extract, target, source),
            runJar, runCommand
        );

        File buildFile = fileUtils.resolvePaths(localPath, fileName);
        fileUtils.writeToFile(lines, buildFile.getPath());
        if(fileName.contains(".sh") && buildFile.setExecutable(true)) {
            consol.printf(CONSOL_FORMAT, "[Info] change file to executable " + buildFile.getPath());
        }
    }
}

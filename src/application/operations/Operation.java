package application.operations;

import application.builders.*;
import application.utils.CommandUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class Operation {
    private static final String COMMAND_OUTPUT_FORMAT = "[Command] %s%n";


    private String[] args;
    private FileOperation fileOperation;

    private CommandUtils commandUtils;

    private CompileBuilder compileBuilder;

    private String oSourcePath;
    private String oClassPath;
    private String oIncludeLib;
    private String oCompileFlags;
    private String root;

    public Operation(String[] args) {
        this.args = args;
        fileOperation = new FileOperation();
        commandUtils = new CommandUtils(args);
    }
    public Operation(String[] args, FileOperation fileOperation) {
        this.args = args;
        this.fileOperation = fileOperation;
        commandUtils = new CommandUtils(args);
    }

    /**
     * Initialize the environment variables.
     * <p> configuration with the command line values.
     * <p> use -c to change the configuration file.
     * <p> use -s to change the source path.
     * <p> use -cp to change the class path.
     * <p> use --i to include/exclude/ignore the lib dependencies.
     */
    public void startUpConfig() {
        String configURI = getPrefixValue("-c");
        String sourcePath = getPrefixValue("-s");
        String classPath = getPrefixValue("-cp");
        String includeLib = getPrefixValue("--i");

        Map<String, String> config = fileOperation.getConfigValues(Optional.ofNullable(configURI).orElse("config.txt"));

        oSourcePath = Optional.ofNullable(sourcePath).orElse(config.get("Source-Path"));
        oClassPath = Optional.ofNullable(classPath).orElse(config.get("Class-Path"));
        oIncludeLib = Optional.ofNullable(includeLib).orElse(config.get("Libraries"));
        oCompileFlags = config.get("Compile-Flags");
        root = config.get("Root-Path");

        compileBuilder = new CompileBuilder(config.get("Root-Path"), fileOperation);
    }
    /**
     * Command operation to compile the project.
     * <p> Use the re-compile command when class path is already created.
     * <p> Use -f to pass one compile flag like -Xlint:all.
     */
    public void compileOperation() {
        if(commandUtils.showHelpOnCompile()) return;
        // For now only 1 argument flags is allowed.
        String flags = getPrefixValue("-f");
        String command = "";
        File classPath = new File(oClassPath);
        if(!classPath.exists()) {
            command = compileBuilder.getCommand(
                    oSourcePath,
                    oClassPath,
                    Optional.ofNullable(flags).orElse(oCompileFlags),
                    oIncludeLib
            );
        } else {
            command = compileBuilder.getReCompileCommand(
                    oSourcePath,
                    oClassPath,
                    Optional.ofNullable(flags).orElse(oCompileFlags),
                    oIncludeLib
            );
        }
        System.console().printf(COMMAND_OUTPUT_FORMAT, command);
    }
    /**
     * Get command to run the project using a main class entry.
     * <p> the main class entry is set by the configuration file or use -e.
     */
    public void runOperation() {

        if(commandUtils.showHelpOnRun()) return;

        String entry = getPrefixValue("-e");

        String command = "";
        if(entry == null) {
            command = new RunBuilder(root, fileOperation)
                .getCommand(
                        oSourcePath,
                        oClassPath,
                        "",
                        oIncludeLib
                );
        } else {
            command = new RunBuilder(root, fileOperation)
                .getCommand(
                        oSourcePath,
                        oClassPath,
                        entry,
                        "",
                        oIncludeLib
                );
        }
        System.console().printf(COMMAND_OUTPUT_FORMAT, command);
    }
    /**
     * Get the command to create the project .jar file.
     * <p> using manifesto file to get the entry point and lib dependencies when they are not included in the build.
     * <p> using main class package name as the entry point.
     * <p> If there are lib dependencies and you have include in your config, they will be copied and extracted to include them in the build process.
     */
    public void createJarOperation() {
        if(commandUtils.showHelpOnCreateJar()) return;
        String flags = getPrefixValue("-f");
        String jarCommand = new JarBuilder(root, fileOperation).getCommand(
                oSourcePath,
                oClassPath,
                Optional.ofNullable(flags).orElse(""),
                oIncludeLib
        );
        System.console().printf(COMMAND_OUTPUT_FORMAT, jarCommand);

        // append jar extraction
        String libPath = getPrefixValue("--l");
        String extractPath = getPrefixValue("--ex");
        String libCommand = new LibBuilder(root, fileOperation).getCommand(
                Optional.ofNullable(libPath).orElse("lib"),
                Optional.ofNullable(extractPath).orElse("extractionFiles"),
                "",
                oIncludeLib

        );
        if(!libCommand.isBlank()) {
            System.console().printf(COMMAND_OUTPUT_FORMAT, libCommand);
        }
    }
    /**
     * Remove the class path of the project in order to compile from scratch.
     * <p> the compile operation should use all the possible paths if class paths doesn't exists.
     */
    public void removeOperation() {
        String removeClassPath = "rm -r " + oClassPath;
        System.console().printf(COMMAND_OUTPUT_FORMAT, removeClassPath);
    }
    /**
     * Add a .jar dependency to a destination path.
     * <p> To give the destination path use --d.
     * <p> If destination path isn't provided lib is use as default value.
     */
    public void addDependency() {
        String prefix = "--add";
        String dependency = getPrefixValue(prefix);
        if(dependency == null) {
            System.console().printf("[Error] No value for prefix | %s | was given%n", prefix);
            return;
        }
        String target = getPrefixValue("--d");
        if(target == null) target = "lib";
        Path destination = Paths.get(target).resolve(dependency);
        fileOperation.copyFileToTarget(dependency, destination.toString());
    }
    /**
     * Get the command line value of a certain prefix.
     * <p> the value is place after the prefix.
     * <p> if no value is provided it returns null.
     * @param prefix - the prefix to search its value.
     * @return the value or null if non is present.
     */
    protected String getPrefixValue(String prefix) {
        for(int i=0; i<args.length; ++i) {
            if(args[i].equals(prefix) && (i+1) < args.length) {
                return args[i+1];
            }
        }
        return null;
    }
    /**
     * Get the command line prefix index.
     * <p> the index is the coordinate of the prefix.
     * <p> if no prefix is present returns -1.
     * @param prefix - the prefix to search its index value.
     * @return the index or -1 if non is present.
     */
    protected int getPrefixIndex(String prefix) {
        for(int i=0; i<args.length; ++i) {
            if(args[i].equals(prefix)) {
                return i;
            }
        }
        return -1;
    }

}

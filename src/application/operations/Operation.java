package application.operations;

import application.builders.*;

import java.util.Map;
import java.util.Optional;

public class Operation {
    private String[] args;
    private FileOperation fileOperation;
    private Map<String, String> config;

    private CompileBuilder compileBuilder;

    private String oSourcePath;
    private String oClassPath;
    private String oIncludeLib;

    public Operation(String[] args) {
        this.args = args;
        fileOperation = new FileOperation();
    }
    public Operation(String[] args, FileOperation fileOperation) {
        this.args = args;
        this.fileOperation = fileOperation;
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

        config = fileOperation.getConfigValues(Optional.ofNullable(configURI).orElse("config.txt"));
        oSourcePath = Optional.ofNullable(sourcePath).orElse(config.get("Source-Path"));
        oClassPath = Optional.ofNullable(classPath).orElse(config.get("Class-Path"));
        oIncludeLib = Optional.ofNullable(includeLib).orElse(config.get("Libraries"));
        compileBuilder = new CompileBuilder(config.get("Root-Path"), fileOperation);
    }
    /**
     * Command operation to compile the project.
     * <p> Use the re-compile command when class path is already created.
     * <p> Use -f to pass one compile flag like -Xlint:all.
     */
    public void compileOperation() {
        // For now only 1 argument flags is allowed.
        String flags = getPrefixValue("-f");
        String command = compileBuilder.getCommand(
                oSourcePath,
                oClassPath,
                Optional.ofNullable(flags).orElse(config.get("Compile-Flags")),
                oIncludeLib
        );
        System.console().printf("[Command] %s", command);
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

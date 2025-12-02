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

    protected String getPrefixValue(String prefix) {
        for(int i=0; i<args.length; ++i) {
            if(args[i].equals(prefix) && (i+1) < args.length) {
                return args[i+1];
            }
        }
        return null;
    }
    protected int getPrefixIndex(String prefix) {
        for(int i=0; i<args.length; ++i) {
            if(args[i].equals(prefix)) {
                return i;
            }
        }
        return -1;
    }

}

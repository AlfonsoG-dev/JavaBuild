package application.builders;

import application.models.CommandModel;
import application.operations.FileOperation;

public record CompileBuilder(FileOperation fileOperation) implements CommandModel {
    public static final String DEFAULT_LIB_PATH = "lib";

    @Override
    public FileOperation getFileOperation() {
        return fileOperation;
    }

    @Override
    public String getCommand(String sourcePath, String classPath, String compileFlags, String includeLib) {
        StringBuilder command = new StringBuilder("javac -d \"");
        // append target or class-path
        if(classPath.isBlank()) return "";
        command.append(String.format("%s\" ", classPath));

        // append compile flags
        if(compileFlags.isBlank()) compileFlags = "-Werror";
        command.append(String.format("%s ", compileFlags));

        if(!includeLib.equals("ignore")) {
            command.append(String.format("-cp '%s' ", preparedLibFiles(DEFAULT_LIB_PATH)));
        }

        // append source files
        String sourceFiles = prepareSourceDirs(sourcePath);
        if(sourceFiles.isEmpty()) return "";
        command.append(sourceFiles);
        return command.toString();
    }
}

package application.builders;

import application.operations.FileOperation;
import application.models.CommandModel;


public record JarBuilder(String root, FileOperation fileOperation)  implements CommandModel {

    @Override
    public FileOperation getFileOperation() {
        return fileOperation;
    }

    @Override
    public String getRoot() {
        return root;
    }

    @Override
    public String getCommand(String sourcePath, String classPath, String flags, String includeLib) {
        StringBuilder command = new StringBuilder("jar -c");

        if(!flags.isBlank()) command.append(flags);
        return command.toString();
    }
}

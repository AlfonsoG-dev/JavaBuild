package application.builders;

import java.io.File;

import application.models.CommandModel;
import application.operations.FileOperation;

public record LibBuilder(String root, FileOperation fileOperation) implements CommandModel  {
    private static final String FILE_EXTENSION = ".jar";
    private static final String CONSOLE_FORMAT = "%s%n";

    @Override
    public FileOperation getFileOperation() {
        return fileOperation;
    }

    @Override
    public String getRoot() {
        return root;
    }

    /**
     * Extract the content of the .jar lib dependency into the extractionFiles directory.
     * <p> Every .jar file present in the lib directory is copy into extractionFiles directory.
     * <p> The name of the dependency is the same name as its parent folder.
     * @param includeLib - to include or not the .jar dependency.
     * @return a command for each one of the extract files separated by ";".
     */
    public String getCommand(String sourcePath, String classPath, String flags, String includeLib) {
        // TODO: test this.
        if(!includeLib.equals("include")) return "";
        StringBuilder command = new StringBuilder();
        if(!flags.isBlank()) {
            command.append("");
        }

        String[] libFiles = preparedLibFiles(sourcePath).split(";");
        if(libFiles.length == 0) return "";

        for(String l: libFiles) {
            //TODO: copy files to extractionFiles
            System.console().printf(CONSOLE_FORMAT, l);
        }

        // copy files from lib to extractionFiles
        File[] extractFiles = new File(classPath).listFiles();
        if(extractFiles == null || extractFiles.length == 0) return "";

        // extract .jar files.
        for(File f: extractFiles) {
            command.append(String.format("cd %s && ", f.toPath().normalize().toString()));
            // FIXME: validate that the file and its parent share the same name.
            // assuming that the .jar file has the same name as its parent folder.
            command.append(String.format("jar -xf %s%s && ", f.getName(), FILE_EXTENSION));
            command.append(String.format("rm -r %s%s%n", f.getName(), FILE_EXTENSION));
        }

        return command.toString();
    }
}

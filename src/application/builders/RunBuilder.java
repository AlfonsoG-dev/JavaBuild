package application.builders;

import application.models.CommandModel;
import application.operations.FileOperation;

public record RunBuilder(String root, FileOperation fileOperation) implements CommandModel {

    public static final String DEFAULT_LIB_PATH = "lib"; 

    @Override
    public FileOperation getFileOperation() {
        return fileOperation;
    }
    @Override
    public String getRoot() {
        return root;
    }

    /**
     * Get the run command.
     * <p> Runs the program using the main class of the project.
     * @param sourcePath - the path where the source files are.
     * @param classPath - the path where the class files are.
     * @param flags - the run flags.
     * @param includeLib - to include or not the lib dependencies.
     * @return the command.
     */
    @Override
    public String getCommand(String sourcePath, String classPath, String flags, String includeLib) {
        StringBuilder command = new StringBuilder("java");

        if(!flags.isBlank()) command.append(String.format(" %s", flags));

        // append class path
        if(classPath.isBlank()) return "";
        command.append(String.format(" -cp '%s", classPath));

        // append lib files
        if(!includeLib.equals("ignore")) {
            command.append(String.format(";%s", preparedLibFiles(DEFAULT_LIB_PATH)));
        }
        command.append("' ");

        // append main class.
        command.append(getMainClass(sourcePath));

        return command.toString();
    }
    /**
     * Get the run command with the given main class.
     * <p> Runs the program using the given main class.
     * @param sourcePath - the path where the source files are.
     * @param classPath - the path where the class files are.
     * @param mainClass - the main class to execute.
     * @param flags - the run flags.
     * @param includeLib - to include or not the lib dependencies.
     * @return the command.
     */
    public String getCommand(String sourcePath, String classPath, String mainClass, String flags, String includeLib) {
        StringBuilder command = new StringBuilder("java");

        if(!flags.isBlank()) command.append(String.format(" %s", flags));

        // append class path
        if(classPath.isBlank()) return "";
        command.append(String.format(" -cp '%s", classPath));

        // append lib files
        if(!includeLib.equals("ignore")) {
            command.append(String.format(";%s", preparedLibFiles(DEFAULT_LIB_PATH)));
        }
        command.append("' ");

        // append main class.
        if(mainClass.isBlank()) command.append(getMainClass(sourcePath));
        command.append(mainClass);

        return command.toString();

    }

}

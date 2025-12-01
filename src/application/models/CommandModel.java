package application.models;
import java.io.File;
import java.nio.file.Path;

import application.operations.FileOperation;

public interface CommandModel {
    public FileOperation getFileOperation();
    public String getCommand(String sourcePath, String classPath, String flags, String includeLib);
    /**
     * Give the directories the format to compile only with *.java.
     * @param pathURI - the path where source files are.
     * @return a string with the directory sources.
     */
    public default String prepareSourceDirs(String pathURI) {
        StringBuilder prepared = new StringBuilder();
        for(Path p: getFileOperation().getDirNames(pathURI, 0)) {
            prepared.append("\"");
            prepared.append(p.normalize().toString());
            prepared.append(String.format("%s%s ", File.separator, "*.java\""));
        }
        return prepared.toString();
    }
    /**
     * List the lib .jar dependencies separated by ;.
     * @param pathURI - the path where the dependencies are.
     * @return a string with the .jar dependencies.
     */
    public default String preparedLibFiles(String pathURI) {
        StringBuilder prepared = new StringBuilder();
        for(Path p: getFileOperation().getFiles(pathURI, 3)) {
            if(p.getFileName().toString().contains(".jar")) {
                prepared.append(String.format("%s;", p.normalize().toString()));
            }
        }
        return prepared.toString();
    }
}

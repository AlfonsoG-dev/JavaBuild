package application.operations;

import application.utils.FileUtils;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileOperation {

    private FileUtils fileUtils;

    public FileOperation() {
        fileUtils = new FileUtils();
    }

    /**
     * Create an instance of this class that receives the FileUtils as argument.
     */
    public FileOperation(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    /**
     * List of only file type content.
     * @param pathURI - the path to list its content.
     * @param level - the nested level to reach.
     * @return the list with only file types.
     */
    public List<Path> getFiles(String pathURI, int level) {
        return fileUtils.getPathContent(pathURI, level)
            .parallelStream()
            .filter(Files::isRegularFile)
            .toList();
    }
    /**
     * List of only directory type content.
     * @param pathURI - the path to list its content.
     * @param level - the nested level to reach.
     * @return the list with only directory types.
     */
    public List<Path> getDirNames(String pathURI, int level) {
        return fileUtils.getPathContent(pathURI, level)
            .parallelStream()
            .filter(Files::isDirectory)
            .toList();
    }

}

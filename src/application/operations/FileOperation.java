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
     * @param fileUtils - the instance of the FileUtils class.
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
    /**
     * Search in the path for a specific line.
     * <p> the line must be contained not equal.
     * @param pathURI - the path to search for the line.
     * @param line - the line to search.
     * @param leve - the nested level to reach.
     * @return the path to the file where the line were founded.
     */
    public String searchLineInPath(String pathURI, String line, int level) {
        List<Path> paths = this.getFiles(pathURI, level);
        for(Path p: paths) {
            String[] lines = fileUtils.getFileLines(p.toString()).split("\n");
            for(String l: lines) {
                if(l.trim().contains(line)) {
                    return p.normalize().toString();
                }
            }
        }
        return "";
    }

}

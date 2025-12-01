package application.operations;

import application.utils.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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
            .filter(p -> fileUtils.countFiles(p.toString()) > 0)
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
    public String getFileWithLine(String pathURI, String line, int level) {
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
    /**
     * Search for the existence of a file in a given path.
     * @param pathURI - path to search for the file.
     * @param fileName - the file to search for its existence.
     * @param level - nested level to reach.
     * @return true if its found, false otherwise.
     */
    public boolean fileIsPresent(String pathURI, String fileName, int level) {
        for(Path p: this.getFiles(pathURI, level)) {
            if(p.normalize().getFileName().toString().toLowerCase().contains(fileName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    /**
     * Get the configuration values from a file.
     * @param fileURI - the configuration file to read the values.
     * @return a map with the key, value relation of the configuration file.
     */
    public Map<String, String> getConfigValues(String fileURI) {
        Map<String, String> content = new HashMap<>();
        String[] lines = fileUtils.getFileLines(fileURI).split("\n");
        if(lines.length == 0) {
            String mainClassLine = "public static void main";
            String mainClass = this.getFileWithLine("src", mainClassLine, 2)
                .replace("src" + File.separator, "")
                .replace(File.separator, ".")
                .replace(".java", "");
            String[][] headers = {
                    {"Root-Path:" + "src"},
                    {"Source-Path:" + "src"},
                    {"Class-Path:" + "bin"},
                    {"Main-Class:" + mainClass},
                    {"Test-Path:" + "src" + File.separator + "test"},
                    {"Test-Class:" + "test.TestLauncher"},
                    {"Libraries:" + "ignore"},
                    {"Compile-Flags:" + "-Werror"}
            };
            for(int i=0; i<headers.length; ++i) {
                for(int j=i; j<headers.length; ++j) {
                    content.put(headers[i][0], headers[i][j]);
                }
            }
        }
        for(String l: lines) {
            String[] options = l.split(":", 2);
            String k = options[0].trim();
            String v = "";
            if(options.length == 2) {
                v = options[1].trim();
            }
            content.put(k, v);
        }
        return content;
    }

}

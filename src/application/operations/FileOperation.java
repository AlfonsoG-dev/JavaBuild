package application.operations;

import application.utils.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileOperation {

    protected static final String[][] DEFAULT_CONFIG_VALUES = {
        {"Root-Path:" + "src"},
        {"Source-Path:" + "src"},
        {"Class-Path:" + "bin"},
        {"Main-Class:" + "App"},
        {"Test-Path:" + "src" + File.separator + "test"},
        {"Test-Class:" + "test.TestLauncher"},
        {"Libraries:" + "ignore"},
        {"Compile-Flags:" + "-Werror"}
    };

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
     * Wrapper method to write lines to a file.
     * @param fileURI - the file to write the lines.
     * @param lines - the lines to write.
     */
    public void writeLines(String fileURI, String lines) {
        if(fileUtils.writeLinesToFile(fileURI, lines)) {
            System.console().printf("[Info] Lines were written to %s", fileUtils);
        }
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
     * Get the configuration default values.
     * @return a string with the default values separated by \n.
     */
    public String getDefaultConfiguration() {
        String lines = "";
        String[][] headers = DEFAULT_CONFIG_VALUES;
        for(int i=0; i<headers.length; ++i) {
            for(int j=i; j<headers.length; ++j) {
                lines = String.format("%s:%s%n", headers[i][0].trim(), headers[i][j].trim());
            }
        }
        return lines;
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
    /**
     * Find if the first file is modified after the creation of second file.
     * <p> First file and second file correspond to a .java and .class file.
     * @param first - is the .java file to find if its newer or equal to the second.
     * @param second - is the .class file to find if its the same or older than the first.
     * @return true if they are different, false otherwise.
     */
    public boolean isNewerThan(Path first, Path second) {
        return !Files.exists(second) || first.toFile().lastModified() > second.toFile().lastModified();
    }
    /**
     * Append the files that depend on the class declare by the package name.
     * @param pathURI - where to search for the dependent files.
     * @param packageName - the package name of the class that other files depend on.
     * @param fileName - the name of the file that other files depend on.
     */
    public void appendDependentFiles(Set<String> dependent, String pathURI, String packageName, String fileName) {

        List<Path> paths = getFiles(pathURI, 0);
        if(paths.isEmpty()) return;

        String dirPackage = packageName.replace(fileName, "*");

        for(Path p: paths) {
            String[] lines = fileUtils.getFileLines(p.toString()).split("\n");
            for(String l: lines) {
                if(l.startsWith("import") && (l.trim().equals(packageName) || l.trim().equals(dirPackage))) {
                    dependent.add(String.format("\"%s\"", p.normalize().toString()));
                }
            }

        }
    }

}

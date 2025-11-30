package application.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {

    /**
     * list the content of a given path in a certain range.
     * <p> The path must be a directory.
     * @param pathURI - the path to list its content
     * @param level - the nested level to reach
     * <p> If the nested level to reach is 0, that means to list the content in a recursively manner.
     * @return the list with the path content.
     */
    public List<Path> getPathContent(String pathURI, int level) {
        level = level > 0 ? level: Integer.MAX_VALUE;
        List<Path> content = new ArrayList<>();
        try (Stream<Path> s = Files.walk(Paths.get(pathURI), level, FileVisitOption.FOLLOW_LINKS)) {
            content.addAll(s.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    /**
     * Create a string with the file lines of the given file path.
     * @param fileURI - the file to read its lines.
     * @return the file lines.
     */
    public String getFileLines(String fileURI) {
        StringBuilder lines = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileURI)))) {
            String line;
            while((line = br.readLine()) != null) {
                // append line and *end of line*
                lines.append(String.format("%s%n", line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.toString();
    }
}

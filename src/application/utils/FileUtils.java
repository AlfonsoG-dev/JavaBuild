package application.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
     * Count only the file type content inside the given path.
     * @param pathURI - the path to count its files.
     * @return the number of type files inside the path.
     */
    public int countFiles(String pathURI) {
        File f = new File(pathURI);
        if(!f.exists() || f.listFiles().length == 0) return 0;
        int n = 0;
        for(File mf: f.listFiles()) {
            if(mf.isFile()) {
                ++n;
            }
        }
        return n;
    }
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
    /**
     * Write lines to a file.
     * <p> If the file doesn't exists it will be created.
     * <p> by default the lines will replace the existing content.
     * @param fileURI - the file to write the lines.
     * @param lines - the lines to write or replace.
     * @return true if the lines were written, false otherwise.
     */
    public boolean writeLinesToFile(String fileURI, String lines) {
        if(lines.isBlank()) return false;
        try (FileWriter writer = new FileWriter(new File(fileURI), false)) {
            writer.write(lines);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Get file lines with lazy loading.
     * @param fileURI - the file to get the lines.
     * @return a lazy load stream with the given file lines.
     */
    public Stream<String> getLazyFileLines(String fileURI) {
        try { 
            return Files.lines(Paths.get(fileURI));
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

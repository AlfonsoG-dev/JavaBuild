package application.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;

import java.nio.file.FileVisitOption;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class FileUtils {
    private static final String CONSOL_FORMAT = "%s%n";
    private String localPath;

    public FileUtils(String localPath) {
        this.localPath = localPath;
    }
    public File getLocalFile() {
        return new File(localPath);
    }
    /**
     * helper function to verify if the file exists
     * @param filePath the path to evaluate
     * @return true if exists, false otherwise
     */
    public boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }
    public boolean createDir(String dirURL) {
        File f = new File(dirURL);
        if(f.exists()) return true;
        if(f.toPath().getNameCount() > 2) return f.mkdirs();
        return f.mkdir();
    }
    /**
     * helper function to write lines to a file, if the file doesn't exists it will be created
     * @param lines the lines to write
     * @param filePath the path of the file
     */
    public void writeToFile(String lines, String filePath) {
        System.console().printf(CONSOL_FORMAT, "[Info] Writing lines...\n" + lines);
        try(FileWriter w = new FileWriter(getLocalFile().toPath().resolve(filePath).toFile())) {
            w.write(lines);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * helper function to normalize any path
     * @param filePath the path to normalize
     * @return the normalized path
     */
    public String getCleanPath(String filePath) {
        return Paths.get(filePath).normalize().toString();
    }
    /**
     * it concatenates two paths into one
     * @param root the parent path
     * @param children the path to add to the parent
     * @return the unified path
     */
    public File resolvePaths(String root, String children) {
        return Paths.get(root).resolve(children).toFile();
    }
    /**
     * helper function to count the files inside a directory
     * @param f the directory path
     * @return the number of files inside or 0.
     */
    public int countFiles(File f) {
        if(f.listFiles() == null || f.listFiles().length == 0) return 0;
        File[] files = f.listFiles();
        int n = 0;
        for(File mf: files) {
            if(mf.isFile()) {
                ++n;
            }
        }
        return n;
    }
    /**
     * validates if the directory has any .java file
     * @param f the directory path
     * @return true if it has .java files, false otherwise
     */
    public boolean validateContent(File f) {
        if(f.isDirectory() && f.listFiles() != null) {
            for(File v: f.listFiles()) {
                if(v.isFile() && v.getName().contains(".java")) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * helper function to list the files inside a directory
     * @param filePath the directory where the files are.
     * @return a list with the files inside the directory in a recursively manner
     */
    public List<Path> listFiles(String filePath) {
        List<Path> result = new ArrayList<>();
        try(Stream<Path> s = Files.walk(Paths.get(filePath), FileVisitOption.FOLLOW_LINKS)) {
            System.console().printf(CONSOL_FORMAT, "Reading path ...");
            result = s
                .filter(Files::isRegularFile)
                .toList();

        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public List<Path> listLimitNestedFiles(String filePath, int level) {
        List<Path> result = new ArrayList<>();
        try(Stream<Path> s = Files.walk(Paths.get(filePath), level, FileVisitOption.FOLLOW_LINKS)) {
            result = s
                .filter(Files::isRegularFile)
                .toList();

        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;

    }
    /**
     * helper function to list files with Callable wrapping.
     * @param filePath the directory to list its files.
     * @return a Callable with the list of files.
     */
    public Callable<List<Path>> listFilesFromPath(String filePath) {
        return () -> {
            List<Path> files = new ArrayList<>();
            try(Stream<Path> s= Files.walk(Paths.get(filePath), FileVisitOption.FOLLOW_LINKS)) {
                files = s
                    .filter(Files::isRegularFile)
                    .toList();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return files;
        };
    }
    public Callable<List<File>> listLimitNestedFilesFromPath(String filePath, int level) {
        return () -> listLimitNestedFiles(filePath, level).stream().map(Path::toFile).toList();
    }
    /**
     * helper function to list directories with Callable wrapping.
     * @param filePath the directory to list its directories.
     * @return a Callable with the list of directories.
     */
    public Callable<List<String>> listDirectoryNames(String filePath) {
        return () -> getDirectoryNames(filePath).stream().map(File::getPath).toList();
    }
    /**
     * helper function to resolve a path from parent and children
     * @param parentFile the parent path
     * @param dirs the path to resolve into
     * @return the unified paths, changes its parent
     */
    public String createTargetFromParentPath(String parentFile, String dirs) {
        String parentName = new File(parentFile).getParent();
        return dirs.replace(parentName, "");
    }
    /**
     * helper function to create directories.
     * @param targetFilePath the root directory
     * @param parentFileNames the path to resolve into
     */
    public void createParentFile(String targetFilePath, String parentFileNames) {
        String[] parentNames = parentFileNames.split("\n");
        for(String pn: parentNames) {
            String nFileName = new File(pn).toPath().normalize().toFile().getPath();
            File mio = new File(pn);
            int fileLength = new File(nFileName).toPath().getNameCount();
            if(!mio.exists() && fileLength > 1) {
                mio.mkdirs();
                System.console().printf(CONSOL_FORMAT, "[Info] created " + mio.getPath());
            } else if(!mio.exists() && fileLength <= 1) {
                mio.mkdir();
                System.console().printf(CONSOL_FORMAT, "[Info] created " + mio.getPath());
            }
        }
    }
    /**
     * helper function to get lines from a file
     * @param path the file path
     * @return the file lines
     */
    public String readFileLines(String path) {
        StringBuilder lines = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
            String l;
            while((l = reader.readLine()) != null) {
                lines.append(l);
                lines.append("\n");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return lines.toString();
    }
    /**
     * helper function to list file lines wrapped in a Callable expression
     * @param filePath the file to read lines
     * @return a Callable with the list of lines
     */
    public Callable<List<String>> listFileLines(String filePath) {
        return () -> {
            List<String> lines = new ArrayList<>();
            try(BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
                String line;
                while((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return lines;
        };
    }
    /**
     * helper function to list the directories inside a directory. Each directory needs to have at least one file.  
     * @param dirPath the directory to list its directories
     * @return the list of directories.
     */
    private List<File> getDirectoryNames(String dirPath) {
        List<File> names = new ArrayList<>();
        try(Stream<Path> s = Files.walk(Paths.get(dirPath), FileVisitOption.FOLLOW_LINKS)) {
            names = s
                .map(Path::toFile)
                .filter(p -> p.isDirectory() && countFiles(p) > 0)
                .toList();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return names;
    }
}

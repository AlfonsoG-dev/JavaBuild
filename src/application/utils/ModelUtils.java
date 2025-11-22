package application.utils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import application.operations.ExecutorOperation;
import application.operations.FileOperation;

import java.io.File;
import java.nio.file.Path;

public class ModelUtils {
    private static final String CONSOL_FORMAT = "%s%n";

    private String sourcePath;
    private String classPath;
    private String localPath;

    private FileUtils fUtils;
    private CommandUtils cUtils;

    private FileOperation fOperation;
    private ExecutorOperation executor;

    public ModelUtils(String sourcePath, String classPath, String localPath) {
        this.sourcePath = sourcePath;
        this.classPath = classPath;
        this.localPath = localPath;

        fUtils = new FileUtils(localPath);
        cUtils = new CommandUtils(localPath);

        fOperation = new FileOperation(localPath);
        executor = new ExecutorOperation();
    }
    public ModelUtils(String sourcePath, String classPath, String localPath, FileUtils fUtils, FileOperation fOperation, ExecutorOperation executor) {

        this.sourcePath = sourcePath;
        this.classPath = classPath;
        this.localPath = localPath;

        this.fUtils = fUtils;
        this.cUtils = new CommandUtils(localPath);

        this.fOperation = fOperation;
        this.executor = executor;
    }
    /**
     * Collect the source files inside an Optional
     * @return optional of source files
     */
    public Optional<String> getSourceFiles() {
        String b = null;
        List<String> names = new ArrayList<>();
        File sourceFile = fUtils.resolvePaths(localPath, sourcePath);
        File classFile = fUtils.resolvePaths(localPath, classPath);
        if(sourceFile.listFiles() == null) {
            System.console().printf(CONSOL_FORMAT, "[Info] " + sourceFile.getPath() + " is empty");
            return Optional.ofNullable(b);
        }
        if((classFile.exists() && classFile.listFiles() == null) || !classFile.exists()) {
            names.addAll(fOperation.listSourceDirs(sourcePath)
                .stream()
                .map(File::new)
                .filter(n -> fUtils.validateContent(n))
                .map(n -> n.getPath() + File.separator + "*.java ")
                .toList()
            );
        } else if((classFile.exists() && classFile.listFiles() != null) || classFile.listFiles().length > 0) {
            List<Path> files = executor.executeConcurrentCallableList(fUtils.listFilesFromPath(sourceFile.toString()));
            Set<String> recompileFiles = new HashSet<>();
            // add only re-compile files
            for(Path p: files) {
                if(cUtils.recompileFiles(p, sourceFile.toPath(), classFile.toPath())) {
                    recompileFiles.add(p.toString());
                }
            }
            // add dependencies of re-compile files
            for(String f: recompileFiles) {
                String root = f.replace(".java", "").split("\\.", 2)[1].split("\\" + File.separator, 2)[1].split("\\" + File.separator, 2)[0];
                String packageName = f.replace(".java", "").replace("." + File.separator + root + File.separator, "").replace(File.separator, ".");
                recompileFiles.addAll(fOperation.dependFiles(files, packageName));
            }
            // add unique entry files to names
            names.add(recompileFiles
                .stream()
                .collect(Collectors.joining(" "))
            );
        } 
        if(!names.isEmpty()) {
            b = names
            .stream()
            .collect(Collectors.joining());
        }
        return Optional.ofNullable(b);
    }
    /**
     * list of lib files
     */
    public List<String> getLibFiles() { 
        List<String> names = new ArrayList<>();
        List<String> libfiles = fOperation.listLibFiles();
        if(!libfiles.isEmpty()) {
            names.addAll(libfiles
                .stream()
                .filter(e -> e.contains(".jar"))
                .toList()
            );
        }
        return names;
    }
    /**
     * list of .jar files
     */
    public String getJarDependencies() {
        String b = "";
        List<String> libFiles = getLibFiles();
        if(!libFiles.isEmpty()) {
            b = libFiles
                .stream()
                .collect(Collectors.joining(";"));
        } 
        return b;
    }
}

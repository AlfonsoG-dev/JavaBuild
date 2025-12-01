package application.builders;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import application.models.CommandModel;
import application.operations.FileOperation;

public record CompileBuilder(String root, FileOperation fileOperation) implements CommandModel {
    public static final String DEFAULT_LIB_PATH = "lib";
    public static final String FILE_EXTENSION = ".java";

    @Override
    public FileOperation getFileOperation() {
        return fileOperation;
    }

    @Override
    public String getCommand(String sourcePath, String classPath, String compileFlags, String includeLib) {
        StringBuilder command = new StringBuilder("javac -d \"");
        // append target or class-path
        if(classPath.isBlank()) return "";
        command.append(String.format("%s\" ", classPath));

        // append compile flags
        if(compileFlags.isBlank()) compileFlags = "-Werror";
        command.append(String.format("%s ", compileFlags));

        if(!includeLib.equals("ignore") && !preparedLibFiles(DEFAULT_LIB_PATH).isBlank()) {
            command.append(String.format("-cp '%s' ", preparedLibFiles(DEFAULT_LIB_PATH)));
        }

        // append source files
        String sourceFiles = prepareSourceDirs(sourcePath);
        if(sourceFiles.isEmpty()) return "";
        command.append(sourceFiles);
        return command.toString();
    }
    public String getReCompileCommand(String sourcePath, String classPath, String compileFlags, String includeLib) {
        StringBuilder command = new StringBuilder("javac -d \"");
        // append target or class-path
        if(classPath.isBlank()) return "";
        command.append(String.format("%s\" ", classPath));

        // append compile flags
        if(compileFlags.isBlank()) compileFlags = "-Werror";
        command.append(String.format("%s ", compileFlags));

        // append assets
        command.append(String.format("-cp '%s", classPath));
        if(!includeLib.equals("ignore") && preparedLibFiles(DEFAULT_LIB_PATH).isBlank()) {
            command.append(String.format(";%s", preparedLibFiles(DEFAULT_LIB_PATH)));
        }
        command.append("' ");

        // append re-compile files
        List<Path> paths = fileOperation.getFiles(sourcePath, 0);
        Set<String> dependent = new HashSet<>();
        for(Path p: paths) {
            String comparator = p.normalize().toString()
                .replace(root + File.separator, classPath + File.separator)
                .replace(FILE_EXTENSION, ".class");
            Path second = Paths.get(comparator);
            if(fileOperation().isNewerThan(p, second)) {
                dependent.add(p.toString());
                String packageName = "import " + p.normalize().toString()
                    .replace(root + File.separator, "")
                    .replace(File.separator, ".")
                    .replace(FILE_EXTENSION, "") + ";";
                fileOperation.appendDependentFiles(
                        dependent, sourcePath, packageName,
                        p.getFileName().toString().replace(FILE_EXTENSION, "")
                );
            }
        }
        command.append(dependent
                .stream()
                .collect(Collectors.joining(" "))
        );

        return command.toString();
    }
}

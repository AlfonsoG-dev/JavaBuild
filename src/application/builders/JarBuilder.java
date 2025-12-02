package application.builders;

import application.operations.FileOperation;
import application.models.CommandModel;


import java.io.File;

import java.nio.file.Path;

import java.util.List;



public record JarBuilder(String root, FileOperation fileOperation)  implements CommandModel {
    private static final String DEFAULT_EXTRACT_PATH = "extractionFiles";
    private static final String DEFAULT_LIB_PATH = "lib";

    @Override
    public FileOperation getFileOperation() {
        return fileOperation;
    }

    @Override
    public String getRoot() {
        return root;
    }

    @Override
    public String getCommand(String sourcePath, String classPath, String flags, String includeLib) {
        StringBuilder command = new StringBuilder("jar -c");

        if(!flags.isBlank()) command.append(flags);

        // append .jar file
        command.append("f");

        // append asset format m for manifesto or e for main class or empty when non are present.
        String assetFormat = getJarAssetFormat(command, sourcePath);

        // assign .jar file name
        command.append(String.format("%s.jar ", getProjectName()));
        // append assets
        switch (assetFormat) {
            case "m " -> command.append("Manifesto.txt");
            case "e " -> command.append(getMainClass(sourcePath));
            default -> command.append("");
        }

        // append class files.
        command.append(String.format(" -C %s%s . ", classPath, File.separator));

        // append lib dependencies
        if(includeLib.equals("include")) {
            File[] extractFiles = new File(DEFAULT_EXTRACT_PATH).listFiles();
            String[] libFiles = preparedLibFiles(DEFAULT_LIB_PATH).split(";");
            if(libFiles.length == 0) return command.toString();
            if(extractFiles != null && extractFiles.length < libFiles.length) {
                System.console().printf("[Warning] %s%n", "You have lib dependencies pending extraction.");
            }
            if(extractFiles == null) return command.toString();
            for(File f: extractFiles) {
                command.append(String.format(" -C %s%s . ", f.toPath().normalize().toString(), File.separator));
            }
        }

        return command.toString();
    }
    public String getJarAssetFormat(StringBuilder command, String sourcePath) {
        String name = "";
        if(haveManifesto()) {
            name = "m ";
        } else if(!getMainClass(sourcePath).isBlank()) {
            name = "e ";
        }
        command.append(name);
        return name;
    }
}

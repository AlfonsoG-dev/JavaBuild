package application.builders;

import application.operations.FileOperation;
import application.models.CommandModel;


import java.io.File;
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

    /**
     * Get the command to create .jar files.
     * @param sourcePath - the path where the source files are.
     * @param classPath - the path where the class files are.
     * @param flags - the .jar command flags.
     * @param includeLib - to include or not the lib dependencies.
     * @return the command.
     */
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
            if(extractFiles == null || extractFiles.length == 0) return command.toString();

            String[] libFiles = preparedLibFiles(DEFAULT_LIB_PATH).split(";");
            if(libFiles.length == 0) return command.toString();

            if(extractFiles.length < libFiles.length) {
                System.console().printf("[Warning] %s%n", "You have lib dependencies pending extraction.%n");
            }
            for(File f: extractFiles) {
                command.append(String.format(" -C %s%s . ", f.toPath().normalize().toString(), File.separator));
            }
        }

        return command.toString();
    }
    /**
     * Append the corresponding format for the .jar build.
     * <p> append m when there is a manifesto file present.
     * <p> append e when no manifesto file is present and the project has a main class.
     * <p> if non of those are present append and empty string.
     * @return the .jar format.
     */
    public String getJarAssetFormat(StringBuilder command, String sourcePath) {
        String name = " ";
        if(haveManifesto()) {
            name = "m ";
        } else if(!getMainClass(sourcePath).isBlank()) {
            name = "e ";
        }
        command.append(name);
        return name;
    }
}

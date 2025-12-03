package application.builders;

import java.io.File;

public record LibBuilder(String root, JarBuilder jarBuilder) {
    private static final String DEFAULT_EXTRACT_PATH = "extractionFiles";
    private static final String DEFAULT_LIB_PATH = "lib";
    private static final String FILE_EXTENSION = ".jar";
    private static final String CONSOLE_FORMAT = "%s%n";

    /**
     * Extract the content of the .jar lib dependency into the extractionFiles directory.
     * <p> Every .jar file present in the lib directory is copy into extractionFiles directory.
     * <p> The name of the dependency is the same name as its parent folder.
     * @param includeLib - to include or not the .jar dependency.
     * @return a command for each one of the extract files separated by ";".
     */
    public String getExtractCommand(String includeLib) {
        // TODO: test this.
        if(!includeLib.equals("include")) return "";
        StringBuilder command = new StringBuilder();

        File[] libFiles = new File(DEFAULT_LIB_PATH).listFiles();
        if(libFiles == null || libFiles.length == 0) return "";

        // copy files from lib to extractionFiles
        for(File l: libFiles) {
            for(File f: l.listFiles()) {
                System.console().printf(CONSOLE_FORMAT, f.toPath().normalize().toString());
            }
        }
        File[] extractFiles = new File(DEFAULT_EXTRACT_PATH).listFiles();
        if(extractFiles == null || extractFiles.length == 0) return "";

        // extract .jar files.
        for(File f: extractFiles) {
            command.append(String.format("cd %s && ", f.toPath().normalize().toString()));
            command.append(String.format("jar -xf %s%s && ", f.getName(), FILE_EXTENSION));
            command.append(String.format("rm -r %s%s%n", f.getName(), FILE_EXTENSION));
        }

        return command.toString();
    }
}

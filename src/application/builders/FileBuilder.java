package application.builders;

import java.io.File;

import application.models.CommandModel;

public class FileBuilder {

    private static final String CONFIG_FORMAT = "%s: %s%n";
    private static final String DEFAULT_TEST_PATH = "src" + File.separator + "test";
    private static final String DEFAULT_LIB_PATH = "lib";

    private CommandModel model;
    public FileBuilder(CommandModel model) {
        this.model = model;
    }

    /**
     * Create the configuration file with the default values if the file doesn't exists.
     * <p> The default configuration is given by file operation static field.
     */
    public void createConfig(String fileURI, String sourcePath, String classPath, String mainClass,
            String flags, String includeLib ) {
        StringBuilder config = new StringBuilder();
        String[] headers;
        if(!new File(fileURI).exists()) {
            headers = model.getFileOperation().getDefaultConfiguration().split("\n");
        } else {
            // FIXME: only change the values that are different from the current configuration file.
            headers = new String[] {
                String.format(CONFIG_FORMAT, "Root-Path", model.getRoot()),
                String.format(CONFIG_FORMAT, "Source-Path", sourcePath),
                String.format(CONFIG_FORMAT, "Class-Path", classPath),
                String.format(CONFIG_FORMAT, "Main-Class", mainClass), 
                String.format(CONFIG_FORMAT, "Test-Path", DEFAULT_TEST_PATH), 
                String.format(CONFIG_FORMAT, "Test-Class", model.getMainClass(DEFAULT_TEST_PATH)), 
                String.format(CONFIG_FORMAT, "Libraries", includeLib),
                String.format(CONFIG_FORMAT, "Compile-Flags", flags),
            };
        }
        for(int i=0; i<headers.length; ++i) {
            String[] l = headers[i].trim().split(":", 2);
            String k = l[0];
            String v = l[1];
            config.append(String.format("%s:%s%n", k, v));
        }
        System.console().printf("[Info] Writing lines:%n%s%n", config);
        model.getFileOperation().writeLines(fileURI, config.toString());
    }
    /**
     * Create the manifesto file if it doesn't exists, otherwise modify the content.
     * @param fileURI - the file path to the manifesto file.
     * @param author - the author of the project.
     * @param mainClass - the main class to execute.
     * @param includeLib - to include or not the lib dependencies.
     */
    public void createManifesto(String fileURI, String author, String mainClass, String includeLib) {
        StringBuilder data = new StringBuilder();
        if(new File(fileURI).exists()) {
            modifyManifestData(data, fileURI, author, mainClass, includeLib);
        } else {
            data.append("Manifest-Version: 1.0\n");
            data.append("Created-By: System-Owner\n");
            data.append(String.format("Main-Class: %s%n", mainClass));
            if(includeLib.equals("exclude")) {
                data.append(String.format("Class-Path: %s%n", model.preparedLibFiles(DEFAULT_LIB_PATH)));
            }
        }
        model.getFileOperation().writeLines(fileURI, data.toString());
    }
    /**
     * Method to modify the manifesto data if the file exists.
     */
    private void modifyManifestData(StringBuilder data, String fileURI, String author, String mainClass, String includeLib) {
        String[] lines = model.getFileOperation().getFileLiles(fileURI).split("\n");
        for(String l: lines) {
            String[] pair = l.split(":", 2);
            String k = pair[0].trim();
            String v = pair[1].trim();
            if(k.equals("Manifest-Version")) {
                v = String.valueOf(Double.parseDouble(v) + 0.1);
            }
            if(k.equals("Created-By") && !v.equals(author)) {
                v = author;
            }
            if(k.equals("Main-Class") && !v.equals(mainClass)) {
                v = mainClass;
            }
            if(includeLib.equals("exclude") && k.equals("Class-Path") && !v.equals(model.preparedLibFiles(DEFAULT_LIB_PATH))) {
                v = model.preparedLibFiles(DEFAULT_LIB_PATH);
            }
            data.append(String.format(CONFIG_FORMAT, k, v));
        }
    }

}

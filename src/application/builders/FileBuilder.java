package application.builders;

import java.io.File;

import application.models.CommandModel;

public class FileBuilder {

    private static final String CONFIG_FORMAT = "%s: %s%n";
    private static final String DEFAULT_TEST_PATH = "src" + File.separator + "test";

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

}

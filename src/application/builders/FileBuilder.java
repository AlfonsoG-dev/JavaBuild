package application.builders;

import application.models.CommandModel;

public class FileBuilder {

    private CommandModel model;
    public FileBuilder(CommandModel model) {
        this.model = model;
    }

    /**
     * Create the configuration file with the default values if the file doesn't exists.
     * <p> The default configuration is given by file operation static field.
     */
    public void createConfig(String fileURI) {
        StringBuilder config = new StringBuilder();
        String[] headers = model.getFileOperation().getDefaultConfiguration().split("\n");
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

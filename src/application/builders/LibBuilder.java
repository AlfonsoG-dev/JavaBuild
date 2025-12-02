package application.builders;

import java.io.File;

public record LibBuilder(String root, JarBuilder jarBuilder) {
    private static final String DEFAULT_EXTRACT_PATH = "extractionFiles";
    private static final String DEFAULT_LIB_PATH = "lib";

    public String extractCommand() {
        StringBuilder command = new StringBuilder();
        // change directory to the extraction files.
        File[] extractFiles = new File(DEFAULT_EXTRACT_PATH).listFiles();
        if(extractFiles == null || extractFiles.length == 0) return "";

        return command.toString();
    }
}

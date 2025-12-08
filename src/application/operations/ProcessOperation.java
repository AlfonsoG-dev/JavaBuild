package application.operations;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessOperation {

    private static final String LOCAL_PATH = "." + File.separator;
    private static final boolean OS_IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");
    private static final String CONSOLE_FORMAT = "[%s] %s%n";
    private static Console console = System.console();


    public void executeCommands(String command) {
        console.printf(CONSOLE_FORMAT, "Info", "Executing commands.");
        try {
            if(command == null || command.isBlank()) {
                command = "echo Happy-Day";
            }
            console.printf(CONSOLE_FORMAT, "Command", command);
            Process p = getProcessForCommandExecution(command).start();
            handleProcessStream(p);
            p.waitFor();
            p.destroy();
        } catch(IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
    private ProcessBuilder getProcessForCommandExecution(String command) {
        ProcessBuilder builder = null;
        if(OS_IS_WINDOWS) {
            builder = new ProcessBuilder("pwsh", "-NoProfile", "-Command", command);
        } else {
            builder = new ProcessBuilder("bin/bash", "-c", command);
        }
        try {
            builder.directory(new File(new File(LOCAL_PATH).getCanonicalPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }
    private void handleProcessStream(Process p) {
        if(p.getInputStream() != null) {
            handleInputStream(p.getInputStream());
        }
        if(p.getErrorStream() != null) {
            handleInputStream(p.getErrorStream());
        }
    }
    private void handleInputStream(InputStream input) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while((line = reader.readLine()) != null) {
                System.console().printf("%s%n", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

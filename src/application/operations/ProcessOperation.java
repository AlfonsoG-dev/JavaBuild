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


    /**
     * Executes the given command with attach to it.
     * <br> If the command is empty or NULL, a Happy-Day message would be printed.
     * @param command - the command to execute.
     */
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
    /**
     * Create a process to attach the commands.
     * <br> The process executes using *pwsh* on windows and *bash* on linux/mac.
     * @param command - command to attach to the process.
     * @return the process with the commands, ready to execute.
     */
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
    /**
     * Helper function to print the success/failure messages of the executed process.
     * @param p - the executed process.
     */
    private void handleProcessStream(Process p) {
        if(p.getInputStream() != null) {
            handleInputStream(p.getInputStream());
        }
        if(p.getErrorStream() != null) {
            handleInputStream(p.getErrorStream());
        }
    }
    /**
     * Helper function to compute the input stream of success/failure of the executed process.
     * @param input - the stream to compute.
     */
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

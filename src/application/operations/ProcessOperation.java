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
}

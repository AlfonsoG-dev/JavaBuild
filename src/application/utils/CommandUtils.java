package application.utils;

import java.io.Console;

public class CommandUtils {
    private static Console console = System.console();
    private static final String CONSOLE_FORMAT = "%s%n";

    private String[] args;

    public CommandUtils(String[] args) {
        this.args = args;
    }
    /**
     * Help messages for the compile command.
     * <p> Using --compile --h as second argument.
     */
    public boolean showHelpOnCompile() {
        String prefix = "--compile";
        String compileValue = getPrefixValue(prefix);
        if(!compileValue.equals("--h")) return false;
        StringBuilder help = new StringBuilder();

        help.append(String.format("Use [%s] to compile the project.%n", prefix));
        help.append(String.format("\tIf you want to pass a compile flag: %n", ""));
        help.append(String.format("\t => Use [%s -f -Werror] to compile the project with the flags that you want.%n", prefix));
        help.append(String.format("\tOnly one flag is allowed so: %n", ""));
        help.append(String.format("\t => This [%s -f -Werror -Xlint:all] will only get the first argument -Werror.%n", prefix));
        help.append(String.format("\tThis behavior might change in other version: %n", ""));
        help.append(String.format("%n\tIf you already compile the project: %n", ""));
        help.append(String.format("\t => This [%s] will only compile the modified files and the files that depend on it.%n", prefix));

        console.printf(CONSOLE_FORMAT, help.toString());
        return true;
    }
    public boolean showHelpOnRun() {
        String prefix = "--run";
        String runValue = getPrefixValue(prefix);
        if(!runValue.equals("--h")) return false;

        StringBuilder help = new StringBuilder();

        help.append(String.format("Use [%s] to run the project.%n", prefix));
        help.append(String.format("\tIf you want to use other main class: %n", ""));
        help.append(String.format("\t => Use [%s -e pacakage.App] to run other main class.%n", prefix));
        help.append(String.format("\tOnly one main class is allowed so: %n", ""));
        help.append(String.format("\t => This [%s -e app other.App] will only get the first argument app.%n", prefix));
        help.append(String.format("\tThis behavior might change in other version: %n", ""));
        help.append(String.format("\t => This [%s] will only run the project using the class path files.%n", prefix));

        console.printf(CONSOLE_FORMAT, help.toString());
        return true;
    }
    public boolean showHelpoOnCreateJar() {
        String prefix = "--jar";
        if(!isHelpCommand()) return false;

        StringBuilder help = new StringBuilder();

        help.append(String.format("Use [%s] to create the .jar file of the project.%n", prefix));
        help.append(String.format("\tIf you have dependencies include/exclude/ignore them by changing you configuration file on: %n", ""));
        help.append(String.format("\t => Libraries: include/exclude/ignore%n", ""));
        help.append(String.format("\tOnly one option is allowed.%n", ""));
        help.append(String.format("\tIf you want to include the dependencies in your .jar file use include.%n", ""));
        help.append(String.format("\tIf you don't want to include the dependencies in your .jar file use exclude.%n", ""));
        help.append(String.format("\tIf you don't want to use in any of the command the dependencies use ignore.%n", ""));
        help.append(String.format("%n\tYou can change the directory where the dependencies are: %n", ""));
        help.append(String.format("\t => Use [%s --l dependency-path] to change the dependency path.%n", prefix));
        help.append(String.format("\tYou can change the directory where the dependencies are extracted: %n", ""));
        help.append(String.format("\t => Use [%s --ex extract-path] to change the extraction path.%n", prefix));
        help.append(String.format("%n\tYou can pass a jar command flags: %n", ""));
        help.append(String.format("\t => Use [%s -f v] to append the v flag in the jar command.%n", prefix));

        console.printf(CONSOLE_FORMAT, help.toString());
        return true;
    }
        for(int i=0; i<args.length; ++i) {
            if(args[i].equals(prefix) && (i+1) < args.length) {
                return args[i+1];
            }
        }
        return "";
    }

}

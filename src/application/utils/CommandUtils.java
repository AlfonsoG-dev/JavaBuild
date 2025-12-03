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

        console.printf(CONSOLE_FORMAT, help.toString());
        return true;
    }

    private String getPrefixValue(String prefix) {
        for(int i=0; i<args.length; ++i) {
            if(args[i].equals(prefix) && (i+1) < args.length) {
                return args[i+1];
            }
        }
        return "";
    }

}

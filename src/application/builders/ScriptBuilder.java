package application.builders;

import java.io.File;

import application.models.CommandModel;

public class ScriptBuilder {
    private CommandModel commandModel;
    public ScriptBuilder(CommandModel commandModel) {
        this.commandModel = commandModel;
    }

    private static final String DEFAULT_LIB_CONFIG = "ignore";
    private static final boolean OS_NAME_WINDOWS = System.getProperty("os.name").equals("Windows 11");

    public void appendSource(StringBuilder lines, String sourceURI) {
        if(OS_NAME_WINDOWS) {
            lines.append("$Source=");
            lines.append("\"");
            lines.append(commandModel.prepareSourceDirs(sourceURI).replace("\"", ""));
            lines.append("\"\n");
        } else {
            lines.append("source=");
            lines.append("\"");
            lines.append(commandModel.prepareSourceDirs(sourceURI).replace("\"", ""));
            lines.append("\"\n");
        }
    }
    public void appendLib(StringBuilder lines, String libURI) {
        if(OS_NAME_WINDOWS){
            lines.append("$Libs=");
            lines.append("\"");
            lines.append(commandModel.preparedLibFiles(libURI));
            lines.append("\"\n");
        } else {
            lines.append("libs=");
            lines.append("\"");
            lines.append(commandModel.preparedLibFiles(libURI));
            lines.append("\"\n");
        }
    }
    public void appendCompileCommand(StringBuilder lines, String targetURI, String includeLib) {
        if(OS_NAME_WINDOWS) {
            lines.append("$Compile=");
            lines.append("\"");
            lines.append("javac -d ");
            lines.append(targetURI);
            if(!includeLib.equals("ingore")) {
                lines.append(" -cp '");
                lines.append("$Libs'");
            }
            lines.append(" $Source");
            lines.append("\"\n");
        } else {
            lines.append("javac -d ");
            lines.append(targetURI);
            if(!includeLib.equals(DEFAULT_LIB_CONFIG)) {
                lines.append(" -cp '");
                lines.append("$libs'");
            }
            lines.append(" $source");
            lines.append("\n");
        }
    }
    public void appendCreateJarCommand(StringBuilder lines, String targetURI, String includeLib) {
        // TEST: create jar command
        if(OS_NAME_WINDOWS) {
            lines.append("$Jar=\"jar -cfm ");
            lines.append(commandModel.getProjectName());
            lines.append(".jar ");
            lines.append("Manifesto.txt -C ");
            lines.append(targetURI);
            lines.append(File.separator);
            lines.append(" .");
            if(includeLib.equals("include")) {
                File f = new File("extractionFiles");
                for(File l: f.listFiles()) {
                    lines.append(" -C ");
                    lines.append(l.getPath());
                    lines.append(File.separator);
                    lines.append(" .");
                }
            }
            lines.append(" \"\n");
        } else {
            // TEST: linux support
            lines.append("jar -cfm ");
            lines.append(commandModel.getProjectName());
            lines.append(".jar ");
            lines.append("Manifesto.txt -C ");
            lines.append(targetURI);
            lines.append(File.separator);
            lines.append(" .");
            if(includeLib.equals("include")) {
                File f = new File("extractionFiles");
                for(File l: f.listFiles()) {
                    lines.append(" -C ");
                    lines.append(l.getPath());
                    lines.append(File.separator);
                    lines.append(" .");
                }
            }
            lines.append("\n");
        }
    }
    public void appendRunCommand(StringBuilder lines, String sourceURI, String targetURI, String includeLib) {
        if(OS_NAME_WINDOWS) {
            lines.append("$Run=");
            lines.append("\"");
            lines.append("java -cp '");
            lines.append(targetURI);
            if(!includeLib.equals(DEFAULT_LIB_CONFIG)) {
                lines.append(";");
                lines.append("$Libs");
            }
            lines.append("' ");
            lines.append(commandModel.getMainClass(sourceURI));
            lines.append("\"\n");
        } else {
            lines.append("java -cp '");
            lines.append(targetURI);
            if(!includeLib.equals(DEFAULT_LIB_CONFIG)) {
                lines.append(";");
                lines.append("$libs");
            }
            lines.append("' ");
            lines.append(commandModel.getMainClass(sourceURI));
            lines.append("\n");
        }
    }
    // only on windows
    public void appendExecuteCommands(StringBuilder lines) {
        if(OS_NAME_WINDOWS) {
            lines.append("Invoke-Expression ($Compile + \" && \" + $Jar + \" && \" + $Run)\n");
        }
    }

    public String getScript(String sourceURI, String targetURI, String includeLib, String libURI) {
        StringBuilder lines = new StringBuilder();
        appendSource(lines, sourceURI);
        if(!includeLib.equals(DEFAULT_LIB_CONFIG)) {
            appendLib(lines, libURI);
        }
        appendCompileCommand(lines, targetURI, includeLib);
        appendCreateJarCommand(lines, targetURI, includeLib);
        appendRunCommand(lines, sourceURI, targetURI, includeLib);
        appendExecuteCommands(lines);
        return lines.toString();
    }

}

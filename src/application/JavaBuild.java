package application;

import application.operations.Operation;

class JavaBuild {
    static final String[] HEADERS = {
        "Use [--compile] to compile the project using the configuration file if present.",
        "Use [--run] to run the project using the main class file.",
        "Use [--jar] to create the '.jar' file of the project using '.class' files and the main class as entry point.",
        "Use [--script] to create the project build script.",
        "Use [--build] to compile from scratch and create the '.jar' file of the project.",
        "Use [--add] to append a '.jar' dependency to the project.",
        "Use [--h, ?, h, --help, or help] to show a more detailed description of each command."
    };
    public static void main(String[] args) {
        Operation op = new Operation(args);
        op.startUpConfig();
        for(String a: args) {
            switch(a) {
                case "--compile":
                    op.compileOperation();
                    break;
                case "--run":
                    op.runOperation();
                    break;
                case "--jar":
                    op.createJarOperation();
                    break;
                case "--script":
                    op.createScript();
                    break;
                case "--build":
                    op.removeOperation();
                    op.compileOperation();
                    op.createJarOperation();
                    break;
                case "--add":
                    op.addDependency();
                    break;
                case "--h":
                    System.console().printf("%n");
                    showCommands();
                    break;
                default:
                    break;
            }
        }
    }
    public static void showCommands() {
        for(String h: HEADERS) {
            System.console().printf("%s%n", h);
        }
    }
}

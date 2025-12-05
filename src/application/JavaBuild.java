package application;

import application.operations.Operation;

class JavaBuild {
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
                case "--build":
                    op.removeOperation();
                    op.compileOperation();
                    op.createJarOperation();
                    break;
                case "--ad":
                    op.addDependency();
                    break;
                case "--h":
                    System.console().printf("%s%n", "Use --h for help");
                    break;
                default:
                    break;
            }
        }
    }
}

package application.models;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import application.utils.CommandUtils;
import application.utils.ModelUtils;

public class CompileModel {

    public static final String LOCAL_PATH = "." + File.separator;

    private String classPath;
    private String flags;

    private CommandUtils cUtils;
    private ModelUtils mUtils;


    public CompileModel(String sourcePath, String classPath, String flags) {
        this.classPath = classPath;
        this.flags = flags;
        cUtils = new CommandUtils(LOCAL_PATH);
        mUtils = new ModelUtils(sourcePath, classPath, LOCAL_PATH);

        // invariant
        CompileModel.verify(sourcePath, classPath);
    }

    /**
     * Create the compile command, it has two version.
     * When you create from `--scratch` you don't need individual files; you need each directory in the project that has at least one .java file. 
     * when you create from `--compile` or `--build` you only want the files that were modified later in the build process; for that you need each individual file to re-compile it.
     * @param release the java jdk version
     * @return the compile command
     */
   public String getCompileCommand(int release) {
        // create jar files command for compile operation
        StringBuilder libFiles = new StringBuilder();
        StringBuilder cLibFiles = new StringBuilder();
        StringBuilder compile = new StringBuilder();

        // lib files
        libFiles.append(mUtils.getJarDependencies());

        String format = cUtils.compileFormatType(libFiles.toString(), classPath, flags, release);
        String srcClases = "";

        Optional<String> oSource = mUtils.getSourceFiles();
        if(oSource.isEmpty()) {
            System.console().printf("%s%n", "[Info] No modified files to compile");
            return null;
        }
        srcClases = oSource.get();

        if(!srcClases.contains("*.java")) {
            compile.append(format);
            if(!libFiles.isEmpty()) {
                compile.append(" '");
                compile.append(classPath);
                compile.append(";");
                compile.append(libFiles);
                compile.append("' ");
            } else {
                compile.append(" -cp '");
                compile.append(classPath);
                compile.append("' ");
            }
        } else {
            compile.append(format);
            if(!libFiles.isEmpty()) {
                String cb = libFiles.toString().trim();

                cLibFiles.append("'");
                cLibFiles.append(cb);
                cLibFiles.append("' ");

                compile.append(cLibFiles);
            }
        }
        compile.append(srcClases);
        return compile.toString();
    }

    // ----------------------------\\
    //   Verify non-null values    \\

    /**
     * verify that for this class you don't provide empty paths.
     * @param sourcePath where the .java files are.
     * @param classPath where to store .class files
     */
    public static void verify(String sourcePath, String classPath) {
        try {
            if(sourcePath.isEmpty() || classPath.isEmpty()) {
                throw new IOException("[Error] No empty paths allowed");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

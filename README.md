# Java build tool

- Basic build tool for simple java projects
- Build the project with 1 or 3 simple commands
- It works on WINDOWS & LINUX

## References

- [compile_references](https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html)
- [create_jar_references](https://docs.oracle.com/javase/tutorial/deployment/jar/index.html)
- [run_java_references](https://docs.oracle.com/en/java/javase/21/docs/specs/man/java.html)
- [executable_from_jar_linux](https://stackoverflow.com/questions/44427355/how-to-convert-jar-to-linux-executable-file)
- [tsoding-nob](https://www.youtube.com/watch?v=l9_TUMZSewo&list=PLpM-Dvs8t0Va1sCJpPFjs2lKzv8bw3Lpw&index=5)

## Instructions

The following are the instructions to do in order to use the build tool.

1. Clone the repository:

```sh
git clone https://github.com/AlfonsoG-dev/JavaBuild
```

2. Use the build script to create a `.jar` file.

```sh
pwsh build.ps1
```

3. Use the `.jar` file to build <u>Java projects</u>

```sh
java -jar JavaBuild --compile
```

## Available Commands

The following are the list of options to use when building a <u>Java</u> project.

1. `-c path` to change the configuration file path.
2. `-s path` to change where the `.java` files are.
3. `-cp path` to change where the `.class` files are.
4. `--i exclude` to change between `include/exclude/ignore` options for the dependencies.

> Each one of the following commands also use the previous options
as part of their process.

The following are the list of commands with their own individual options.

### Compile

To compile a <u>Java</u> project use:

```sh
java -jar javaBuild.jar --compile
```

> This command also receives the following options:
- `-f` to add flags to the compile process.

### Run

To run a <u>Java</u> project use:

```sh
java -jar javaBuild.jar --run
```

> This command also receives the following options:
- `-e package.Main` to run another class.

### Create Jar

To create the project `.jar` file use:

```sh
java -jar javaBuild.jar --jar
```

> This command also receives the following options:
- `--l path` to change where to search for the project dependencies.
- `--ex path` to change where to extract the content of the project dependencies.
- `-f flag` to add custom flags to the process.
- `-n name` to change the name of the project `.jar` file.

### Add dependency

To add a dependency to the project use:

```sh
java -jar javaBuild.jar --add path.jar
```

> This command also receives the following options:
- `--d path` to change where the dependency is store.

All the previous information is also available
through the command line using the prefix `?` after each command:

```sh
java -jar javaBuild.jar --compile ?
```

------

## Disclaimer

- This project is for educational purposes.
- Security issues are not taken into account.
- Use it at your own risk.

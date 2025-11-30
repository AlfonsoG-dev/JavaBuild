$SourceFiles = "src\application\*.java src\application\builders\*.java src\application\models\*.java src\application\operations\*.java src\application\utils\*.java"
$Compile = "javac -d bin -Werror $SourceFiles"
$CreateJar = "jar -cfm JavaBuild.jar Manifesto.txt -C .\bin\ ."
$Run = "java -jar JavaBuild.jar"
Invoke-Expression ($Compile + " && " + $CreateJar + " && " + $Run)

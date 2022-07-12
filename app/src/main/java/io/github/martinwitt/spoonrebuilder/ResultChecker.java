package io.github.martinwitt.spoonrebuilder;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.maven.cli.MavenCli;

public class ResultChecker {
  
  private Path resultFolderPath;
  private boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
  public ResultChecker(Path resultFolderPath) {
    this.resultFolderPath = resultFolderPath;
  }

  public void check() throws IllegalStateException {
    System.out.println("checking build result");
    ProcessBuilder processBuilder = new ProcessBuilder();
    try {
      Process process;
      if(isWindows) {
        process = processBuilder.command("powershell.exe", "/C", "mvn", "clean", "package", "-DskipDepClean=true")
            .directory(resultFolderPath.toFile()).inheritIO().start();
      } else {
        process = processBuilder.command("sh", "mvn", "clean", "package", "-DskipDepClean=true")
            .directory(resultFolderPath.toFile()).inheritIO().start();
      }
      int result = process.waitFor();
     if(result != 0) {
       throw new IllegalStateException("Maven build failed");
     }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      throw new IllegalStateException("Maven build failed");
    }
  }
}

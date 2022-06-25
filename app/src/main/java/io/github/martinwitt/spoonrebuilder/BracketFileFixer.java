package io.github.martinwitt.spoonrebuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.tuple.Pair;

public class BracketFileFixer implements Consumer<Path> {

  private String fileName;
  private List<Pair<String, String>> list = new ArrayList<>();
  
  
  /**
   * @param fileName
   * @param list
   */
  public BracketFileFixer(String fileName, List<Pair<String, String>> list) {
    this.fileName = fileName;
    this.list = list;
  }


  @Override
  public void accept(Path path) {
    if (path.getFileName().toString().startsWith(fileName)) {
      try {
        String content = Files.readString(path);
        for (Pair<String, String> pair : list) {
          content = content.replaceAll(pair.getLeft(), pair.getRight());
        }
        Files.writeString(path, content, StandardOpenOption.WRITE);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

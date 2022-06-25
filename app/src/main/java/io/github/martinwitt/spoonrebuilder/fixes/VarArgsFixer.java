package io.github.martinwitt.spoonrebuilder.fixes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class VarArgsFixer implements Consumer<Path> {


  @Override
  public void accept(Path path) {
    if (!Files.isDirectory(path) && path.getFileName().toString().endsWith("java")) {
      try {
        String content = Files.readString(path);
        content = content.replaceAll(Pattern.quote("......"),"...");
        Files.writeString(path, content, StandardOpenOption.TRUNCATE_EXISTING);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

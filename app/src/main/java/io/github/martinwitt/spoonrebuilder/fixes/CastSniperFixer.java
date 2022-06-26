package io.github.martinwitt.spoonrebuilder.fixes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class CastSniperFixer implements Consumer<Path> {
  private final Set<String> names;

  private Set<String> fileNames = new HashSet<>();
  @Override
  public void accept(Path path) {
    if (!Files.isDirectory(path) && path.getFileName().toString().endsWith("java")) {
      try {
        String content = Files.readString(path);

        for (String type : names) {
          content = content.replaceAll("\\(\\(" + type +"(?:<.*>)?"+ "\\)\\)",
              "(" + type + ")");
          content = content.replace("finalspoon", "final spoon");
          content = content.replace("abstractclass", "abstract class");
        }
        Files.writeString(path, content, StandardOpenOption.TRUNCATE_EXISTING);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @param types
   */
  public CastSniperFixer(Iterable<CtType<?>> types) {
    fileNames.add("AbstractTypingContext");
    fileNames.add("ClassTypingContext");
    fileNames.add("ReplacementVisitor");
    this.names  = StreamSupport.stream(types.spliterator(), false)
        .flatMap(v -> v.getReferencedTypes().stream()).map(CtTypeReference::getSimpleName)
        .collect(Collectors.toSet());
    for(char alphabet : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
      names.add(Character.toString(alphabet).toUpperCase());
    }
  }
  
  

  
}

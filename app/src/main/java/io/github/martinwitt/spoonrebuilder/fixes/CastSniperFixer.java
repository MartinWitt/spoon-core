package io.github.martinwitt.spoonrebuilder.fixes;

import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CastSniperFixer implements UnaryOperator<String> {
  private final Set<String> names;

  /**
   * @param types
   */
  public CastSniperFixer(Iterable<CtType<?>> types) {
    this.names  = StreamSupport.stream(types.spliterator(), false)
        .flatMap(v -> v.getReferencedTypes().stream()).map(CtTypeReference::getSimpleName)
        .collect(Collectors.toSet());
    for(char alphabet : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
      names.add(Character.toString(alphabet).toUpperCase());
    }
  }


  @Override
  public String apply(String s) {
    String content = s;

    for (String type : names) {
      content = content.replaceAll("\\(\\(" + type +"(?:<.*>)?"+ "\\)\\)",
          "(" + type + ")");
      content = content.replace("finalspoon", "final spoon");
      content = content.replace("abstractclass", "abstract class");
    }
    return content;
  }
}

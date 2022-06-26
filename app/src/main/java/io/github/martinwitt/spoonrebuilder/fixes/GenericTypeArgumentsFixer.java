package io.github.martinwitt.spoonrebuilder.fixes;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class GenericTypeArgumentsFixer implements UnaryOperator<String> {
  private static final Pattern INNER_PATTERN = Pattern.compile("<(Ct\\w+)<[^>]+>>");
  @Override
  public String apply(String s) {
    return INNER_PATTERN.matcher(s).replaceAll("<$1>");
  }
}

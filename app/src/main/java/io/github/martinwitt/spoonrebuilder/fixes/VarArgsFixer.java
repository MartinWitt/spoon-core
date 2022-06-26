package io.github.martinwitt.spoonrebuilder.fixes;

import java.util.function.UnaryOperator;

public class VarArgsFixer implements UnaryOperator<String> {

  @Override
  public String apply(String s) {
    return s.replace("......","...");
  }
}

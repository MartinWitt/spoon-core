package io.github.martinwitt.spoonrebuilder.fixes;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

public class TemplateParameterProcessor extends AbstractProcessor<CtMethod<?>> {

  @Override
  public void process(CtMethod<?> method) {
    if (method.getSimpleName().equals("S")) {
      method.setType((CtTypeReference) method.getFactory().Type().voidType());
    }
  }
}

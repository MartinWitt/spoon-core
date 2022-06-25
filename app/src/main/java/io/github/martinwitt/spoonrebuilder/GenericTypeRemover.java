package io.github.martinwitt.spoonrebuilder;

import java.util.ArrayList;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

/**
 * Removes the generics from all CtElements
 */
public class GenericTypeRemover extends AbstractProcessor<CtType<?>> {

  @Override
  public void process(CtType<?> element) {
    if (isSpoonType(element) && isSubtypeOfCtElement(element) && isNotCtLiteral(element)) {
      element.setFormalCtTypeParameters(new ArrayList<>());
    }
  }

  private boolean isNotCtLiteral(CtType<?> element) {
    return !element.isSubtypeOf(getFactory().createCtTypeReference(CtLiteral.class));
  }

  private boolean isSubtypeOfCtElement(CtType<?> element) {
    return element.isSubtypeOf(getFactory().createCtTypeReference(CtElement.class));
  }

  private boolean isSpoonType(CtType<?> element) {
    return element.getQualifiedName().startsWith("spoon");
  }
}

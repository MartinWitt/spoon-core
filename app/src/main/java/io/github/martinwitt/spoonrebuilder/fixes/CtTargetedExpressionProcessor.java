package io.github.martinwitt.spoonrebuilder.fixes;

import spoon.processing.AbstractProcessor;
import spoon.refactoring.CtRefactoring;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

public class CtTargetedExpressionProcessor extends AbstractProcessor<CtMethod<?>> {

  @Override
  public void process(CtMethod<?> method) {
    if(method.getDeclaringType().getReference().isSubtypeOf(method.getFactory().createCtTypeReference(
        CtRefactoring.class))) {
      return;
        }
    if (method.getSimpleName().equals("setTarget")) {
      method.getParameters().iterator().next().setType(createCtTargetedExpressionRef(method));
    }
    if (method.getSimpleName().equals("getTarget")) {
      method.setType(createCtTargetedExpressionRef(method));
    }
  }

  private <T> CtTypeReference<T> createCtTargetedExpressionRef(CtMethod<?> method) {
    return method.getFactory().Type().createReference(CtTargetedExpression.class.getName());
  }

}

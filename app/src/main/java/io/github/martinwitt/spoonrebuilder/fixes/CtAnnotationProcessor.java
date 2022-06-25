package io.github.martinwitt.spoonrebuilder.fixes;

import java.util.ArrayList;
import java.util.List;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;

public class CtAnnotationProcessor extends AbstractProcessor<CtMethod<?>> {

  @Override
  public void process(CtMethod<?> method) {
    if (method.getSimpleName().equals("getActualAnnotation")) {
      var parameter = method.getFactory().createTypeParameter();
      parameter.setSimpleName("A");
      List<CtTypeParameter> parameters = new ArrayList<>();
      parameters.add(parameter);
      method.setFormalCtTypeParameters(parameters);
      method.setType((CtTypeReference) parameter.getReference().getTypeDeclaration().getReference());
    }
  }
}

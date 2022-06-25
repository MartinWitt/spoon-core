package io.github.martinwitt.spoonrebuilder.fixes;

import java.util.ArrayList;
import java.util.List;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;

public class GetActualClassProcessor extends AbstractProcessor<CtMethod<?>> {

  @Override
  public void process(CtMethod<?> method) {
    if (method.getSimpleName().equals("getActualClass")) {
      var parameter = method.getFactory().createTypeParameter();
      parameter.setSimpleName("A");
      List<CtTypeParameter> parameters = new ArrayList<>();
      parameters.add(parameter);
      method.setFormalCtTypeParameters(parameters);
      CtTypeReference<?> ref = method.getFactory().createReference(Class.class.getName());
      ref.setActualTypeArguments(
          List.of(parameter.getReference().getTypeDeclaration().getReference()));
      method
          .setType((CtTypeReference) ref);
    }    
  }
  
}

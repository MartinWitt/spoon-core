package io.github.martinwitt.spoonrebuilder.fixes;

import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.units.qual.A;
import spoon.processing.AbstractProcessor;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;

public class NewInstanceProcessor extends AbstractProcessor<CtMethod<?>> {

  @Override
  public void process(CtMethod<?> method) {
    if (method.getSimpleName().equals("newInstance")) {
      var parameter = method.getFactory().createTypeParameter();
      parameter.setSimpleName("T");
      List<CtTypeParameter> parameters = new ArrayList<>();
      parameters.add(parameter);
           method.setFormalCtTypeParameters(parameters);
      method
          .setType((CtTypeReference) parameter.getReference().getTypeDeclaration().getReference());
          markElementForSniperPrinting(method);
    }
    
  }
  
  /**
  * Modify an element such that the sniper printer detects it as modified, without changing its final content. This
  * forces it to be sniper-printed "as-is".
  */
  private static void markElementForSniperPrinting(CtElement element) {
    SourcePosition pos = element.getPosition();
    element.setPosition(SourcePosition.NOPOSITION);
    element.setPosition(pos);
  }
}

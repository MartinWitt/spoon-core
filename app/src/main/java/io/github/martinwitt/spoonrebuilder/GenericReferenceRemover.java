package io.github.martinwitt.spoonrebuilder;

import java.util.ArrayList;
import spoon.processing.AbstractProcessor;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

public class GenericReferenceRemover extends AbstractProcessor<CtTypeReference<?>> {

  @Override
  public void process(CtTypeReference<?> reference) {
    if(reference.getPackage() !=null && reference.getDeclaration() != null && !reference
        .getActualTypeArguments().isEmpty()) {
      CtType<?> type = reference.getDeclaration();
      if (!type.isParameterized()) {
        reference.setActualTypeArguments(new ArrayList<>());
        markElementForSniperPrinting(reference);
        if (CtRole.CAST.equals(reference.getRoleInParent())) {
          markElementForSniperPrinting(reference.getParent(CtMethod.class));
          markElementForSniperPrinting(reference.getParent());

        }
      }
    }    
  }

  private boolean isSpoonType(CtTypeReference<?> element) {
    return element.getQualifiedName().startsWith("spoon")||true;
  }
  
  /**
  * Modify an element such that the sniper printer detects it as modified, without changing its final content. This
  * forces it to be sniper-printed "as-is".
  */
  private static void markElementForSniperPrinting(CtElement element) {
    if(element == null) {
      return;
    }
    SourcePosition pos = element.getPosition();
    element.setPosition(SourcePosition.NOPOSITION);
    element.setPosition(pos);
  }
}

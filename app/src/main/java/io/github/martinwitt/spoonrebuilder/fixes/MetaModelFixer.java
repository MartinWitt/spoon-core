package io.github.martinwitt.spoonrebuilder.fixes;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class MetaModelFixer extends AbstractProcessor<CtType<?>> {

  @Override
  public void process(CtType<?> type) {
    if (type.getSimpleName().equals("Metamodel")) {
      type.getMethodsByName("getRoleOfMethod").stream()
          .flatMap(getAllInvocations())
          .filter(v -> v.getExecutable().getSimpleName().equals("getActualAnnotation"))
          .forEach(addTypeCasts(type));
    }
  }

  private Consumer<? super CtInvocation> addTypeCasts(CtType<?> type) {
    
    return v -> v.addTypeCast(createPropertyGetterReference(type));
  }

  private CtTypeReference<Object> createPropertyGetterReference(CtType<?> type) {
    return type.getFactory().createReference("spoon.reflect.annotations.PropertyGetter");
  }

  private Function<? super CtMethod<?>, ? extends Stream<? extends CtInvocation>> getAllInvocations() {
    return v -> v.getElements(new TypeFilter<>(CtInvocation.class)).stream();
  }
  
}

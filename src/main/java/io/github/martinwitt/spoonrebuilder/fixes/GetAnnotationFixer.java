package io.github.martinwitt.spoonrebuilder.fixes;

import java.util.ArrayList;
import java.util.List;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class GetAnnotationFixer extends AbstractProcessor<CtMethod<?>> {

    @Override
    public void process(CtMethod<?> element) {
        if (element.getSimpleName().equals("getAnnotation")
                && element.getDeclaringType().getSimpleName().equals("CtElementImpl")
                && hasClassParameter(element)) {
            element.getElements(new TypeFilter<>(CtReturn.class)).stream()
                    .map(CtReturn::getReturnedExpression)
                    .filter(v -> !v.toString().contains("null"))
                    .forEach(v -> {
                        List<CtTypeReference<?>> arrayList = new ArrayList<>();
                        var ref = element.getFactory().createTypeReference();
                        ref.setSimpleName("A");
                        arrayList.add(ref);
                        v.setTypeCasts(arrayList);
                    });
        }
    }

    private boolean hasClassParameter(CtMethod<?> element) {
        return element.getParameters().stream()
                .allMatch(v -> v.getType().getSimpleName().equals("Class"));
    }
}

package io.github.martinwitt.spoonrebuilder.fixes;

import java.util.List;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

public class GetActualClassProcessor extends AbstractProcessor<CtMethod<?>> {

    @Override
    public void process(CtMethod<?> method) {
        if (method.getSimpleName().equals("getActualClass")) {
            CtTypeReference<?> ref = method.getFactory().createReference(Class.class.getName());
            ref.setActualTypeArguments(List.of(method.getFactory().createWildcardReference()));
            method.setType((CtTypeReference) ref);
        }
    }
}

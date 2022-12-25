package io.github.martinwitt.spoonrebuilder.generics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.template.TemplateParameter;

/**
 * Removes the generics from all CtElements
 */
public class GenericTypeRemover extends AbstractProcessor<CtType<?>>
        implements BiFunction<String, CtType<?>, Optional<CtTypeReference<?>>> {
    private final Map<CtType<?>, Map<String, CtTypeReference<?>>> bounds = new HashMap<>();

    @Override
    public void process(CtType<?> element) {
        if (isSpoonType(element)
                && (isSubtypeOfCtElement(element)
                        || isSubtypeOfCtRHSReceiver(element)
                        || isCtTemplateParameter(element))
                && isNotCtLiteral(element)) {
            List<CtTypeParameter> formalCtTypeParameters = element.getFormalCtTypeParameters();
            for (CtTypeParameter parameter : formalCtTypeParameters) {
                CtTypeReference<?> superclass = parameter.getSuperclass();
                if (superclass != null && !superclass.equals(getFactory().Type().objectType())) {
                    bounds.computeIfAbsent(element, k -> new HashMap<>()).put(parameter.getSimpleName(), superclass);
                }
            }
            element.setFormalCtTypeParameters(new ArrayList<>());
        }
    }

    private boolean isCtTemplateParameter(CtType<?> element) {
        return element.isSubtypeOf(getFactory().createCtTypeReference(TemplateParameter.class));
    }

    private boolean isSubtypeOfCtRHSReceiver(CtType<?> element) {
        return element.isSubtypeOf(getFactory().createCtTypeReference(CtRHSReceiver.class));
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

    @Override
    public Optional<CtTypeReference<?>> apply(String s, CtType<?> ctType) {
        return Optional.ofNullable(bounds.get(ctType)).flatMap(tm -> Optional.ofNullable(tm.get(s)));
    }
}

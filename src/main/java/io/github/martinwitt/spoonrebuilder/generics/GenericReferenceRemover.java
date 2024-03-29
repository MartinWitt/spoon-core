package io.github.martinwitt.spoonrebuilder.generics;

import io.github.martinwitt.spoonrebuilder.spoon.SpoonUtils;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiFunction;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtExpression;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;

public class GenericReferenceRemover extends AbstractProcessor<CtTypeReference<?>> {

    private final BiFunction<String, CtType<?>, Optional<CtTypeReference<?>>> boundsAccessor;

    public GenericReferenceRemover(BiFunction<String, CtType<?>, Optional<CtTypeReference<?>>> boundsAccessor) {
        this.boundsAccessor = boundsAccessor;
    }

    @Override
    public void process(CtTypeReference<?> reference) {
        if (hasGenericParameter(reference)) {
            CtType<?> type = reference.getDeclaration();
            if (!type.isParameterized()) {
                reference.setActualTypeArguments(new ArrayList<>());
                markElementForSniperPrinting(reference);
                CtRole roleInParent = reference.getRoleInParent();
                if (roleInParent == CtRole.CAST || roleInParent == CtRole.TYPE_ARGUMENT) {
                    markElementForSniperPrinting(reference.getParent(CtMethod.class));
                    markElementForSniperPrinting(reference.getParent());
                }
            }
        } else if (reference instanceof CtTypeParameterReference tpr
                && !(tpr instanceof CtWildcardReference) // wildcard can be ignored
                && tpr.getDeclaration() == null // assumption: declaration was already deleted
        ) {
            boundsAccessor
                    .apply(reference.getSimpleName(), reference.getParent(CtType.class))
                    .ifPresentOrElse(
                            element -> {
                                process(element); // might have an outdated bound too
                                reference.replace(element);
                            },
                            () -> {
                                if (reference.getParent() instanceof CtTypeParameter tp) {
                                    SpoonUtils.removeSupertype(tp);
                                } else if (reference.getParent() instanceof CtMethod<?> m && m.getType() == reference) {
                                    reference.replace(
                                            reference.getFactory().Type().objectType());
                                } else if (reference.getParent() instanceof CtExpression<?> e
                                        && reference.getRoleInParent() == CtRole.CAST) {
                                    e.setTypeCasts(e.getTypeCasts().stream()
                                            .filter(ref -> ref != reference)
                                            .toList());
                                } else if (!(reference.getParent() instanceof CtExecutableReference<?>
                                        && reference.getRoleInParent() == CtRole.TYPE_ARGUMENT)) {
                                    reference.replace(reference.getFactory().createWildcardReference());
                                }
                            });
        }
    }

    private boolean hasGenericParameter(CtTypeReference<?> reference) {
        return reference.getPackage() != null
                && reference.getDeclaration() != null
                && !reference.getActualTypeArguments().isEmpty();
    }

    /**
     * Modify an element such that the sniper printer detects it as modified, without changing its final content. This
     * forces it to be sniper-printed "as-is".
     */
    private static void markElementForSniperPrinting(CtElement element) {
        if (element == null) {
            return;
        }
        SourcePosition pos = element.getPosition();
        element.setPosition(SourcePosition.NOPOSITION);
        element.setPosition(pos);
    }
}

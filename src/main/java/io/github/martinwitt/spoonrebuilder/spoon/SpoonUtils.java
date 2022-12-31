package io.github.martinwitt.spoonrebuilder.spoon;

import spoon.reflect.declaration.CtType;

public class SpoonUtils {
    private SpoonUtils() {}

    @SuppressWarnings("nullness")
    public static void removeSupertype(CtType<?> type) {
        type.setSuperclass(null);
    }
}

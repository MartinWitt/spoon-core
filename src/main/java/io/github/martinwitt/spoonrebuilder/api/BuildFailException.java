package io.github.martinwitt.spoonrebuilder.api;

public class BuildFailException extends IllegalArgumentException {

    public BuildFailException(String string) {
        super(string);
    }
}

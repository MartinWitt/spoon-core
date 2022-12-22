package io.github.martinwitt.spoonrebuilder.fixes;

import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class CastSniperFixer implements UnaryOperator<String> {
    private final Set<String> names;
    private final Pattern ctLiteral = Pattern.compile("\\(\\(CtLiteral(<.+>)\\)\\)");
    /**
     * @param types
     */
    public CastSniperFixer(Iterable<CtType<?>> types) {
        this.names = StreamSupport.stream(types.spliterator(), false)
                .flatMap(v -> v.getReferencedTypes().stream())
                .map(CtTypeReference::getSimpleName)
                .collect(Collectors.toSet());
        for (char alphabet : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
            names.add(Character.toString(alphabet).toUpperCase());
        }
    }

    @Override
    public String apply(String s) {
        String content = s;

        for (String type : names) {
            if (type.equals("CtLiteral")) {
                Matcher matcher = ctLiteral.matcher(content);
                if (matcher.find()) {
                    if (matcher.group(1) != null) {
                        content = matcher.replaceFirst("(" + type + matcher.group(1) + ")");
                    }
                }
                continue;
            }
            content = content.replaceAll("abstract// an abstract class", "// an abstract class");
            content = content.replaceAll("\\(\\(" + type + "(?:<.*>)?" + "\\)\\)", "(" + type + ")");
            content = content.replace("finalspoon", "final spoon");
            content = content.replace("abstractclass", "abstract class");
        }
        return content;
    }
}

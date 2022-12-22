package io.github.martinwitt.spoonrebuilder.fixes;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class GenericTypeArgumentsFixer implements UnaryOperator<String> {
    private static final Pattern INNER_PATTERN = Pattern.compile("<(\\w+\\s*,\\s*)*?(Ct\\w+)<[^>]+>>");

    @Override
    public String apply(String s) {
        var matcher = INNER_PATTERN.matcher(s);
        if (!matcher.find()) {
            return s;
        }
        if (matcher.group(1) != null) {
            return INNER_PATTERN.matcher(s).replaceAll("<$1 $2>");
        }
        return INNER_PATTERN.matcher(s).replaceAll("<$2>");
    }
}

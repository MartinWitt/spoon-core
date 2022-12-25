package io.github.martinwitt.spoonrebuilder.fixes;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record FileFixer(Function<String, String> function) implements Consumer<Path> {
    private static final Logger logger =
            LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void accept(Path path) {
        if (!Files.isDirectory(path) && isJavaFile(path)) {
            try {
                String content = Files.readString(path);
                Files.writeString(path, function.apply(content), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                logger.atError().withThrowable(e).log("Error while fixing file {}", path);
            }
        }
    }

    private boolean isJavaFile(Path path) {
        return path.getFileName().toString().endsWith("java");
    }
}

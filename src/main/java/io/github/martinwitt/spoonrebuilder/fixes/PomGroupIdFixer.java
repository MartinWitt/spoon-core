package io.github.martinwitt.spoonrebuilder.fixes;

import com.google.errorprone.annotations.Var;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PomGroupIdFixer {

    private static final Logger logger =
            LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private static final String ARTIFACT_ID_SPOON_CORE = "<artifactId>spoon-core</artifactId>";
    private static final String GROUP_ID = "<groupId>com.github.martinwitt</groupId>";

    public void fixPom(List<Path> files) {
        files.stream().filter(this::isPom).forEach(f -> {
            try {
                @Var String content = Files.readString(f);
                content = content.replace(ARTIFACT_ID_SPOON_CORE, GROUP_ID + "\n" + ARTIFACT_ID_SPOON_CORE);
                Files.write(f, content.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                logger.atError().withThrowable(e).log("Error while fixing pom.xml");
            }
        });
    }

    private boolean isPom(Path path) {
        return path.getFileName() != null && path.getFileName().toString().equals("pom.xml");
    }
}

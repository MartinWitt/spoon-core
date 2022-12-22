package io.github.martinwitt.spoonrebuilder.fixes;

import io.quarkus.logging.Log;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PomGroupIdFixer {

    private static final String ARTIFACT_ID_SPOON_CORE = "<artifactId>spoon-core</artifactId>";
    private final String groupId = "<groupId>com.github.martinwitt</groupId>";

    public void fixPom(List<Path> files) {
        files.stream().filter(f -> f.getFileName().toString().equals("pom.xml")).forEach(f -> {
            try {
                String content = Files.readString(f);
                content = content.replace(ARTIFACT_ID_SPOON_CORE, groupId + "\n" + ARTIFACT_ID_SPOON_CORE);

                Files.write(f, content.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                Log.error("Error while fixing pom.xml", e);
            }
        });
    }
}

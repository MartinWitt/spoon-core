package io.github.martinwitt.spoonrebuilder.api;

import java.nio.file.Path;

/**
 * Checks if the result the rebuild. This is done by executing a build.
 * Afterwards the result folder <b>must</b> be cleaned up.
 */
public interface ResultChecker {
    void checkBuildResult(Path projectPath) throws BuildFailException;
}

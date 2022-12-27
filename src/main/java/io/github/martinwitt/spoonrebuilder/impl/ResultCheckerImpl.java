package io.github.martinwitt.spoonrebuilder.impl;

import io.github.martinwitt.spoonrebuilder.api.BuildFailException;
import io.github.martinwitt.spoonrebuilder.api.ResultChecker;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import javax.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Checks if the result of the SpoonRebuilder is correct. Checking the build result is done by executing a maven build with {@code mvn clean package}.
 * If the build fails, an @{code IllegalStateException} is thrown. If the build succeeds, the result is cleaned up with {@code mvn clean}.
 */
@ApplicationScoped
public class ResultCheckerImpl implements ResultChecker {
    private static final Logger logger =
            LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean IS_WINDOWS =
            System.getProperty("os.name").toLowerCase().startsWith("windows");

    private Process mvnPackage(Path projectPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (IS_WINDOWS) {
            return processBuilder
                    .command("powershell.exe", "/C", "mvn", "clean", "package", "-DskipDepClean=true")
                    .directory(projectPath.toFile())
                    .inheritIO()
                    .start();
        } else {
            return processBuilder
                    .command("mvn", "clean", "package", "-DskipDepClean=true")
                    .directory(projectPath.toFile())
                    .inheritIO()
                    .start();
        }
    }
    /**
     * Cleans the project in result folder with {@code mvn clean}.
     * @param projectPath
     * @return  the started process of the maven clean command
     * @throws IOException  if an I/O error occurs
     */
    private Process mvnClean(Path projectPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        // check if the OS is windows
        if (IS_WINDOWS) {
            return processBuilder
                    .command("powershell.exe", "/C", "mvn", "clean", "-DskipDepClean=true")
                    .directory(projectPath.toFile())
                    .inheritIO()
                    .start();
        } else {
            return processBuilder
                    .command("mvn", "clean", "-DskipDepClean=true")
                    .directory(projectPath.toFile())
                    .inheritIO()
                    .start();
        }
    }

    @Override
    public void checkBuildResult(Path projectPath) throws BuildFailException {
        logger.atInfo().log("Checking result");
        try {
            int result = mvnPackage(projectPath).waitFor();
            if (result != 0) {
                throw new IllegalStateException("Maven build failed");
            }
            mvnClean(projectPath).waitFor();
        } catch (IOException | InterruptedException e) {
            logger.atError().withThrowable(e).log("Error while checking result");
            throw new BuildFailException("Maven build failed");
        }
    }
}

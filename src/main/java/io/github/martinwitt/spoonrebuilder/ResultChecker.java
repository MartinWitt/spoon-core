package io.github.martinwitt.spoonrebuilder;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Checks if the result of the SpoonRebuilder is correct. Checking the build result is done by executing a maven build with {@code mvn clean package}.
 * If the build fails, an @{code IllegalStateException} is thrown. If the build succeeds, the result is cleaned up with {@code mvn clean}.
 */
public class ResultChecker {
    private static final Logger logger =
            LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private final Path resultFolderPath;
    private static final boolean IS_WINDOWS =
            System.getProperty("os.name").toLowerCase().startsWith("windows");

    public ResultChecker(Path resultFolderPath) {
        this.resultFolderPath = resultFolderPath;
    }

    /**
     * Checks if the result of the SpoonRebuilder is correct.
     * Checking the build result is done by executing a maven build with {@code mvn clean package}.
     * If the build fails, an @{code IllegalStateException} is thrown.
     * If the build succeeds, the result is cleaned up with {@code mvn clean}.
     *
     * @throws IllegalStateException if the result is not correct
     */
    public void checkBuildResult() throws IllegalStateException {
        logger.atInfo().log("Checking result");
        try {
            int result = mvnPackage().waitFor();
            if (result != 0) {
                throw new IllegalStateException("Maven build failed");
            }
            mvnClean().waitFor();
        } catch (IOException | InterruptedException e) {
            logger.atError().withThrowable(e).log("Error while checking result");
            throw new IllegalStateException("Maven build failed");
        }
    }

    private Process mvnPackage() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (IS_WINDOWS) {
            return processBuilder
                    .command("powershell.exe", "/C", "mvn", "clean", "package", "-DskipDepClean=true")
                    .directory(resultFolderPath.toFile())
                    .inheritIO()
                    .start();
        } else {
            return processBuilder
                    .command("mvn", "clean", "package", "-DskipDepClean=true")
                    .directory(resultFolderPath.toFile())
                    .inheritIO()
                    .start();
        }
    }
    /**
     * Cleans the project in result folder with {@code mvn clean}.
     * @return  the started process of the maven clean command
     * @throws IOException  if an I/O error occurs
     */
    private Process mvnClean() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        // check if the OS is windows
        if (IS_WINDOWS) {
            return processBuilder
                    .command("powershell.exe", "/C", "mvn", "clean", "-DskipDepClean=true")
                    .directory(resultFolderPath.toFile())
                    .inheritIO()
                    .start();
        } else {
            return processBuilder
                    .command("mvn", "clean", "-DskipDepClean=true")
                    .directory(resultFolderPath.toFile())
                    .inheritIO()
                    .start();
        }
    }
}

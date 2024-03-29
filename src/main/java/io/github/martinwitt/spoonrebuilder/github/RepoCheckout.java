package io.github.martinwitt.spoonrebuilder.github;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

public class RepoCheckout implements AutoCloseable {

    private static final Logger logger =
            LogManager.getLogger(MethodHandles.lookup().lookupClass());
    private String repoURL;
    private Path outputPath;

    @SuppressWarnings({"initialization", "NullAway"})
    private Git git;

    private RevCommit revCommit;

    public RepoCheckout(String repoURL, Path outputPath, RevCommit revCommit) {
        this.repoURL = Objects.requireNonNull(repoURL);
        this.outputPath = Objects.requireNonNull(outputPath);
        this.revCommit = revCommit;
    }

    private void cloneRepo() {
        try {
            git = Git.cloneRepository()
                    .setURI(repoURL)
                    .setDirectory(outputPath.toFile())
                    .call();
            git.checkout().setName(revCommit.getName()).call();
        } catch (GitAPIException e) {
            logger.atError().withThrowable(e).log("Error while cloning repo");
        }
    }

    @Override
    public void close() {
        if (git != null) {
            git.close();
        }
    }

    @Nullable
    public RevCommit getCurrentCommit() {
        if (git == null) {
            cloneRepo();
            if (git == null) {
                return null;
            }
        }
        try {
            return git.log().call().iterator().next();
        } catch (GitAPIException e) {
            logger.atError().withThrowable(e).log("Error while getting current commit");
            return null;
        }
    }
}

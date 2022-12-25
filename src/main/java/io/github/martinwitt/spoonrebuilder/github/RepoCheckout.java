package io.github.martinwitt.spoonrebuilder.github;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

public class RepoCheckout {

    private static final Logger logger =
            LogManager.getLogger(MethodHandles.lookup().lookupClass());
    private String repoURL;
    private Path outputPath;
    private Git git;

    public RepoCheckout(String repoURL, Path outputPath, RevCommit revCommit) {
        this.repoURL = repoURL;
        this.outputPath = outputPath;
        cloneRepo(revCommit);
    }

    private void cloneRepo(RevCommit revCommit) {
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

    public void close() {
        git.close();
    }

    public RevCommit getCurrentCommit() {
        try {
            return git.log().call().iterator().next();
        } catch (GitAPIException e) {
            logger.atError().withThrowable(e).log("Error while getting current commit");
            return null;
        }
    }
}

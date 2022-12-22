package io.github.martinwitt.spoonrebuilder.github;

import java.nio.file.Path;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class RepoCheckout {

    private String repoURL;
    private Path outputPath;
    private Git git;

    public RepoCheckout(String repoURL, Path outputPath) {
        this.repoURL = repoURL;
        this.outputPath = outputPath;
        cloneRepo();
    }

    private void cloneRepo() {
        try {
            git = Git.cloneRepository()
                    .setURI(repoURL)
                    .setDirectory(outputPath.toFile())
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        git.close();
    }
}

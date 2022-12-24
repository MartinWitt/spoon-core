package io.github.martinwitt.spoonrebuilder.github;

import java.nio.file.Path;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

public class RepoCheckout {

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

            e.printStackTrace();
        }
    }

    public void close() {
        git.close();
    }
}

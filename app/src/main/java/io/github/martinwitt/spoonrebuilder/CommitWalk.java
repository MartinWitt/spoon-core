package io.github.martinwitt.spoonrebuilder;

import java.nio.file.Path;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitWalk {
  
  private String repoURL;
  private Path outputPath;
  private Git git;

  public CommitWalk(String repoURL, Path outputPath) {
    this.repoURL = repoURL;
    this.outputPath = outputPath;
    cloneRepo();
  }
  private void cloneRepo() {
    try {
      git = Git.cloneRepository().setURI(repoURL).setDirectory(outputPath.toFile()).call();
    } catch (GitAPIException e) {
      e.printStackTrace();
    }
  }

  public RevCommit checkoutNextCommit() {
    try {
      return git.log().call().iterator().next();
    } catch (GitAPIException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void commitFilesToBranch() {
    try {
      git.add().addFilepattern(".").setUpdate(true).call();
      git.checkout().setCreateBranch(true).setName("rebuild").call();
      git.commit().setAuthor("SpoonRebuild", "empty").call();
    } catch (GitAPIException e) {
      e.printStackTrace();
    }
  }
}

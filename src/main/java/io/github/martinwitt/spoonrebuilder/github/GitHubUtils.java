package io.github.martinwitt.spoonrebuilder.github;

import io.quarkus.logging.Log;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitHubUtils {

    private GitHubUtils() {}

    private static final int NEW_COMMIT_LIMIT = 10;
    private static final String INRIA_SPOON = "https://github.com/INRIA/spoon";
    private static final String MARTINWITT_SPOON = "https://github.com/martinwitt/spoon-core";

    public static String getRepoName(String repoURL) {
        return repoURL.substring(repoURL.lastIndexOf('/') + 1);
    }

    public static String getRepoOwner(String repoURL) {
        return repoURL.substring(repoURL.lastIndexOf('/') - 1);
    }

    public static Collection<Ref> getTagsOfRepo(String repoUrl) {
        try {
            return Git.lsRemoteRepository().setTags(true).setRemote(repoUrl).call();
        } catch (GitAPIException e) {
            Log.error(e);
        }
        return List.of();
    }

    public static Collection<RevCommit> getCommitsOfRepo(String repoUrl, int limit) {
        File gitFolder = new File("./spoon-git");
        try (Closeable c = () -> FileUtils.deleteDirectory(gitFolder);
                Git git = Git.cloneRepository()
                        .setURI(repoUrl)
                        .setDirectory(gitFolder)
                        .call()) {
            Iterable<RevCommit> revIterator = git.log().all().call();
            return StreamSupport.stream(revIterator.spliterator(), false)
                    .limit(limit)
                    .toList();
        } catch (GitAPIException | IOException e) {
            Log.error(e);
        }
        return List.of();
    }

    public static List<RevCommit> getNewCommitsForSpoon() {
        var tags = GitHubUtils.getTagsOfRepo(MARTINWITT_SPOON).stream()
                .map(GitHubUtils::getCommitHashOfTag)
                .toList();
        var commits = GitHubUtils.getCommitsOfRepo(INRIA_SPOON, NEW_COMMIT_LIMIT);
        return commits.stream().filter(v -> !tags.contains(v.getName())).toList();
    }

    private static String getCommitHashOfTag(Ref revCommit) {
        return revCommit.getName().split("/")[2];
    }
}

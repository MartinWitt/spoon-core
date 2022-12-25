package io.github.martinwitt.spoonrebuilder.github;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitHubUtils {

    private static final Logger logger =
            LogManager.getLogger(MethodHandles.lookup().lookupClass());

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
            logger.atError().withThrowable(e).log("Error while getting tags of repo");
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
            logger.atError().withThrowable(e).log("Error while getting commits of repo");
        }
        return List.of();
    }

    public static List<RevCommit> getNewCommitsForSpoon() {
        var tags = GitHubUtils.getTagsOfRepo(MARTINWITT_SPOON).stream()
                .map(GitHubUtils::getCommitHashOfTag)
                .toList();
        logger.atInfo().log("Found " + tags.size() + " tags");
        tags.forEach(logger::info);
        var commits = GitHubUtils.getCommitsOfRepo(INRIA_SPOON, NEW_COMMIT_LIMIT);
        logger.atInfo().log("Found " + commits.size() + " commits");
        commits.stream().filter(v -> !tags.contains(v.getName())).toList().forEach(logger::info);
        return commits.stream().filter(v -> !tags.contains(v.getName())).toList();
    }

    private static String getCommitHashOfTag(Ref revCommit) {
        return revCommit.getName().split("/")[2];
    }
}

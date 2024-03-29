package io.github.martinwitt.spoonrebuilder.github;

import io.github.martinwitt.spoonrebuilder.SpoonRebuilder;
import io.github.martinwitt.spoonrebuilder.api.ReleaseService;
import io.github.martinwitt.spoonrebuilder.api.ResultChecker;
import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubaction.Inputs;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@SuppressWarnings("initialization")
public class GitHubAction {

    private static final Logger logger =
            LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private ResultChecker resultChecker;
    private ReleaseService releaseService;

    @Inject
    @ConfigProperty(name = "rebuilder.repoUrl")
    String repoUrl;

    @Inject
    @ConfigProperty(name = "rebuilder.outputFolder")
    String outputFolder;

    @Inject
    @ConfigProperty(name = "rebuilder.gitFolder")
    String gitFolder;

    @Inject
    @ConfigProperty(name = "rebuilder.upstream-repo")
    String upstreamRepo;

    @Inject
    @ConfigProperty(name = "rebuilder.upstream-branch")
    String upstreamBranch;

    @SuppressWarnings("NullAway")
    String githubToken;

    private static final String TEMP_PATH_SPOON = "./spoon_git";

    GitHubAction(ResultChecker resultChecker, ReleaseService releaseService) {
        this.resultChecker = resultChecker;
        this.releaseService = releaseService;
    }

    @Action
    public void rebuildSpoon(Inputs inputs, Commands commands) {
        commands.notice("Rebuild Spoon");
        Path gitFolderForSpoon = Path.of(TEMP_PATH_SPOON);
        inputs.get("token").ifPresent(token -> githubToken = token);
        if (githubToken == null) {
            commands.error("No token found");
            return;
        }
        var commits = GitHubUtils.getNewCommitsForSpoon();
        commands.notice(
                """
                # Rebuild Spoon
                -------------
                ## New Commits
                %s
            """
                        .formatted(commits.stream().map(RevCommit::getName).collect(Collectors.joining("\n"))));
        if (commits.isEmpty()) {
            commands.notice("No new commits found");
            return;
        }
        for (RevCommit revCommit : commits) {
            commands.group("Rebuild Spoon for commit " + revCommit.getName());
            Path outputPath = Path.of(outputFolder);
            boolean refactorSuccess = refactorRepo(gitFolderForSpoon, outputPath, commands, revCommit);
            if (!refactorSuccess) {
                commands.error("Refactoring failed");
                commands.endGroup();
                continue;
            }
            boolean buildCheck = checkBuildResult(outputPath, commands);
            if (!buildCheck) {
                commands.error("Build check failed");
                commands.endGroup();
                continue;
            }
            pushResultToGitHub(outputPath, commands, revCommit);
            commands.endGroup();
        }
    }

    private boolean refactorRepo(Path gitFolderForSpoon, Path outputPath, Commands commands, RevCommit revCommit) {
        try (RepoCheckout repo = new RepoCheckout(repoUrl, gitFolderForSpoon, revCommit);
                Closeable c = () -> FileUtils.deleteDirectory(gitFolderForSpoon.toFile())) {
            RevCommit currentCommit = repo.getCurrentCommit();
            if (currentCommit == null) {
                commands.error("No current commit found");
                return false;
            }
            commands.notice("Repo on commit " + currentCommit.getName());
            SpoonRebuilder spoonRebuilder = new SpoonRebuilder(gitFolderForSpoon, outputPath);
            spoonRebuilder.rebuild();
            FileUtils.deleteDirectory(gitFolderForSpoon.toFile());
            // Check the build result and push it to the server
        } catch (Exception e) {
            commands.error("Error while rebuilding Spoon");
            commands.error(getErrorMessage(e));
            logger.atInfo().withThrowable(e).log("Error while rebuilding Spoon");
            return false;
        }
        return true;
    }
    /**
     * Returns the error message of the exception. If the message is null, it returns an empty string.
     * @param e The exception.
     * @return  The error message of the exception or an empty string.
     */
    private String getErrorMessage(Exception e) {
        return e.getMessage() == null ? "" : e.getMessage();
    }

    /**
     * This method checks the build result.
     * It calls the ResultChecker class to check the result.
     * If the result is correct, it calls the pushResult method.
     * @param outputPath The path of the output.
     * @param commands
     */
    private boolean checkBuildResult(Path outputPath, Commands commands) {
        try {
            commands.notice("Checking build result");
            commands.group("Maven build");
            resultChecker.checkBuildResult(outputPath);
            commands.endGroup();
            commands.notice("Build result is correct");
            return true;
        } catch (Exception e) {
            commands.endGroup();
            commands.error("Error while checking build result" + e);
            logger.atError().withThrowable(e).log("Error while checking build result");
            return false;
        }
    }

    private boolean pushResultToGitHub(Path outputPath, Commands commands, RevCommit commit) {
        commands.notice("Pushing result to GitHub");
        File gitMergeFolder = new File("./spoon-merged");
        try (Closeable c = () -> FileUtils.deleteDirectory(gitMergeFolder);
                Closeable output = () -> FileUtils.deleteDirectory(outputPath.toFile());
                Git git = mergeResult(outputPath, gitMergeFolder)) {
            createCommit(commit, git);
            // create a tag with the current date
            CredentialsProvider credentialsProvider =
                    new UsernamePasswordCredentialsProvider("martinWitt", githubToken);
            createTag(commit, git, credentialsProvider);
            // push the changes to the upstream repository
            gitPushResults(commands, git, credentialsProvider);
            releaseService.createRelease(githubToken);
            commands.notice("Commit " + commit.getName() + " was pushed to GitHub and a release created\n");
            return true;
        } catch (Exception e) {
            commands.error("Error while pushing result to GitHub");
            commands.error(getErrorMessage(e));
            logger.atError().withThrowable(e).log("Error while pushing result to GitHub");
            return false;
        }
    }

    private void gitPushResults(Commands commands, Git git, CredentialsProvider credentialsProvider)
            throws GitAPIException {
        git.push()
                .setCredentialsProvider(credentialsProvider)
                .setPushTags()
                .call()
                .forEach(v -> commands.notice(v.getMessages()));
    }

    private void createCommit(RevCommit commit, Git git) throws GitAPIException {
        git.commit()
                .setMessage("Rebuild Spoon: " + commit.getName())
                .setSign(false)
                .call();
    }

    private Git mergeResult(Path outputPath, File gitMergeFolder) throws GitAPIException, IOException {
        // clone the upstream repository and checkout the upstream branch
        Git git = Git.cloneRepository()
                .setURI(upstreamRepo)
                .setBranch(upstreamBranch)
                .setDirectory(gitMergeFolder)
                .call();
        // copy the output of the spoon model to the git repository
        FileUtils.copyDirectory(outputPath.toFile(), gitMergeFolder);
        git.add().addFilepattern(".").call();
        return git;
    }

    private void createTag(RevCommit commit, Git git, CredentialsProvider credentialsProvider) throws GitAPIException {
        git.tag()
                .setName("commit-" + commit.getName())
                .setMessage("Rebuild Spoon for commit hash: " + commit.getName())
                .setCredentialsProvider(credentialsProvider)
                .call();
    }
}

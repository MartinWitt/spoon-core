package io.github.martinwitt.spoonrebuilder.github;

import io.github.martinwitt.spoonrebuilder.ResultChecker;
import io.github.martinwitt.spoonrebuilder.SpoonRebuilder;
import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubaction.Context;
import io.quarkiverse.githubaction.Inputs;
import io.quarkiverse.githubaction.Outputs;
import io.quarkus.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GitHubAction {

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

    String githubToken;

    private static final String TEMP_PATH_SPOON = "./spoon_git";

    @Action
    public void rebuildSpoon(Inputs inputs, Outputs outputs, Context context, Commands commands) {
        commands.notice("Rebuild Spoon");
        Path gitFolderForSpoon = Path.of(TEMP_PATH_SPOON);
        inputs.get("token").ifPresent(token -> githubToken = token);
        var commits = GitHubUtils.getNewCommitsForSpoon();
        commands.jobSummary(
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
            refactorRepo(gitFolderForSpoon, commands, revCommit);
        }
    }

    void refactorRepo(Path gitFolderForSpoon, Commands commands, RevCommit revCommit) {
        try {
            RepoCheckout repo = new RepoCheckout(repoUrl, gitFolderForSpoon, revCommit);
            Path outputPath = Path.of(outputFolder);
            SpoonRebuilder spoonRebuilder = new SpoonRebuilder(gitFolderForSpoon, outputPath);
            spoonRebuilder.rebuild();
            repo.close();
            FileUtils.deleteDirectory(gitFolderForSpoon.toFile());
            // Check the build result and push it to the server
            checkBuildResult(outputPath, commands, revCommit);
        } catch (Exception e) {
            commands.error("Error while rebuilding Spoon");
            commands.error(e.getMessage());
            Log.error("Error while rebuilding Spoon", e);
        }
    }

    /**
     * This method checks the build result.
     * It calls the ResultChecker class to check the result.
     * If the result is correct, it calls the pushResult method.
     * @param outputPath The path of the output.
     * @param commands
     */
    private void checkBuildResult(Path outputPath, Commands commands, RevCommit commit) {
        try {
            commands.notice("Checking build result");
            commands.group("Maven build");
            new ResultChecker(outputPath).check();
            commands.endGroup();
            commands.notice("Build result is correct");
            pushResult(outputPath, commands, commit);
        } catch (Exception e) {
            commands.endGroup();
            commands.error("Error while checking build result" + e);
            Log.error("Error while checking build result", e);
        }
    }

    private void pushResult(Path outputPath, Commands commands, RevCommit commit) throws GitAPIException, IOException {
        commands.notice("Pushing result to GitHub");
        File gitFolder = new File("./spoon-merged");
        // clone the upstream repository and checkout the upstream branch
        Git git = Git.cloneRepository()
                .setURI(upstreamRepo)
                .setBranch(upstreamBranch)
                .setDirectory(gitFolder)
                .call();
        git.checkout().setName("rebuild").call();
        // copy the output of the spoon model to the git repository
        FileUtils.copyDirectory(outputPath.toFile(), gitFolder);
        git.add().addFilepattern(".").call();
        git.commit()
                .setMessage("Rebuild Spoon " + commit.getName())
                .setSign(false)
                .call();
        // create a tag with the current date
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("martinWitt", githubToken);
        git.tag()
                .setName(commit.getName())
                .setCredentialsProvider(credentialsProvider)
                .call();
        // push the changes to the upstream repository
        git.push()
                .setRemote(upstreamRepo)
                .setCredentialsProvider(credentialsProvider)
                .setPushTags()
                .call();
        git.close();
        FileUtils.deleteDirectory(gitFolder);
        commands.appendJobSummary("Rebuild Spoon " + commit.getName() + " was pushed to GitHub\n");
    }
}

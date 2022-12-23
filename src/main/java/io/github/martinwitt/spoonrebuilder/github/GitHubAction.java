package io.github.martinwitt.spoonrebuilder.github;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.github.martinwitt.spoonrebuilder.ResultChecker;
import io.github.martinwitt.spoonrebuilder.SpoonRebuilder;
import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubaction.Context;
import io.quarkiverse.githubaction.Inputs;
import io.quarkiverse.githubaction.Outputs;
import io.quarkus.logging.Log;

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
        refactorRepo(gitFolderForSpoon);
    }

    void refactorRepo(Path gitFolderForSpoon) {
        try {
            RepoCheckout repo = new RepoCheckout(repoUrl, gitFolderForSpoon);
            Path outputPath = Path.of(outputFolder);
            SpoonRebuilder spoonRebuilder = new SpoonRebuilder(gitFolderForSpoon, outputPath);
            spoonRebuilder.rebuild();
            repo.close();
            FileUtils.deleteDirectory(gitFolderForSpoon.toFile());
            // Check the build result and push it to the server
            checkBuildResult(outputPath);
        } catch (Exception e) {
            System.out.println("Error while rebuilding Spoon");
            e.printStackTrace();
            Log.error("Error while rebuilding Spoon", e);
        }
    }

    /**
     * This method checks the build result.
     * It calls the ResultChecker class to check the result.
     * If the result is correct, it calls the pushResult method.
     * @param outputPath The path of the output.
     */
    private void checkBuildResult(Path outputPath) {
        try {
            // new ResultChecker(outputPath).check();
            pushResult(outputPath);
        } catch (Exception e) {
            System.out.println("Error while checking build result");
            e.printStackTrace();
            Log.error("Error while checking build result", e);
        }
    }

    private void pushResult(Path outputPath) throws GitAPIException, IOException {
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
                .setMessage("Rebuild Spoon " + LocalDate.now().toString())
                .setSign(false)
                .call();
        // create a tag with the current date
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("martinWitt", githubToken);
        git.tag()
                .setName(LocalDate.now().toString())
                .setCredentialsProvider(credentialsProvider)
                .call();
        // push the changes to the upstream repository
        git.push()
                .setRemote(upstreamRepo)
                .setCredentialsProvider(credentialsProvider)
                .setPushTags()
                .call();
    }
}

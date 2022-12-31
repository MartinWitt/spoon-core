package io.github.martinwitt.spoonrebuilder.impl;

import io.github.martinwitt.spoonrebuilder.api.ReleaseService;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;
import javax.enterprise.context.ApplicationScoped;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.annotations.Nullable;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

@ApplicationScoped
public class ReleaseServiceImpl implements ReleaseService {

    private static final String INRIA_SPOON = "https://github.com/INRIA/spoon";
    private static final Logger logger =
            LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @SuppressWarnings({"NullAway.Init", "initialization"})
    private Git git;

    @Override
    public void createRelease(String githubToken) {
        File gitFolder = new File("./spoon-git");
        try (Closeable c2 = () -> git.close();
                Closeable c = () -> FileUtils.deleteDirectory(gitFolder); ) {
            cloneRepo(gitFolder);
            Ref tag = getNewestTag();
            if (tag == null) {
                logger.atError().log("No tags found");
                return;
            }
            ObjectId objectId = tag.getTarget().getObjectId();
            if (objectId == null) {
                logger.atError().log("Commit id is null");
                return;
            }
            List<RevCommit> commitsTillTag = getCommitsTillTag(objectId.getName());
            if (commitsTillTag.isEmpty()) {
                logger.atError().log("No commits found");
                return;
            }
            String changeLog = createChangeLog(commitsTillTag).toString();
            GHRepository repo = GitHub.connect("martinWitt", githubToken).getRepository("martinwitt/spoon-core");
            RevCommit newestCommit = commitsTillTag.get(0);
            String md =
                    """
           # Release Notes:
           %s
           # Meta-Data
           Rebuild Spoon for commit hash:  %s
           Rebuilder hash: %s
           Date: %s
           Time: %s
           """
                            .formatted(
                                    changeLog,
                                    newestCommit.getName(),
                                    repo.getBranch("master").getSHA1(),
                                    LocalDate.now(),
                                    LocalTime.now());
            repo.createRelease("commit-" + newestCommit.getName()).body(md).create();
        } catch (Exception e) {
            logger.atError().withThrowable(e).log("Error while creating release");
        }
    }

    private StringBuilder createChangeLog(List<RevCommit> commitsTillTag) {
        StringBuilder sb = new StringBuilder();
        for (RevCommit revCommit : commitsTillTag) {
            sb.append("- " + revCommit.getShortMessage() + " "
                    + "[%s](https://github.com/INRIA/spoon/commit/%s)%n".formatted("GitHub", revCommit.getName()));
        }
        return sb;
    }

    private List<RevCommit> getCommitsTillTag(String tagSha) {
        try {
            Iterable<RevCommit> revIterator = git.log().all().call();
            return StreamSupport.stream(revIterator.spliterator(), false)
                    .takeWhile(v -> !v.getId().getName().equals(tagSha))
                    .toList();
        } catch (GitAPIException | IOException e) {
            logger.atError().withThrowable(e).log("Error while getting commits of repo");
            return Collections.emptyList();
        }
    }

    @Nullable
    private Ref getNewestTag() {
        try {
            var list = git.tagList().call();
            try (RevWalk walk = new RevWalk(git.getRepository())) {
                Collections.sort(list, Comparator.comparing(v -> getCommitDate(v, walk)));
                return list.get(list.size() - 1);
            }
        } catch (GitAPIException e) {
            logger.atError().withThrowable(e).log("Error while getting tags of repo");
        }
        return null;
    }

    private void cloneRepo(File gitFolder) throws GitAPIException {
        git = Git.cloneRepository().setURI(INRIA_SPOON).setDirectory(gitFolder).call();
    }

    private int getCommitDate(Ref ref, RevWalk walk) {

        try {
            ObjectId objectId = ref.getObjectId();
            if (objectId == null) {
                return 0;
            }
            return walk.parseCommit(objectId).getCommitTime();
        } catch (IOException e) {
            logger.atError().withThrowable(e).log("Error while getting commit date");
            return 0;
        }
    }
}

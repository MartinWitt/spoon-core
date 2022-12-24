package io.github.martinwitt.spoonrebuilder.github;

import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.Test;

public class GitHubUtilsTest {
    @Test
    void testGetTagsOfRepo() {
        var set = GitHubUtils.getTagsOfRepo("https://github.com/martinwitt/spoon-core").stream()
                .map(this::getCommitHashOfTag)
                .toList();
        set.forEach(System.out::println);
        // var list = GitHubUtils.getCommitsOfRepo("https://github.com/INRIA/spoon", 20);
        //
        // // for (RevCommit revCommit : list) {
        // //   System.out.println(revCommit.getName());
        // // }
        // var newCommits = list.stream().filter(v -> !set.contains(v.getName())).toList();
        // newCommits.forEach(System.out::println);
    }

    private String getCommitHashOfTag(Ref revCommit) {
        return revCommit.getName().split("/")[2];
    }
}

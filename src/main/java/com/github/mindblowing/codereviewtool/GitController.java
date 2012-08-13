package com.github.mindblowing.codereviewtool;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/git")
public class GitController {
    @Resource
    private String gitFolder;

    private Repository repository;
    private Git git;
    private RevWalk walk;

    @PostConstruct
    public void populateData() throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        repository = builder.setGitDir(new File(gitFolder))
                .readEnvironment()
                .findGitDir()
                .build();

        git = new Git(repository);
        walk = new RevWalk(repository);
    }

    @RequestMapping("/commits.html")
    public String commits(Model model) throws IOException, GitAPIException {
        Iterable<RevCommit> log = git.log().call();

        model.addAttribute("log", log);

        return "git/commits";
    }

    @RequestMapping("/commit/{commitId}.html")
    public String commit(Model model, @PathVariable String commitId) throws IOException, GitAPIException {
        RevCommit commit = walk.parseCommit(ObjectId.fromString(commitId));
        model.addAttribute("commit", commit);

        if (commit.getParentCount() > 0) {
            ObjectReader reader = repository.newObjectReader();

            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();

            newTreeIter.reset(reader, walk.parseTree(commit.getId()));
            oldTreeIter.reset(reader, walk.parseTree(commit.getParent(0).getId()));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            model.addAttribute("diffEntries", git.diff().setOutputStream(outputStream).setNewTree(newTreeIter).setOldTree(oldTreeIter).call());
            model.addAttribute("out", outputStream.toString());
        }

        return "git/commit";
    }
}

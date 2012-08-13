package com.github.mindblowing.codereviewtool;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Controller
@RequestMapping("/svn")
public class SvnController {

    @RequestMapping("/commits.html")
    public String commits() throws SVNException, IOException {
        //SamplesUtility.initializeFSFSprotocol();

        File baseDirectory = new File("d:\\home_projects\\test\\");
        //File reposRoot = new File(baseDirectory, "exampleRepository");
        File wcRoot = new File(baseDirectory, "exampleWC");

        try {
            //first create a repository and fill it with data
            /*SamplesUtility.createRepository(reposRoot);
            SVNCommitInfo info = SamplesUtility.createRepositoryTree(reposRoot);
            System.out.println(info);

            //checkout the entire repository tree
            SVNURL reposURL = SVNURL.fromFile(reposRoot);
            SamplesUtility.checkOutWorkingCopy(reposURL, wcRoot);

            //now make some changes to the working copy
            SamplesUtility.writeToFile(new File(wcRoot, "iota"), "New text appended to 'iota'", true);
            SamplesUtility.writeToFile(new File(wcRoot, "A/mu"), "New text in 'mu'", false);*/

            SVNClientManager clientManager = SVNClientManager.newInstance();
            /*SVNWCClient wcClient = SVNClientManager.newInstance().getWCClient();
            wcClient.doSetProperty(new File(wcRoot, "A/B"), "spam", SVNPropertyValue.create("egg"), false, SVNDepth.EMPTY, null, null);*/

            //now run diff the working copy against the repository
            SVNDiffClient diffClient = clientManager.getDiffClient();
            SVNLogClient logClient = clientManager.getLogClient();

            File[] paths = new File[1];
            paths[0] = wcRoot;
            //logClient.doLog(paths, SVNRevision.UNDEFINED, SVNRevision.WORKING, SVNRevision.HEAD, SVNDepth.INFINITY, true, System.out, null);
            /*
             * This corresponds to 'svn diff -rHEAD'.
             */
            diffClient.doDiff(wcRoot, SVNRevision.UNDEFINED, SVNRevision.WORKING, SVNRevision.HEAD, SVNDepth.INFINITY, true, System.out, null);
        } catch (SVNException svne) {
            System.out.println(svne.getErrorMessage());
            System.exit(1);
        }

        return "svn/commits";
    }
}

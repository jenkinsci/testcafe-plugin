package io.jenkins.plugins;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;
import hudson.model.Run;
import java.io.File;

public class TestcafeAttachmentsAction implements Action {

    private final Run run;

    TestcafeAttachmentsAction(Run run) {
        this.run = run;
    }

    Run getRun() {
        return run;
    }

    @Override
    public String getUrlName() {
        return "testcafe-attachments";
    }

    @Override
    public String getDisplayName() {
        return "Testcafe attachments";
    }

    @Override
    public String getIconFileName() {
        return "package.gif";
    }

    public DirectoryBrowserSupport doDynamic() {
        return new DirectoryBrowserSupport(this, new FilePath(new File(run.getRootDir().getAbsolutePath()))
                .child("testcafe-attachments"), "Testcafe attachments", "package.gif", true);
    }
}

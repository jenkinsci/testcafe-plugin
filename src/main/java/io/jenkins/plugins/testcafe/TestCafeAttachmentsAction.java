package io.jenkins.plugins.testcafe;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;
import hudson.model.Run;
import java.io.File;

public class TestCafeAttachmentsAction implements Action {

    private static final String TESTCAFE_ATTACHMENTS_URL_NAME = "testcafe-attachments";
    private static final String TESTCAFE_ATTACHMENTS_DISPLAY_NAME = "TestCafe Attachments";
    private static final String TESTCAFE_ATTACHMENTS_ICON_NAME = "package.gif";

    private final Run run;

    TestCafeAttachmentsAction(Run run) {
        this.run = run;
    }

    Run getRun() {
        return run;
    }

    @Override
    public String getUrlName() {
        return TESTCAFE_ATTACHMENTS_URL_NAME;
    }

    @Override
    public String getDisplayName() {
        return TESTCAFE_ATTACHMENTS_DISPLAY_NAME;
    }

    @Override
    public String getIconFileName() {
        return TESTCAFE_ATTACHMENTS_ICON_NAME;
    }

    public DirectoryBrowserSupport doDynamic() {
        return new DirectoryBrowserSupport(
                this,
                new FilePath(new File(run.getRootDir().getAbsolutePath()))
                        .child(Constants.TESTCAFE_ATTACHMENTS_DIR_NAME),
                TESTCAFE_ATTACHMENTS_DISPLAY_NAME,
                TESTCAFE_ATTACHMENTS_ICON_NAME,
                true
        );
    }
}

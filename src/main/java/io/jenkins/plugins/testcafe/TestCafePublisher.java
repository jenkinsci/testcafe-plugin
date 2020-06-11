package io.jenkins.plugins.testcafe;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.junit.TestDataPublisher;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.SuiteResult;
import java.io.File;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.kohsuke.stapler.DataBoundConstructor;

public class TestCafePublisher extends TestDataPublisher {

    @DataBoundConstructor
    public TestCafePublisher() {
    }

    @Override
    public TestResultAction.Data contributeTestData(Run<?, ?> run, FilePath workspace,
            Launcher launcher, TaskListener listener,
            TestResult testResult) throws IOException, InterruptedException {
        FilePath attachmentsStorage = new FilePath(new File(run.getRootDir().getAbsolutePath()))
                .child(Constants.TESTCAFE_ATTACHMENTS_DIR_NAME);

        attachmentsStorage.mkdirs();

        run.addAction(new TestCafeAttachmentsAction(run));

        final Set<String> suiteIDs = new HashSet<>();

        for (SuiteResult suiteResult : testResult.getSuites()) {
            suiteIDs.add(suiteResult.getId());

            List<CaseResult> caseResults = suiteResult.getCases();

            for (CaseResult caseResult : caseResults) {
                AttachmentsDirs attachmentsDirs = (new AttachmentsDirsParser(caseResult)).parse();
                List<Attachment> attachments = (new AttachmentsParser(caseResult)).parse();

                for (Attachment attachment : attachments) {
                    final String attachmentAbsolutePath = Paths.get(
                            attachmentsDirs.getDir(attachment.getType()),
                            attachment.getPath()
                    ).toString();

                    final String attachmentNewFilename = attachment.getHashValue() + attachment.getExtension();

                    // even though we use child(), this should be absolute
                    FilePath from = workspace.child(attachmentAbsolutePath);

                    FilePath dst = new FilePath(attachmentsStorage, attachmentNewFilename);

                    from.copyTo(dst);
                }
            }
        }

        return new TestData(suiteIDs);
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<TestDataPublisher> {
        
        private static final String TESTCAFE_PUBLISH_DISPLAY_NAME = "Include links to TestCafe artifacts";
        
        /*
        * Displayed when user clicked Add button in "Additional test report features"
         */
        @Override
        public String getDisplayName() {
            return TESTCAFE_PUBLISH_DISPLAY_NAME;
        }
    }
}

package io.jenkins.plugins;

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
import java.util.List;
import java.util.Map;
import org.kohsuke.stapler.DataBoundConstructor;

public class TestcafePublisher extends TestDataPublisher {

    @DataBoundConstructor
    public TestcafePublisher() {
    }

    @Override
    public TestResultAction.Data contributeTestData(Run<?, ?> run, FilePath workspace,
            Launcher launcher, TaskListener listener,
            TestResult testResult) throws IOException, InterruptedException {
        FilePath attachmentsStorage = new FilePath(new File(run.getRootDir().getAbsolutePath()))
                .child("testcafe-attachments");

        attachmentsStorage.mkdirs();

        run.addAction(new TestcafeAttachmentsAction(run));

        for (SuiteResult suiteResult : testResult.getSuites()) {
            List<CaseResult> caseResults = suiteResult.getCases();

            for (CaseResult caseResult : caseResults) {
                Map<String, String> attachments = (new AttachmentsParser(caseResult)).parse();

                for (Map.Entry<String, String> entry : attachments.entrySet()) {
                    final String attachmentAbsolutePath = entry.getKey();
                    final String attachmentNewFilename = entry.getValue();

                    // even though we use child(), this should be absolute
                    FilePath from = workspace.child(attachmentAbsolutePath);

                    FilePath dst = new FilePath(attachmentsStorage, attachmentNewFilename);

                    from.copyTo(dst);
                }
            }
        }

        return new TestData();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<TestDataPublisher> {

        /*
        * Displayed when user clicked Add button in "Additional test report features"
         */
        @Override
        public String getDisplayName() {
            return "Testcafe Report";
        }
    }
}

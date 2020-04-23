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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jenkins.model.ArtifactManager;
import jenkins.util.BuildListenerAdapter;
import org.kohsuke.stapler.DataBoundConstructor;

public class TestcafePublisher extends TestDataPublisher {

    @DataBoundConstructor
    public TestcafePublisher() {
    }

    @Override
    public TestResultAction.Data contributeTestData(Run<?, ?> run, FilePath workspace,
            Launcher launcher, TaskListener listener,
            TestResult testResult) throws IOException, InterruptedException {

        final ArtifactManager artifactManager = run.pickArtifactManager();

        Map<String, String> mapFromArchiveToWorkspace = new HashMap<>();

        for (SuiteResult suiteResult : testResult.getSuites()) {
            List<CaseResult> caseResults = suiteResult.getCases();

            for (CaseResult caseResult : caseResults) {
                List<String> attachments = (new AttachmentsParser(caseResult, workspace)).parse();

                attachments.forEach(attachment -> {
                    mapFromArchiveToWorkspace.put(attachment, attachment);
                });
            }
        }

        artifactManager.archive(workspace, launcher, new BuildListenerAdapter(listener), mapFromArchiveToWorkspace);

        return new TestData(workspace);
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

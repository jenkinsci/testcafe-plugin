package io.jenkins.plugins;

import hudson.FilePath;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.TestAction;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.TestObject;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class TestData extends TestResultAction.Data {

    private static final Logger LOG = Logger.getLogger(TestData.class.getName());
    private final FilePath workspace;

    TestData(FilePath workspace) {
        this.workspace = workspace;
    }

    @Override
    public List<? extends TestAction> getTestAction(hudson.tasks.junit.TestObject testObject) {
        if (testObject instanceof CaseResult) {
            List<String> caseAttachments = (new AttachmentsParser((CaseResult) testObject, workspace)).parse();
            
            TestAction action = new TestcafeTestAction((TestObject) testObject, caseAttachments);
            
            return Collections.<TestAction>singletonList(action);
        }

        return Collections.emptyList();
    }
}

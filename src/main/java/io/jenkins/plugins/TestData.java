package io.jenkins.plugins;

import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.TestAction;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.TestObject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TestData extends TestResultAction.Data {

    private static final Logger LOG = Logger.getLogger(TestData.class.getName());

    @Override
    public List<? extends TestAction> getTestAction(hudson.tasks.junit.TestObject testObject) {
        if (testObject instanceof CaseResult) {
            final Map<String, String> testAttachments = (new AttachmentsParser((CaseResult) testObject)).parse();
            TestAction action = new TestcafeTestAction((TestObject) testObject, testAttachments);

            return Collections.<TestAction>singletonList(action);
        }

        return Collections.emptyList();
    }
}

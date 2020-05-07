package io.jenkins.plugins;

import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestAction;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.TestObject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class TestData extends TestResultAction.Data {

    private final Set<String> suiteIDs;

    TestData(Set<String> suiteIDs) {
        this.suiteIDs = suiteIDs;
    }

    private static final Logger LOG = Logger.getLogger(TestData.class.getName());

    @Override
    public List<? extends TestAction> getTestAction(hudson.tasks.junit.TestObject testObject) {
        if (testObject instanceof CaseResult) {
            CaseResult caseResult = (CaseResult) testObject;
            SuiteResult suiteResult = caseResult.getSuiteResult();
            String currentSuiteId = suiteResult.getId();

            if (!suiteIDs.contains(currentSuiteId)) {
                return Collections.emptyList();
            }

            final Map<String, String> testAttachments = (new AttachmentsParser(caseResult)).parse();
            TestAction action = new TestcafeTestAction((TestObject) testObject, testAttachments);

            return Collections.<TestAction>singletonList(action);
        }

        return Collections.emptyList();
    }
}

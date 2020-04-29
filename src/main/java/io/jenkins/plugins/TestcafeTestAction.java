package io.jenkins.plugins;

import hudson.Util;
import hudson.tasks.junit.TestAction;
import hudson.tasks.test.TestObject;
import java.util.Map;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/**
 * Represents page with information about the test
 *
 */
public class TestcafeTestAction extends TestAction {

    private static final Logger LOG = Logger.getLogger(TestAction.class.getName());

    private final TestObject testObject;

    private final Map<String, String> testAttachments;

    public Map<String, String> getAttachments() {
        return testAttachments;
    }

    public TestcafeTestAction(TestObject testObject, Map<String, String> testAttachments) {
        this.testObject = testObject;
        this.testAttachments = testAttachments;
    }

    @Override
    public String getUrlName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getIconFileName() {
        return "package.gif";
    }

    public String getUrl(String fileName) {
        return Jenkins.get().getRootUrl()
                + testObject.getRun().getUrl()
                + "testcafe-attachments/"
                + Util.rawEncode(fileName);
    }

    public TestObject getTestObject() {
        return testObject;
    }

    @Override
    public String annotate(String text) {
        return text;
    }
}

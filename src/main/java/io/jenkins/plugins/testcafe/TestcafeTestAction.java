package io.jenkins.plugins.testcafe;

import hudson.tasks.junit.TestAction;
import hudson.tasks.test.TestObject;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jenkins.model.Jenkins;

/**
 * Represents page with information about the test
 *
 */
public class TestcafeTestAction extends TestAction {

    private static final Logger LOG = Logger.getLogger(TestAction.class.getName());
    private static final String TESTCAFE_ATTACHMENTS_DIRNAME = "testcafe-attachments/";

    private final TestObject testObject;

    private final List<Attachment> testAttachments;

    public List<Attachment> getScreenshots() {
        return testAttachments
                .stream()
                .filter(attachment -> attachment.getType().equals("screenshot"))
                .collect(Collectors.toList());
    }

    public List<Attachment> getVideos() {
        return testAttachments
                .stream()
                .filter(attachment -> attachment.getType().equals("video"))
                .collect(Collectors.toList());
    }

    public TestcafeTestAction(TestObject testObject, List<Attachment> testAttachments) {
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

    public String getUrl(Attachment attachment) {
        return new StringBuilder()
                .append(Jenkins.get().getRootUrl())
                .append(testObject.getRun().getUrl())
                .append(TESTCAFE_ATTACHMENTS_DIRNAME)
                .append(attachment.getHashValue())
                .append(attachment.getExtension())
                .toString();
    }

    public String getDisplayUrl(Attachment attachment) {
        return attachment.getPath();
    }

    public TestObject getTestObject() {
        return testObject;
    }

    @Override
    public String annotate(String text) {
        return text;
    }
}

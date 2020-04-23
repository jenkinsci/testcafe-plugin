package io.jenkins.plugins;

import hudson.tasks.junit.TestAction;
import hudson.tasks.test.TestObject;
import java.util.List;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/**
 * Represents page with information about the test
 * 
 */
public class TestcafeTestAction extends TestAction {

    private static final Logger LOG = Logger.getLogger(TestAction.class.getName());

    private final TestObject testObject;
    
    /*
    * List of attachment paths with root in artifacts dir
    *
    * E.g.: 
    *   screenshots/2020-04-22_16-42-12/test-1/Firefox_75.0_Linux_0.0/errors/1.png
    *   videos/2020-04-22_16-42-12/test-1/Firefox_75.0_Linux_0.0/1.mp4
    */
    private final List<String> attachmentNames;

    public List<String> getAttachments() {
        return attachmentNames;
    }

    public TestcafeTestAction(TestObject testObject, List<String> attachmentNames) {
        this.testObject = testObject;
        this.attachmentNames = attachmentNames;
    }

    @Override
    public String getUrlName() {
        // Returning absolute path to archived artifacts
        // (because can not avoid usage of methods from jenkins Action)
        return Jenkins.get().getRootUrl() + testObject.getRun().getUrl() + "artifact/";
    }

    @Override
    public String getDisplayName() {
        return "Artifacts";
    }

    @Override
    public String getIconFileName() {
        return "package.gif";
    }

    /**
     * Transformation from relative to absolute path artifact
     * 
     * @param fileName with root in artifacts dir
     * @return 
     */
    public String getUrl(String fileName) {
        return Jenkins.get().getRootUrl() + testObject.getRun().getUrl() + "artifact/" + fileName;
    }

    public TestObject getTestObject() {
        return testObject;
    }

    @Override
    public String annotate(String text) {
        return text;
    }
}

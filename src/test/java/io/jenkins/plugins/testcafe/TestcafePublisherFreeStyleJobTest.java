package io.jenkins.plugins.testcafe;

import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.junit.JUnitResultArchiver;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.ExtractResourceSCM;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TouchBuilder;

public class TestcafePublisherFreeStyleJobTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private FreeStyleBuild build;

    private final List<String> ATTACHMENTS_HASHES = Arrays.asList(
            "19bdf35f-e776-4706-83f5-91f807386bcd",
            "b3b3fcf2-c7bb-416f-a834-28deb7dbdf85"
    );
    private final String WORKSPACE_FILENAME = "workspace.zip";
    private final String ATTACHMENTS_DIRNAME = "testcafe-attachments";

    @Before
    public void runFreestyleProjectBuild() throws IOException, InterruptedException, ExecutionException {
        FreeStyleProject project = jenkinsRule.createFreeStyleProject();

        project.setScm(new ExtractResourceSCM(getClass().getResource(WORKSPACE_FILENAME)));
        project.getBuildersList().add(new TouchBuilder());

        // publishing through JUnit with Testcafe
        JUnitResultArchiver archiver = new JUnitResultArchiver("*.xml");
        archiver.setKeepLongStdio(true);
        archiver.setTestDataPublishers(Arrays.asList(new TestcafePublisher()));

        project.getPublishersList().add(archiver);

        this.build = project.scheduleBuild2(0).get();

        assertNotNull(build);
    }

    @Test
    public void testThatAttachmentsCopiedCorrectly() throws IOException, InterruptedException, ExecutionException {
        final FilePath testcafeAttachments = (new FilePath(build.getRootDir())).child(ATTACHMENTS_DIRNAME);

        assertTrue("Testcafe attachments directory should exists", testcafeAttachments.exists());

        final List<String> attachmentBasenames = testcafeAttachments
                .list()
                .stream()
                .map(attachment -> attachment.getBaseName())
                .collect(Collectors.toList());

        Collections.sort(ATTACHMENTS_HASHES);
        Collections.sort(attachmentBasenames);

        assertTrue("Attachments should be named as UUID hashes", ATTACHMENTS_HASHES.equals(attachmentBasenames));
    }
}

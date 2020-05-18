package io.jenkins.plugins.testcafe;

import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.JUnitResultArchiver;
import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestResultAction;
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

    private final String SCREENSHOT_HASH = "b3b3fcf2-c7bb-416f-a834-28deb7dbdf85";
    private final String VIDEO_HASH = "19bdf35f-e776-4706-83f5-91f807386bcd";
    private final List<String> ATTACHMENTS_HASHES = Arrays.asList(SCREENSHOT_HASH, VIDEO_HASH);

    private final String WORKSPACE_FILENAME = "workspace.zip";
    private final String ATTACHMENTS_DIRNAME = "testcafe-attachments";
    private final String SUITE_NAME = "TestCafe Tests_ Chrome 81.0.4044.138 _ Linux 0.0";
    private final String CASE_NAME = "Submit name";

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

    @Test
    public void testThatActionReturnsCorrectAttachments() {
        TestResultAction testResultAction = build.getAction(TestResultAction.class);
        assertNotNull(testResultAction);

        SuiteResult suiteResult = testResultAction.getResult().getSuite(SUITE_NAME);
        assertNotNull(suiteResult);

        CaseResult caseResult = suiteResult.getCase(CASE_NAME);
        assertNotNull(caseResult);

        TestcafeTestAction caseAction = caseResult.getTestAction(TestcafeTestAction.class);
        assertNotNull(caseAction);

        List<Attachment> screenshots = caseAction.getScreenshots();
        assertNotNull(screenshots);

        List<Attachment> videos = caseAction.getVideos();
        assertNotNull(videos);

        final List<String> screenshotHashes = screenshots
                .stream()
                .map(attachment -> attachment.getHashValue())
                .collect(Collectors.toList());

        final List<String> videoHashes = videos
                .stream()
                .map(video -> video.getHashValue())
                .collect(Collectors.toList());

        assertTrue("Action should return correct screenshot", screenshotHashes.equals(Arrays.asList(SCREENSHOT_HASH)));
        assertTrue("Action should return correct video", videoHashes.equals(Arrays.asList(VIDEO_HASH)));
    }
}

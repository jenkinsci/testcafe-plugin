package io.jenkins.plugins.testcafe;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.JUnitResultArchiver;
import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestResultAction;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertEquals;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.ExtractResourceSCM;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TouchBuilder;
import org.xml.sax.SAXException;

public class TestCafePublisherFreeStyleJobTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private FreeStyleBuild build;

    private final String SCREENSHOT_MD5_HASH = "7f6311d49e814d49bee43da03d6aaa2a";
    private final String VIDEO_MD5_HASH = "b06f526b7b1501638fa83a65d7c51019";

    private final String SCREENSHOT_HASH = "b3b3fcf2-c7bb-416f-a834-28deb7dbdf85";
    private final String VIDEO_HASH = "19bdf35f-e776-4706-83f5-91f807386bcd";
    private final List<String> ATTACHMENTS_HASHES = Arrays.asList(
            // Hashes of attachments with absolute paths
            SCREENSHOT_HASH,
            VIDEO_HASH,
            // Hashes of attachments with relative to screenshotsDir and videosDir
            "ab720da1-1f7c-4b6b-a31e-3219741baa38",
            "f2c130fa-de86-4ac1-9e33-a4e850746976");

    private final String WORKSPACE_FILENAME = "workspace.zip";

    // Suite with absolute paths
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
        archiver.setTestDataPublishers(Arrays.asList(new TestCafePublisher()));

        project.getPublishersList().add(archiver);

        this.build = project.scheduleBuild2(0).get();

        assertNotNull(build);
    }

    @Test
    public void testThatAttachmentsCopiedCorrectly() throws IOException, InterruptedException, ExecutionException {
        final FilePath testcafeAttachments = (new FilePath(build.getRootDir()))
                .child(Constants.TESTCAFE_ATTACHMENTS_DIR_NAME);

        assertTrue("TestCafe attachments directory should exists", testcafeAttachments.exists());

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

        TestCafeTestAction caseAction = caseResult.getTestAction(TestCafeTestAction.class);
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

    @Test
    public void testThatActionReturnValidUrlToScreenshot() throws IOException, InterruptedException {
        TestCafeTestAction caseAction = build
                .getAction(TestResultAction.class)
                .getResult()
                .getSuite(SUITE_NAME)
                .getCase(CASE_NAME)
                .getTestAction(TestCafeTestAction.class);

        final List<Attachment> screenshots = caseAction.getScreenshots();
        assertEquals(1, screenshots.size());

        final String screenshotUrl = caseAction.getUrl(screenshots.get(0));

        InputStream inputStream = jenkinsRule
                .createWebClient()
                .getPage(screenshotUrl)
                .getWebResponse()
                .getContentAsStream();

        File downloadedScreenshot = new File("downloadedScreenshot.png");
        FileUtils.copyInputStreamToFile(inputStream, downloadedScreenshot);

        assertNotNull(downloadedScreenshot);
        assertEquals("Downloaded screenshot should have the same MD5 value",
                SCREENSHOT_MD5_HASH,
                (new FilePath(downloadedScreenshot)).digest()
        );

        assertTrue("Should delete downloaded screenshot", downloadedScreenshot.delete());
    }

    @Test
    public void testThatActionReturnValidUrlToVideo() throws IOException, InterruptedException {
        TestCafeTestAction caseAction = build
                .getAction(TestResultAction.class)
                .getResult()
                .getSuite(SUITE_NAME)
                .getCase(CASE_NAME)
                .getTestAction(TestCafeTestAction.class);

        final List<Attachment> videos = caseAction.getVideos();
        assertEquals(1, videos.size());

        final String screenshotUrl = caseAction.getUrl(videos.get(0));

        InputStream inputStream = jenkinsRule
                .createWebClient()
                .getPage(screenshotUrl)
                .getWebResponse()
                .getContentAsStream();

        File downloadedVideo = new File("downloadedVideo.mp4");
        FileUtils.copyInputStreamToFile(inputStream, downloadedVideo);

        assertNotNull(downloadedVideo);
        assertEquals("Downloaded video should have the same MD5 hash",
                VIDEO_MD5_HASH,
                (new FilePath(downloadedVideo)).digest()
        );

        assertTrue("Should delete downloaded video", downloadedVideo.delete());
    }

    @Test
    public void testThatTestCasePageCorrectShowAttachments() throws IOException, SAXException {
        String buildUrl = build.getUrl();
        CaseResult caseResult = build
                .getAction(TestResultAction.class)
                .getResult()
                .getSuite(SUITE_NAME)
                .getCase(CASE_NAME);

        final HtmlElement body = jenkinsRule
                .createWebClient()
                .goTo(buildUrl + "testReport" + caseResult.getUrl())
                // this url looks like
                // /jenkins/job/test0/1/testReport/junit/(root)/Page manipulation/Submit_name/
                .getBody();

        final List<HtmlElement> attachmentHeadings = body.getByXPath("//*[@id=\"main-panel\"]/table/tbody/tr/td/h3");

        assertEquals("Should contain two table headings (screenshots and videos)",
                2,
                attachmentHeadings.size()
        );
        assertEquals("Should contain table with correct number of screenshots in heading",
                "Screenshots (1)",
                attachmentHeadings.get(0).getTextContent()
        );
        assertEquals("Should contain table with correct number of videos in heading",
                "Videos (1)",
                attachmentHeadings.get(1).getTextContent()
        );

        final HtmlTable screenshotsTable = (HtmlTable) attachmentHeadings.get(0).getNextSibling();
        final HtmlTable videosTable = (HtmlTable) attachmentHeadings.get(1).getNextSibling();

        assertEquals("Should contain screenshots table with correct number of rows",
                2,
                screenshotsTable.getRowCount()
        );
        assertEquals("Should contain videos table with correct number of rows",
                2,
                videosTable.getRowCount()
        );
        assertEquals("Should contain screenshots table with correct first row",
                "Files",
                screenshotsTable.getRow(0).getTextContent()
        );
        assertEquals("Should contain videos table with correct first row",
                "Files",
                videosTable.getRow(0).getTextContent()
        );

        final HtmlAnchor linkToScreenshot = (HtmlAnchor) screenshotsTable.getCellAt(1, 0).getFirstChild();
        final HtmlAnchor linkToVideo = (HtmlAnchor) videosTable.getCellAt(1, 0).getFirstChild();
        final TestCafeTestAction caseAction = caseResult.getTestAction(TestCafeTestAction.class);
        final Attachment screenshot = caseAction.getScreenshots().get(0);
        final Attachment video = caseAction.getVideos().get(0);

        assertEquals("Should contain screenshots table with correct link href",
                caseAction.getUrl(screenshot),
                linkToScreenshot.getHrefAttribute()
        );
        assertEquals("Should contain videos table with correct link href",
                caseAction.getUrl(video),
                linkToVideo.getHrefAttribute()
        );
        assertEquals("Should contain screenshots table with correct link text",
                screenshot.getPath(),
                linkToScreenshot.getTextContent()
        );
        assertEquals("Should contain videos table with correct link text",
                video.getPath(),
                linkToVideo.getTextContent()
        );
    }
}

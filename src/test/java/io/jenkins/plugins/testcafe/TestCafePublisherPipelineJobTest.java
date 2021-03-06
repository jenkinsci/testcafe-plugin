package io.jenkins.plugins.testcafe;

import hudson.FilePath;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class TestCafePublisherPipelineJobTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private WorkflowRun build;

    private final String WORKSPACE_FILENAME = "workspace.zip";
    private final String PIPELINE_FILENAME = "pipelineTest.groovy";

    private final String SCREENSHOT_HASH = "b3b3fcf2-c7bb-416f-a834-28deb7dbdf85";
    private final String VIDEO_HASH = "19bdf35f-e776-4706-83f5-91f807386bcd";
    private final List<String> ATTACHMENTS_HASHES = Arrays.asList(
            // Hashes of attachments with absolute paths
            SCREENSHOT_HASH,
            VIDEO_HASH,
            // Hashes of attachments with relative to screenshotsDir and videosDir
            "ab720da1-1f7c-4b6b-a31e-3219741baa38",
            "f2c130fa-de86-4ac1-9e33-a4e850746976");

    @Before
    public void runPipelineProjectBuild() throws IOException, InterruptedException, ExecutionException {
        WorkflowJob project = jenkinsRule.createProject(WorkflowJob.class);

        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(project);
        FilePath wsZip = workspace.child(WORKSPACE_FILENAME);

        wsZip.copyFrom(getClass().getResource(WORKSPACE_FILENAME));
        wsZip.unzip(workspace);

        for (FilePath f : workspace.list()) {
            f.touch(System.currentTimeMillis());
        }

        project.setDefinition(new CpsFlowDefinition(fileContentsFromResources(PIPELINE_FILENAME), true));

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

    private String fileContentsFromResources(String fileName) throws IOException {
        String fileContents = null;

        URL url = getClass().getResource(fileName);
        if (url != null) {
            fileContents = IOUtils.toString(url, "UTF-8");
        }

        return fileContents;

    }
}

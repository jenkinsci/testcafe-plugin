package io.jenkins.plugins.testcafe;

import hudson.tasks.junit.CaseResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract attachment dirs from CaseResult stdOut
 */
public class AttachmentsDirsParser {

    private final CaseResult caseResult;

    private static final Pattern ATTACHMENT_PATTERN = Pattern.compile("\\[\\[(screenshotsDir|videosDir)\\|(.*)\\]\\]");

    AttachmentsDirsParser(CaseResult caseResult) {
        this.caseResult = caseResult;
    }

    AttachmentsDirs parse() {
        final AttachmentsDirs attachmentsDirs = new AttachmentsDirs();

        if (caseResult.getStdout() == null) {
            return attachmentsDirs;
        }

        final String stdOut = caseResult.getStdout();
        final Matcher matcher = ATTACHMENT_PATTERN.matcher(stdOut);

        while (matcher.find()) {
            final String attachmentType = matcher.group(1);
            final String baseDir = matcher.group(2);

            attachmentsDirs.setDir(attachmentType, baseDir);
        }

        return attachmentsDirs;
    }
}

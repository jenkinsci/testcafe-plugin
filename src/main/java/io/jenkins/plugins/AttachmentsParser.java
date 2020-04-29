package io.jenkins.plugins;

import hudson.tasks.junit.CaseResult;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract attachments from CaseResult stdOut
 *
 * FORMAT: [[ATTACHMENT|/absolute/path/to/attach/|UUID-OF-ATTACHMENT]]
 */
public class AttachmentsParser {

    private final CaseResult caseResult;

    private static final Pattern ATTACHMENT_PATTERN = Pattern.compile("\\[\\[ATTACHMENT\\|(.*)\\|(.*)\\]\\]");

    AttachmentsParser(CaseResult caseResult) {
        this.caseResult = caseResult;
    }

    Map<String, String> parse() {
        final Map<String, String> attachments = new HashMap<>();

        if (caseResult.getStdout() == null) {
            return attachments;
        }

        final String stdOut = caseResult.getStdout();
        final Matcher matcher = ATTACHMENT_PATTERN.matcher(stdOut);

        while (matcher.find()) {
            String attachmentAbsolutePath = matcher.group(1);
            String attachmentHash = matcher.group(2);
            String extension = attachmentAbsolutePath.substring(attachmentAbsolutePath.lastIndexOf("."));

            attachments.put(attachmentAbsolutePath, attachmentHash + extension);
        }

        return attachments;
    }
}

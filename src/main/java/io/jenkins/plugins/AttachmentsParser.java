package io.jenkins.plugins;

import hudson.tasks.junit.CaseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract attachments from CaseResult stdOut
 *
 * FORMAT: [[screenshot|/absolute/path/to/screenshot/|UUID-OF-SCREENSHOT]]
 * [[video|/absolute/path/to/video/|UUID-OF-VIDEO]]
 */
public class AttachmentsParser {

    private final CaseResult caseResult;

    private static final Pattern ATTACHMENT_PATTERN = Pattern.compile("\\[\\[(.*)\\|(.*)\\|(.*)\\]\\]");

    AttachmentsParser(CaseResult caseResult) {
        this.caseResult = caseResult;
    }

    List<Attachment> parse() {
        final List<Attachment> attachments = new ArrayList<>();

        if (caseResult.getStdout() == null) {
            return attachments;
        }

        final String stdOut = caseResult.getStdout();
        final Matcher matcher = ATTACHMENT_PATTERN.matcher(stdOut);

        while (matcher.find()) {
            String type = matcher.group(1);
            String absolutePath = matcher.group(2);
            String hashValue = matcher.group(3);

            Attachment attachment = new Attachment(type, absolutePath, hashValue);

            attachments.add(attachment);
        }

        return attachments;
    }
}

package io.jenkins.plugins;

import hudson.FilePath;
import hudson.tasks.junit.CaseResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract attachments from CaseResult stdOut
 */
public class AttachmentsParser {

    private final CaseResult caseResult;
    private final FilePath workspace;

    private final String PREFIX;
    private final String SUFFIX;
    private final Pattern ATTACHMENT_PATTERN;

    AttachmentsParser(CaseResult caseResult, FilePath workspace) {
        this.caseResult = caseResult;
        this.workspace = workspace;
        this.PREFIX = "[[ATTACHMENT|";
        this.SUFFIX = "]]";
        this.ATTACHMENT_PATTERN = Pattern.compile("\\[\\[ATTACHMENT\\|.+\\]\\]");
    }

    /*
    * Copy-pasted from "JUnit Attachments plugin"
    */
    List<String> parse() {
        final List<String> attachments = new LinkedList<>();

        if (caseResult.getStdout() == null) {
            return attachments;
        }

        final String stdOut = caseResult.getStdout();

        Matcher matcher = ATTACHMENT_PATTERN.matcher(stdOut);
        
        while (matcher.find()) {
            String line = matcher.group().trim(); // Be more tolerant about where ATTACHMENT lines start/end
            // compute the file name
            line = line.substring(PREFIX.length(), line.length() - SUFFIX.length());
            int idx = line.indexOf('|');
            if (idx >= 0) {
                line = line.substring(0, idx);
            }

            String fileName = line;

            if (fileName != null) {
                // relativeze attachment's absolute path to current workspace 
                // E.g.:
                // from    /jenkins/workspace/Job name/screenshots/1.png
                // to      screenshots/1.png
                Path relativeToWorkspace = Paths.get(workspace.getRemote()).relativize(Paths.get(fileName));
                
                attachments.add(relativeToWorkspace.toString());
            }
        }

        return attachments;
    }
}

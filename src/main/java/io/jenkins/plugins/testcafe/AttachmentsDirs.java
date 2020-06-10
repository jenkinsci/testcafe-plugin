package io.jenkins.plugins.testcafe;

public class AttachmentsDirs {

    private String screenshotsDir;
    private String videosDir;

    AttachmentsDirs() {
        this.screenshotsDir = "";
        this.videosDir = "";
    }

    void setDir(String type, String baseDir) {
        switch (type) {
            case "screenshotsDir":
                this.screenshotsDir = baseDir;
                break;
            case "videosDir":
                this.videosDir = baseDir;
                break;
            default:
                throw new Error("Incorrect attachment type");
        }
    }

    String getDir(Attachment.Type attachmentType) {
        switch (attachmentType) {
            case Screenshot:
                return this.screenshotsDir;
            case Video:
                return this.videosDir;
            default:
                throw new Error("Incorrect attachment type");
        }
    }
}

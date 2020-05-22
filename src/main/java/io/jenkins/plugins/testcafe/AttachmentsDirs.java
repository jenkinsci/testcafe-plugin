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

    String getDir(String attachmentType) {
        switch (attachmentType) {
            case "screenshot":
                return this.screenshotsDir;
            case "video":
                return this.videosDir;
            default:
                throw new Error("Incorrect attachment type");
        }
    }
}

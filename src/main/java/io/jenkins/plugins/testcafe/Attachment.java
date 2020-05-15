package io.jenkins.plugins.testcafe;

public class Attachment {

    private final String type;
    private final String absolutePath;
    private final String hashValue;

    Attachment(String type, String absolutePath, String hashValue) {
        this.type = type;
        this.absolutePath = absolutePath;
        this.hashValue = hashValue;
    }

    String getType() {
        return type;
    }

    String getAbsolutePath() {
        return absolutePath;
    }

    String getHashValue() {
        return hashValue;
    }

    String getExtension() {
        switch (type) {
            case "video":
                return ".mp4";
            case "screenshot":
                return ".png";
            default:
                return "";
        }
    }
}

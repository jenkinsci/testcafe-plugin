package io.jenkins.plugins.testcafe;

public class Attachment {

    private final String type;
    private final String path;
    private final String hashValue;

    Attachment(String type, String path, String hashValue) {
        this.type = type;
        this.path = path;
        this.hashValue = hashValue;
    }

    String getType() {
        return type;
    }

    String getPath() {
        return path;
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

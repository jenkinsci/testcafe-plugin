package io.jenkins.plugins.testcafe;

import java.util.HashMap;
import java.util.Map;

public class AttachmentsDirs {
    enum Type {
        ScreenshotsDir,
        VideosDir,
        Unknown
    }
    
    Map<String, Type> mapStringToType = new HashMap<>();
    {
        mapStringToType.put("screenshotsDir", Type.ScreenshotsDir);
        mapStringToType.put("videosDir", Type.VideosDir);
    }
    
    private String screenshotsDir;
    private String videosDir;

    AttachmentsDirs() {
        this.screenshotsDir = "";
        this.videosDir = "";
    }

    Type getType(String type) {
        return mapStringToType.getOrDefault(type, Type.Unknown);
    }
    
    void setDir(Type type, String baseDir) {
        switch (type) {
            case ScreenshotsDir:
                this.screenshotsDir = baseDir;
                break;
            case VideosDir:
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

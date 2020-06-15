package io.jenkins.plugins.testcafe;

import java.util.HashMap;
import java.util.Map;

public class Attachment {
    enum Type {
        Screenshot,
        Video,
        Unknown
    };
    
    Map<String,Type> mapStringToType = new HashMap<>();
    {
        mapStringToType.put("screenshot", Type.Screenshot);
        mapStringToType.put("video", Type.Video);
    }
    
    private final Type type;
    private final String path;
    private final String hashValue;

    Attachment(String type, String path, String hashValue) {
        this.type = mapStringToType.getOrDefault(type, Type.Unknown);
        this.path = path;
        this.hashValue = hashValue;
    }

    Type getType() {
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
            case Video:
                return ".mp4";
            case Screenshot:
                return ".png";
            default:
                return "";
        }
    }
}

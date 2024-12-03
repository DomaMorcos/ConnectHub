package connecthub.ContentCreation.Backend;

import javax.json.JsonObject;

public interface Content {
    String getContentId();
    String getAuthorId();
    String getTimestamp();
    JsonObject toJson();
}
package connecthub.ContentCreation.Backend;

import javax.json.Json;
import javax.json.JsonObject;

public abstract class AbstractContent implements Content {
    private String contentId;
    private String authorId;
    private String content;
    private String imagePath;
    private String timestamp;

    public AbstractContent(String contentId, String authorId, String content, String imagePath, String timestamp) {
        this.contentId = contentId;
        this.authorId = authorId;
        this.content = content;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public JsonObject toJson() {
        //create a json object
        return Json.createObjectBuilder()
                .add("contentId", contentId)
                .add("authorId", authorId)
                .add("content", content)
                .add("imagePath", imagePath != null ? imagePath : "")
                .add("timestamp", timestamp)
                .build();
    }
}
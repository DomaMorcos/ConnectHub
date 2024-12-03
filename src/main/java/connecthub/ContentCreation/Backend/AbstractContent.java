package connecthub.ContentCreation.Backend;

import javax.json.Json;
import javax.json.JsonObject;

public abstract class AbstractContent implements Content {
    protected String contentId;
    protected String authorId;
    protected String content;
    protected String imagePath;
    protected String timestamp;

    public AbstractContent(String contentId, String authorId, String content, String imagePath, String timestamp) {
        this.contentId = contentId;
        this.authorId = authorId;
        this.content = content;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String getContentId() {
        return contentId;
    }

    @Override
    public String getAuthorId() {
        return authorId;
    }

    @Override
    public String getTimestamp() {
        return timestamp;
    }

    public  JsonObject baseJson() {

        return  Json.createObjectBuilder()
                .add("contentId", contentId)
                .add("authorId", authorId)
                .add("content", content)
                .add("imagePath", imagePath != null ? imagePath : "")
                .add("timestamp", timestamp)
                .build();
    }
}
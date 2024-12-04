package connecthub.ContentCreation.Backend;

import org.json.JSONObject;

public abstract class AbstractContent implements Content {
    private int contentId;
    private String authorId;
    private String content;
    private String imagePath;
    private String timestamp;

    public AbstractContent(int contentId, String authorId, String content, String imagePath, String timestamp) {
        this.contentId = contentId;
        this.authorId = authorId;
        this.content = content;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
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

    public JSONObject toJson() {
        //create a json object
        JSONObject jsonObject = new JSONObject();
        jsonObject
                .put("contentId", contentId)
                .put("authorId", authorId)
                .put("content", content)
                .put("imagePath", imagePath != null ? imagePath : "")
                .put("timestamp", timestamp);
        return jsonObject;
    }
}
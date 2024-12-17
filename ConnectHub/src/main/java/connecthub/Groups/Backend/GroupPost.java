package connecthub.Groups.Backend;

import org.json.JSONObject;

public class GroupPost {
    private String postId;
    private String authorId;
    private String content;
    private String imagePath; // Optional field for image path
    private String timestamp;

    public GroupPost(String postId, String authorId, String content, String imagePath, String timestamp) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }

    public GroupPost(String authorId, String content, String imagePath, String timestamp) {
        this.postId = authorId + "_" + timestamp;
        this.authorId = authorId;
        this.content = content;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("postId", postId);
        jsonObject.put("authorId", authorId);
        jsonObject.put("content", content);
        jsonObject.put("imagePath", imagePath != null ? imagePath : "");
        jsonObject.put("timestamp", timestamp);
        return jsonObject;
    }

    public static GroupPost fromJson(JSONObject jsonObject) {
        String postId = jsonObject.getString("postId");
        String authorId = jsonObject.getString("authorId");
        String content = jsonObject.getString("content");
        String imagePath = jsonObject.optString("imagePath", "");
        String timestamp = jsonObject.getString("timestamp");

        return new GroupPost(postId, authorId, content, imagePath, timestamp);
    }

    @Override
    public String toString() {
        return "Post ID: " + postId + "\n" +
                "Author: " + authorId + "\n" +
                "Content: " + content + "\n" +
                "Image Path: " + imagePath + "\n" +
                "Timestamp: " + timestamp;
    }
}
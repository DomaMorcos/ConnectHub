package connecthub.ContentCreation.Backend;

import org.json.JSONObject;

public class Post extends AbstractContent {

    public Post(String contentId, String authorId, String content, String imagePath, String timestamp) {
        super(contentId, authorId, content, imagePath, timestamp);
    }

    @Override
    public JSONObject toJson() {
        //add to the json object the type
        JSONObject obj = super.toJson();
        obj.put("type", "post");
        return obj;
    }

    public static Post readFromJson(JSONObject jsonObject) {
        // make post from json object
        String contentId = jsonObject.optString("contentId", "");
        String authorId = jsonObject.optString("authorId", "unknown");
        String content = jsonObject.optString("content", "");
        String imagePath = jsonObject.optString("imagePath", "");
        String timestamp = jsonObject.optString("timestamp", "");
        return new Post(contentId, authorId, content, imagePath, timestamp);
    }

    @Override
    public String toString() {
        return "Post{contentId='" + getContentId() + "', content='" + getContent() + "', authorId='" + getAuthorId() + "', imagePath='" + getImagePath() + "', timestamp='" + getTimestamp() + "'}";
    }
}
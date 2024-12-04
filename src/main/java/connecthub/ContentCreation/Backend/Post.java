package connecthub.ContentCreation.Backend;

import org.json.JSONObject;

import javax.json.Json;
import javax.json.JsonObject;

public class Post extends AbstractContent {

    public Post(String contentId, String authorId, String content, String imagePath, String timestamp) {
        super(contentId, authorId, content, imagePath, timestamp);
    }

    @Override
    public JsonObject toJson() {
        return Json.createObjectBuilder(baseJson())
                .add("type", "post")
                .build();
    }

    public static Post fromJson(JsonObject jsonObject) {
        return new Post(
                jsonObject.getString("contentId"),
                jsonObject.getString("authorId"),
                jsonObject.getString("content"),
                jsonObject.getString("imagePath", ""),
                jsonObject.getString("timestamp")
        );
    }


}
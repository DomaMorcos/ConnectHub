package connecthub.ContentCreation.Backend;

import javax.json.Json;
import javax.json.JsonObject;

public class Post extends AbstractContent {

    public Post(String contentId, String authorId, String content, String imagePath, String timestamp) {
        super(contentId, authorId, content, imagePath, timestamp);
    }

    @Override
    public JsonObject toJson() {
        //add to the json object the type
        return Json.createObjectBuilder(super.toJson())
                .add("type", "post")
                .build();
    }

    public static Post readFromJson(JsonObject jsonObject) {
        //make from json a post
        return new Post(
                jsonObject.getString("contentId"),
                jsonObject.getString("authorId"),
                jsonObject.getString("content"),
                jsonObject.getString("imagePath", ""),
                jsonObject.getString("timestamp")
        );
    }
}
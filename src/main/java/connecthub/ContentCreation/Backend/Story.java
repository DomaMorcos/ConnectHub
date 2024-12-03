package connecthub.ContentCreation.Backend;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDateTime;

public class Story extends AbstractContent {
    private static final int EXPIRATION_HOURS = 24;

    public Story(String contentId, String authorId, String content, String imagePath, String timestamp) {
        super(contentId, authorId, content, imagePath, timestamp);
    }

    @Override
    public JsonObject toJson() {
        //add to the json object the type
        return Json.createObjectBuilder(super.toJson())
                .add("type", "story")
                .build();
    }

    public static Story readFromJson(JsonObject jsonObject) {
        //make from json a story
        return new Story(
                jsonObject.getString("contentId"),
                jsonObject.getString("authorId"),
                jsonObject.getString("content"),
                jsonObject.getString("imagePath", ""),
                jsonObject.getString("timestamp")
        );
    }

    public boolean isExpired() {
        //get the date of creation
        LocalDateTime storyTime = LocalDateTime.parse(getTimestamp());
        //make it +24
        //if it's before now return true
        return storyTime.plusHours(EXPIRATION_HOURS).isBefore(LocalDateTime.now());
    }
}
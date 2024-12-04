package connecthub.ContentCreation.Backend;

import org.json.JSONObject;

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
        return Json.createObjectBuilder(baseJson())
                .add("type", "story")
                .build();
    }

    public static Story fromJson(JsonObject jsonObject) {
        return new Story(
                jsonObject.getString("contentId"),
                jsonObject.getString("authorId"),
                jsonObject.getString("content"),
                jsonObject.getString("imagePath", ""),
                jsonObject.getString("timestamp")
        );
    }


    public boolean isExpired() {
        LocalDateTime storyTime = LocalDateTime.parse(timestamp);
        return storyTime.plusHours(EXPIRATION_HOURS).isBefore(LocalDateTime.now());
    }
}
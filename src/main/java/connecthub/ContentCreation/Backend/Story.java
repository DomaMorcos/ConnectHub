package connecthub.ContentCreation.Backend;

import org.json.JSONObject;

import java.time.LocalDateTime;

public class Story extends AbstractContent {
    private static final int EXPIRATION_HOURS = 24;

    public Story(String contentId, String authorId, String content, String imagePath, String timestamp) {
        super(contentId, authorId, content, imagePath, timestamp);
    }

    @Override
    public JSONObject toJson() {
        //add to the json object the type
        JSONObject obj = super.toJson();
        obj.put("type", "story");
        return obj;
    }

    public static Story readFromJson(JSONObject jsonObject) {
        // make Story from json object
        String contentId = jsonObject.optString("contentId", "");
        String authorId = jsonObject.optString("authorId", "unknown");
        String content = jsonObject.optString("content", "");
        String imagePath = jsonObject.optString("imagePath", "");
        String timestamp = jsonObject.optString("timestamp", "");
        return new Story(contentId, authorId, content, imagePath, timestamp);
    }

    public boolean isExpired() {
        //get the date of creation
        LocalDateTime storyTime = LocalDateTime.parse(getTimestamp());
        //make it +24
        //if it's before now return true
        return storyTime.plusHours(EXPIRATION_HOURS).isBefore(LocalDateTime.now());
    }
}
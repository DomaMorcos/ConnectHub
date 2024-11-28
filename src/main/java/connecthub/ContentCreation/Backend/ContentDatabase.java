package connecthub.ContentCreation.Backend;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ContentDatabase {
    private static final String POSTS_FILEPATH = "Posts.JSON";
    private static final String STORIES_FILEPATH = "Stories.JSON";
    private final List<Content> allContent = new ArrayList<>();

    public void saveContent() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Content content : allContent) {
            arrayBuilder.add(content.toJson());
        }
        JsonArray jsonArray = arrayBuilder.build();

        try (OutputStream os = new FileOutputStream(POSTS_FILEPATH);
             JsonWriter jsonWriter = Json.createWriter(os)) {
            jsonWriter.writeArray(jsonArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllContent() {
        allContent.clear();
        loadContent(POSTS_FILEPATH, Post::fromJson);
        loadContent(STORIES_FILEPATH, Story::fromJson);
        removeExpiredStories();
    }

    private <T extends Content> void loadContent(String filePath, Function<JsonObject, T> factory) {
        try (InputStream is = new FileInputStream(filePath);
             JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            for (JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class)) {
                allContent.add(factory.apply(jsonObject));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeExpiredStories() {
        allContent.removeIf(content -> content instanceof Story && ((Story) content).isExpired());
        saveContent();
    }

    public void createPost(String authorId, String content, String imagePath) {
        String contentId = generateUniqueId();
        Post post = new Post(contentId, authorId, content, imagePath, LocalDateTime.now().toString());
        allContent.add(post);
        saveContent();
    }

    public void createStory(String authorId, String content, String imagePath) {
        String contentId = generateUniqueId();
        Story story = new Story(contentId, authorId, content, imagePath, LocalDateTime.now().toString());
        allContent.add(story);
        saveContent();
    }

    private String generateUniqueId() {
        return "CONTENT-" + System.nanoTime();
    }
}
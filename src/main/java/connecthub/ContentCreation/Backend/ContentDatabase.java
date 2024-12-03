package connecthub.ContentCreation.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContentDatabase {
    private static final String POSTS_FILEPATH = "Posts.JSON";
    private static final String STORIES_FILEPATH = "Stories.JSON";
    private static final List<Content> contents = new ArrayList<>();
    private static long counter = 1;

    public static List<Content> getContents() {
        return contents;
    }

    public void saveContent() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Content content : contents) {
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

    public void loadContents() {
        contents.clear();
        loadPosts();
        loadStories();
        removeExpiredStories();
    }

    private void loadPosts() {
        try (InputStream is = new FileInputStream(POSTS_FILEPATH);
             JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            for (JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class)) {
                Post post = Post.fromJson(jsonObject);
                contents.add(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStories() {
        try (InputStream is = new FileInputStream(STORIES_FILEPATH);
             JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            for (JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class)) {
                Story story = Story.fromJson(jsonObject);
                contents.add(story);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeExpiredStories() {
        contents.removeIf(content -> content instanceof Story && ((Story) content).isExpired());
        saveContent();
    }

    public void createPost(String authorId, String content, String imagePath) {
        String contentId = generateId();
        Post post = new Post(contentId, authorId, content, imagePath, LocalDateTime.now().toString());
        contents.add(post);
        saveContent();
    }

    public void createStory(String authorId, String content, String imagePath) {
        String contentId = generateId();
        Story story = new Story(contentId, authorId, content, imagePath, LocalDateTime.now().toString());
        contents.add(story);
        saveContent();
    }



    public static synchronized String generateId() {
        return String.valueOf(counter++);
    }
}
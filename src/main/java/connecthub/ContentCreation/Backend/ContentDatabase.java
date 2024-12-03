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

public class ContentDatabase {
    private static final String CONTENT_FILEPATH = "content.json";
    private final List<Content> contents = new ArrayList<>();
    private static long counter = 1;

    public void saveContents() {
        //loop on contents and make them json object
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Content content : contents) {
            arrayBuilder.add(content.toJson());
        }
        //array list of json object
        JsonArray jsonArray = arrayBuilder.build();
        //write in json file
        try (OutputStream os = new FileOutputStream(CONTENT_FILEPATH);
             JsonWriter jsonWriter = Json.createWriter(os)) {
            //write the jsonArray
            jsonWriter.writeArray(jsonArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadContents() {
        //clear the old one
        contents.clear();
        //jsonReader for reading from json file
        try (InputStream is = new FileInputStream(CONTENT_FILEPATH);
             JsonReader jsonReader = Json.createReader(is)) {
            //read the json array
            JsonArray jsonArray = jsonReader.readArray();
            //loop on every json object
            for (JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class)) {
                //check for type
                String type = jsonObject.getString("type");
                //if post add to the contents a new post
                if ("post".equals(type)) {
                    contents.add(Post.readFromJson(jsonObject));
                    //if story add to the contents a new story
                } else if ("story".equals(type)) {
                    contents.add(Story.readFromJson(jsonObject));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //after loading all remove expired
        removeExpiredStories();
    }

    private void removeExpiredStories() {
        //remove if the content is story & if it's expired
        contents.removeIf(content -> content instanceof Story && ((Story) content).isExpired());
        saveContents();
    }

    public void createPost(String authorId, String content, String imagePath) {
        //make unique id for the content
        String contentId = generateId();
        //make a post
        Post post = new Post(contentId, authorId, content, imagePath, LocalDateTime.now().toString());
        //add it th the contents and save
        contents.add(post);
        saveContents();
    }

    public void createStory(String authorId, String content, String imagePath) {
        //make unique id for the content
        String contentId = generateId();
        //make a story
        Story story = new Story(contentId, authorId, content, imagePath, LocalDateTime.now().toString());
        //add it th the contents and save
        contents.add(story);
        saveContents();
    }

    public static synchronized String generateId() {
        //unique id
        return String.valueOf(counter++);
    }

    public List<Content> getContents() {
        return contents;
    }
}
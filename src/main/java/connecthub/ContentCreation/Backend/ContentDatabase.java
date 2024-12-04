package connecthub.ContentCreation.Backend;

import connecthub.UserAccountManagement.Backend.User;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContentDatabase {
    private static final String CONTENT_FILEPATH = "content.json";
    private static ArrayList<Content> contents = new ArrayList<>();
    private static ContentDatabase contentDatabase = null;

    private ContentDatabase() {

    }

    public static ContentDatabase getInstance() {
        if (contentDatabase == null) {
            contentDatabase = new ContentDatabase();
        }
        return contentDatabase;
    }

    public static void saveContents() {
        //loop on contents and make them json object

        JSONArray contentsArray = new JSONArray();
        for (Content content : contents) {
            contentsArray.put(content.toJson());
        }
        //array list of json object
        //write in json file
        try {
            FileWriter file = new FileWriter(CONTENT_FILEPATH);
            file.write(contentsArray.toString(4));
            file.close();
        } catch (IOException e) {
            System.out.println("Error while saving contents");
        }
    }

    public static ArrayList<Content> loadContents() {
        // Clear the old one
        contents.clear();
        // jsonReader for reading from json file
        try {
            Path contentFile = Paths.get(CONTENT_FILEPATH);

            // If the file doesn't exist, create it
            if (!Files.exists(contentFile)) {
                Files.createFile(contentFile); // Create an empty file
                return contents; // Return empty list if no content exists yet
            }
            // Read the content of the file
            String json = new String(Files.readAllBytes(contentFile));
            JSONArray contentsArray = new JSONArray(json);
            // Loop on every json object
            for (int i = 0; i < contentsArray.length(); i++) {
                // Check for type
                JSONObject jsonObject = contentsArray.getJSONObject(i);

                // If post add to the contents a new post
                if (jsonObject.getString("type").equals("post")) {
                    contents.add(Post.readFromJson(jsonObject));
                    // If story add to the contents a new story
                } else if (jsonObject.getString("type").equals("story")) {
                    contents.add(Story.readFromJson(jsonObject));
                }
            }
            // After loading all remove expired
            removeExpiredStories();

            return contents;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contents;
    }

    public static void removeExpiredStories() {
        //remove if the content is story & if it's expired
        contents.removeIf(content -> content instanceof Story && ((Story) content).isExpired());
        saveContents();
    }

    public static synchronized int generateId() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return contents.size() + 1;
    }

    public static List<Content> getContents() {
        return contents;
    }

}
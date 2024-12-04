package connecthub.ContentCreation.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContentDatabase {
    private static final String CONTENT_FILEPATH = "content.json";
    private static ArrayList<Content> contents = new ArrayList<>();
    private static ContentDatabase contentDatabase = null;

    private ContentDatabase() {

    }

    public static ContentDatabase getInstance() {
        //only one instance
        if (contentDatabase == null) {
            contentDatabase = new ContentDatabase();
        }
        return contentDatabase;
    }

    public static void saveContents() {
        //loop on contents and make them json object
        JSONArray jsonArray = new JSONArray();
        for (Content content : contents) {
            jsonArray.put(content.toJson());
        }
        //write in json file
        try {
            FileWriter file = new FileWriter(CONTENT_FILEPATH);
            file.write(jsonArray.toString(4));
            file.close();
        } catch (IOException e) {
            System.out.println("Error while saving contents");
        }
    }

    public static ArrayList<Content> loadContents() {
        // Clear the old one
        contents.clear();
        try {
            Path pathFile = Paths.get(CONTENT_FILEPATH);
            // check the file exist
            if (!Files.exists(pathFile)) {
                Files.createFile(pathFile);
                return contents;
            }
            // read the file
            String json = new String(Files.readAllBytes(pathFile));
            //make jsonArray
            JSONArray jsonArray = new JSONArray(json);
            // Loop on every json object
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
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

    public static String generateId(String authorId) {
        //the unique id is the place of content
        String time = LocalDateTime.now().toString();
        return authorId + time;
    }

    public static List<Content> getContents() {
        return contents;
    }

}
package connecthub.ContentCreation.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ContentDatabase {

    private static final String CONTENT_FILEPATH = "content.json";
    private ArrayList<Content> contents = new ArrayList<>();
    private static ContentDatabase contentDatabase = null;

    public ArrayList<Content> getContents() {
        return contents;
    }

    private ContentDatabase() {

    }

    public static ContentDatabase getInstance() {
        //only one instance
        if (contentDatabase == null) {
            contentDatabase = new ContentDatabase();
            contentDatabase.loadContents();
        }
        return contentDatabase;
    }

    public static void saveContents() {
        //loop on contents and make them json object
        ContentDatabase contentDB = getInstance();
        JSONArray jsonArray = new JSONArray();
        for (Content content : contentDB.contents) {
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
        ContentDatabase contentDB = getInstance();
        contentDB.contents.clear();
        try {
            Path pathFile = Paths.get(CONTENT_FILEPATH);
            // check the file exist
            if (!Files.exists(pathFile)) {
                Files.createFile(pathFile);
                return contentDB.contents;
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
                    contentDB.contents.add(Post.readFromJson(jsonObject));
                    // If story add to the contents a new story
                } else if (jsonObject.getString("type").equals("story")) {
                    contentDB.contents.add(Story.readFromJson(jsonObject));
                }
            }
            // After loading all remove expired
            removeExpiredStories();
            return contentDB.contents;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentDB.contents;
    }

    public static void removeExpiredStories() {
        //remove if the content is story & if it's expired
        ContentDatabase contentDB = getInstance();
        contentDB.contents.removeIf(content -> content instanceof Story && ((Story) content).isExpired());
        saveContents();
    }

    public static String generateId(String authorId) {
        //the unique id is the place of content
        String time = LocalDateTime.now().toString();
        return authorId + "_" + time;

    }

}
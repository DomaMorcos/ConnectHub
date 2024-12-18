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

    private static final String CONTENT_FILEPATH = "content.JSON";
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

    public void saveContents() {
        ContentDatabase contentDB = getInstance();
        JSONArray jsonArray = new JSONArray();
        for (Content content : contentDB.contents) {
            JSONObject jsonObject = content.toJson();
            System.out.println("Saving content: " + jsonObject.toString(4)); // DEBUG
            jsonArray.put(jsonObject);
        }
        try (FileWriter file = new FileWriter(CONTENT_FILEPATH)) {
            file.write(jsonArray.toString(4));
        } catch (Exception e) {
            System.out.println("Error saving contents: " + e.getMessage());
        }
    }

    public ArrayList<Content> loadContents() {
        ContentDatabase contentDB = getInstance();
        contentDB.contents.clear();
        try {
            Path pathFile = Paths.get(CONTENT_FILEPATH);
            if (!Files.exists(pathFile)) {
                Files.createFile(pathFile);
                return contentDB.contents;
            }
            String json = new String(Files.readAllBytes(pathFile));
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                System.out.println("Loading content: " + jsonObject.toString(4)); // DEBUG
                if (jsonObject.getString("type").equals("post")) {
                    contentDB.contents.add(Post.readFromJson(jsonObject));
                } else if (jsonObject.getString("type").equals("story")) {
                    contentDB.contents.add(Story.readFromJson(jsonObject));
                }
            }
            removeExpiredStories();
            return contentDB.contents;
        } catch (Exception e) {
            System.out.println("Error loading contents: " + e.getMessage());
        }
        return contentDB.contents;
    }

    public static void removeExpiredStories() {
        //remove if the content is story & if it's expired
        ContentDatabase contentDatabase = getInstance();
        contentDatabase.contents.removeIf(content -> content instanceof Story && ((Story) content).isExpired());
        contentDatabase.saveContents();
    }

    public static String generateId(String authorId) {
        //the unique id is the place of content
        String time = LocalDateTime.now().toString();
        return authorId + "_" + time;

    }
}
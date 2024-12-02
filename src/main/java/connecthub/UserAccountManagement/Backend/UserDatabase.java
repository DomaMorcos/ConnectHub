package connecthub.UserAccountManagement.Backend;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class UserDatabase {
    public static ArrayList<User> users = new ArrayList<>();
    public static final String FILEPATH = "User.JSON";

    public static void saveUsersToJsonFile() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (User user : users) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("userId", user.getUserId())
                    .add("email", user.getEmail())
                    .add("username", user.getUsername())
                    .add("dateOfBirth", user.getDateOfBirth())
                    .add("status", user.getStatus());
            arrayBuilder.add(objectBuilder.build());
        }
        JsonArray jsonArray = arrayBuilder.build();

        try (OutputStream os = new FileOutputStream(FILEPATH);
             JsonWriter jsonWriter = Json.createWriter(os)) {
            jsonWriter.writeArray(jsonArray);
        } catch (IOException e) {
            System.err.println("Error while saving users to JSON file: " + e.getMessage());
        }
    }

    public static void readUsersFromJsonFile() {
        users.clear();
        try (InputStream is = new FileInputStream(FILEPATH);
             JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            for (JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class)) {
                // Validate JSON structure before creating User object
                if (jsonObject.containsKey("userId") && jsonObject.containsKey("email") &&
                        jsonObject.containsKey("username") && jsonObject.containsKey("dateOfBirth") &&
                        jsonObject.containsKey("status")) {
                    User user = new User(
                            jsonObject.getString("userId"),
                            jsonObject.getString("email"),
                            jsonObject.getString("username"),
                            "", // Password should not be stored in plain text
                            jsonObject.getString("dateOfBirth"),
                            jsonObject.getString("status")
                    );
                    users.add(user);
                } else {
                    System.err.println("Invalid user data in JSON file: " + jsonObject);
                }
            }
        } catch (IOException e) {
            System.err.println("Error while reading users from JSON file: " + e.getMessage());
        }
    }

    public static User getUser(String email) {
        for (User user:users) {
            if (user.getEmail().equals(email))
                return  user;
        }
        return null; // User not found
    }

    public static boolean contains (String email) {
        for (User user:users) {
            if (user.getEmail().equals(email))
                return  true;
        }
        return false; // Email does not exist
    }

}
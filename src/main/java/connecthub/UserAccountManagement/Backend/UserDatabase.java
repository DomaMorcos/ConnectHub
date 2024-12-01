package connecthub.UserAccountManagement.Backend;

import connecthub.UserAccountManagement.Backend.User;

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
import java.util.List;

public class UserDatabase {
    public static ArrayList<User> users = new ArrayList<>();

    public static void saveUsersToJsonFile(String filePath) {
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

        try (OutputStream os = new FileOutputStream(filePath);
             JsonWriter jsonWriter = Json.createWriter(os)) {
            jsonWriter.writeArray(jsonArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readUsersFromJsonFile(String filePath) {

        users.clear();
        try (InputStream is = new FileInputStream(filePath);
             JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            for (JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class)) {
                User user = new User(jsonObject.getString("userId"), jsonObject.getString("email"), jsonObject.getString("password"), jsonObject.getString("username"), jsonObject.getString("dateOfBirth"), jsonObject.getString("status"));
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
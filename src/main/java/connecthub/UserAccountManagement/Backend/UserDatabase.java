package connecthub.UserAccountManagement.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserDatabase {
    public static ArrayList<User> users = new ArrayList<>();
    public static final String FILEPATH = "User.JSON";

    public static void saveUsersToJsonFile() {
        JSONArray usersArray = new JSONArray();
        for (User user : users) {
            JSONObject j = new JSONObject();
            j.put("userId", user.getUserId());
            j.put("email", user.getEmail());
            j.put("username", user.getUsername());
            j.put("dateOfBirth", user.getDateOfBirth());
            j.put("status", user.getStatus());
            usersArray.put(j);
        }
        try {
            FileWriter file = new FileWriter(FILEPATH);
            file.write(usersArray.toString());
            file.close();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    public static void readUsersFromJsonFile() {
        users.clear();
        try {
            String json = new String(Files.readAllBytes(Paths.get(FILEPATH)));
            JSONArray usersArray = new JSONArray(json);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userJson = usersArray.getJSONObject(i);
                String email = userJson.getString("email");
                String userId = userJson.getString("userId");
                String username = userJson.getString("username");
                LocalDate dateOfBirth = LocalDate.parse(userJson.getString("dateOfBirth"), formatter);
                String password = userJson.getString("password");
                String status = userJson.getString("status");
                User user = new User(userId, email, username, password, dateOfBirth.toString(), status);
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static   User getUser(String email) {
        for (User user:users) {
            if (user.getEmail().equals(email))
                return  user;
        }
        return null;
    }
    public User getUserById(String userId) {
        for (User user:users) {
            if (user.getUserId().equals(userId))
                return  user;
        }
        return null;
    }

    public static boolean contains (String email) {
        for (User user:users) {
            if (user.getEmail().equals(email))
                return  true;
        }
        return false;
    }

}
package connecthub.UserAccountManagement.Backend;

import connecthub.ProfileManagement.Backend.ProfileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserDatabase {
    private static UserDatabase instance; // Singleton instance
    public ArrayList<User> users = new ArrayList<>();
    public static final String FILEPATH = "User.JSON";
    private static UserDatabase userDatabase = null;



    // Private constructor to prevent instantiation
    private UserDatabase() {
    }

    // Public method to provide access to the singleton instance
    public static UserDatabase getInstance() {
        // Only one instance

        if (userDatabase == null) {
            userDatabase = new UserDatabase();
            userDatabase.readUsersFromJsonFile();

        }
        return userDatabase ;

    }

    public static void saveUsersToJsonFile() {
        UserDatabase userDB = UserDatabase.getInstance();
        JSONArray usersArray = new JSONArray();
        for (User user : userDB.users) {
            JSONObject j = new JSONObject();
            j.put("userId", user.getUserId());
            j.put("email", user.getEmail());
            j.put("username", user.getUsername());
            j.put("password", user.getPassword());
            j.put("dateOfBirth", user.getDateOfBirth());
            j.put("status", user.getStatus());
            usersArray.put(j);
        }
        try {
            FileWriter file = new FileWriter(FILEPATH);
            file.write(usersArray.toString(4));
            file.close();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    public void readUsersFromJsonFile() {
        File file = new File(FILEPATH);
        if (!file.exists()) {
            System.out.println("User.JSON file not found. Creating a new file.");
            saveUsersToJsonFile(); // Save an empty user list to create the file
            return;
        }
        UserDatabase userDB = UserDatabase.getInstance();
        userDB.users.clear();
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
                userDB.users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String email) {
        UserDatabase userDB = UserDatabase.getInstance();
        for (User user : userDB.users) {
            if (user.getEmail().equals(email))
                return user;
        }
        return null; // User not found
    }

    public User getUserById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId))
                return user;
        }
        return null;
    }

    public boolean contains(String email) {
        UserDatabase userDB = UserDatabase.getInstance();
        for (User user : userDB.users) {
            if (user.getEmail().equals(email))
                return true;
        }
        return false; // Email does not exist
    }

    public void printUsers() {
        UserDatabase userDB = UserDatabase.getInstance();
        for (User user : userDB.users) {
            System.out.println(user.toString());
        }
    }
}
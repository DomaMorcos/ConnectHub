package connecthub.ProfileManagement.Backend;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import connecthub.UserAccountManagement.Backend.HashPassword;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;

public class ProfileDatabase {
    private static String PROFILE_FILEPATH = "Profiles.JSON";
    private static Map<String, UserProfile> profiles = new HashMap<>();
    private static ProfileDatabase profileDatabase = null;

    public ProfileDatabase() {
    }

    public static ProfileDatabase getInstance() {
        // Only one instance
        if (profileDatabase == null) {
            profileDatabase = new ProfileDatabase();
            profileDatabase.loadProfiles();
        }
        return profileDatabase;
    }
    public UserProfile getProfile(String userId) {
        loadProfiles();
        return profiles.get(userId);
    }
    public void updatePassword(String userId, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        UserDatabase userDatabase = UserDatabase.getInstance();
        User user = userDatabase.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found for ID: " + userId);
        }

        user.setPassword(HashPassword.hashPassword(newPassword));
        UserDatabase.saveUsersToJsonFile();
    }


    public void updateProfile(UserProfile profile) {
        if (profile == null || profile.getUserId() == null || profile.getUserId().isEmpty()) {
            throw new IllegalArgumentException("Invalid profile data.");
        }

        loadProfiles();
        profiles.put(profile.getUserId(), profile);
        saveProfilesToJsonFile();
    }
    public void saveProfilesToJsonFile() {
        JSONArray profilesArray = new JSONArray();
        for (UserProfile profile : profiles.values()) {
            JSONObject profileObject = new JSONObject();
            profileObject.put("userId", profile.getUserId());
            profileObject.put("profilePhotoPath", profile.getProfilePhotoPath());
            profileObject.put("coverPhotoPath", profile.getCoverPhotoPath());
            profileObject.put("bio", profile.getBio());

            // Add the friends list to the profile object
            JSONArray friendsArray = new JSONArray(profile.getFriends());
            profileObject.put("friends", friendsArray);

            profilesArray.put(profileObject);
        }

        try (FileWriter file = new FileWriter("Profiles.JSON")) {
            file.write(profilesArray.toString(4)); // Pretty print the JSON
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadProfiles() {
        File file = new File(PROFILE_FILEPATH);
        if (!file.exists()) {
            System.out.println("Profiles file not found. Creating a new file.");
            saveProfilesToJsonFile(); // Save an empty profiles file
            return;
        }
        try {
            // Read the JSON file into a string
            profiles.clear();
            String json = new String(Files.readAllBytes(Paths.get(PROFILE_FILEPATH)));
            JSONArray profilesArray = new JSONArray(json); // Parse the JSON array
            for (int i = 0; i < profilesArray.length(); i++) {
                JSONObject profileObject = profilesArray.getJSONObject(i);
                JSONArray friendsArray = profileObject.getJSONArray("friends"); // Get the friends array
                List<String> friends = new ArrayList<>();
                for (int j = 0; j < friendsArray.length(); j++) {
                    friends.add(friendsArray.getString(j));
                }
                String userId =profileObject.getString("userId");
                String profilePhotoPath =profileObject.getString("profilePhotoPath");
                String coverPhotoPath =profileObject.getString("coverPhotoPath");
                String bio =profileObject.getString("bio");
                UserProfile profile = new UserProfile(userId,profilePhotoPath,coverPhotoPath,bio,friends);
                // Add the profile to the map
                profiles.put(profile.getUserId(), profile);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
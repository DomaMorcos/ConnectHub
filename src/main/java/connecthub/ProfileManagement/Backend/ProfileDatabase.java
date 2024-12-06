package connecthub.ProfileManagement.Backend;

import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.UserAccountManagement.Backend.HashPassword;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ProfileDatabase {
    private static final String PROFILE_FILEPATH = "Profiles.JSON";
    private static Map<String, UserProfile> profiles = new HashMap<>();
    private static ProfileDatabase profileDatabase = null;

    public ProfileDatabase() {
    }

    public static ProfileDatabase getInstance() {
        if (profileDatabase == null) {
            profileDatabase = new ProfileDatabase();
        }
        return profileDatabase;
    }

    public UserProfile getProfile(String userId) {
        loadProfiles();
        UserProfile profile = profiles.get(userId);
        if (profile == null) {
            throw new IllegalArgumentException("Profile not found for user ID: " + userId);
        }
        return profile;
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
        userDatabase.saveUsersToJsonFile();
    }

    public void updateProfile(UserProfile profile) {
        if (profile == null || profile.getUserId() == null || profile.getUserId().isEmpty()) {
            throw new IllegalArgumentException("Invalid profile data.");
        }

        loadProfiles();
        profiles.put(profile.getUserId(), profile);
        saveProfilesToJsonFile();
    }
//
//    public void saveProfilesToJsonFile() {
//        JSONArray profilesArray = new JSONArray();
//        for (UserProfile profile : profiles.values()) {
//            JSONObject profileObject = new JSONObject();
//            JSONArray friendsArray = new JSONArray();
//
//            List<User> friends = FriendManager.getInstance().getFriendsList(profile.getUserId());
//            for (User friend : friends) {
//                friendsArray.put("UserId:" + friend.getUserId());
//            }
//
//            profileObject.put("userId", profile.getUserId());
//            profileObject.put("profilePhotoPath", profile.getProfilePhotoPath().getDescription());
//            profileObject.put("coverPhotoPath", profile.getCoverPhotoPath().getDescription());
//            profileObject.put("bio", profile.getBio());
//            profileObject.put("friends", friendsArray);
//
//            profilesArray.put(profileObject);
//        }
//
//        try (FileWriter file = new FileWriter(PROFILE_FILEPATH)) {
//            file.write(profilesArray.toString(4));
//            file.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
public void saveProfilesToJsonFile() {
    System.out.println("Saving profiles to JSON...");

    JSONArray profilesArray = new JSONArray();
    for (UserProfile profile : profiles.values()) {
        JSONObject profileObject = new JSONObject();
        profileObject.put("userId", profile.getUserId());
        profileObject.put("profilePhotoPath", profile.getProfilePhotoPath().getDescription());
        profileObject.put("coverPhotoPath", profile.getCoverPhotoPath().getDescription());
        profileObject.put("bio", profile.getBio());

        // Add friends list
        JSONArray friendsArray = new JSONArray();
        for (User friend : profile.getFriends()) {
            friendsArray.put("UserId:" + friend.getUserId());
        }
        profileObject.put("friends", friendsArray);

        profilesArray.put(profileObject);
    }

    try (FileWriter file = new FileWriter(PROFILE_FILEPATH)) {
        file.write(profilesArray.toString(4));
        file.flush();
        System.out.println("Profiles saved successfully!");
    } catch (IOException e) {
        System.err.println("Error saving profiles: " + e.getMessage());
    }
}

    public void loadProfiles() {
        File file = new File(PROFILE_FILEPATH);
        if (!file.exists()) {
            System.out.println("Profiles file not found. Creating a new file.");
            saveProfilesToJsonFile();
            return;
        }
        try {
            profiles.clear();
            String json = new String(Files.readAllBytes(Paths.get(PROFILE_FILEPATH)));
            JSONArray profilesArray = new JSONArray(json);
            for (int i = 0; i < profilesArray.length(); i++) {
                JSONObject profileObject = profilesArray.getJSONObject(i);
                JSONArray friendsArray = profileObject.getJSONArray("friends");
                List<String> friends = new ArrayList<>();
                for (int j = 0; j < friendsArray.length(); j++) {
                    friends.add(friendsArray.getString(j));
                }
                String userId = profileObject.getString("userId");
                String profilePhotoPath = profileObject.optString("profilePhotoPath", "");
                String coverPhotoPath = profileObject.optString("coverPhotoPath", "");
                String bio = profileObject.getString("bio");

                UserProfile profile = new UserProfile(
                        userId,
                        new ImageIcon(profilePhotoPath.isEmpty() ? "default.jpg" : profilePhotoPath),
                        new ImageIcon(coverPhotoPath.isEmpty() ? "defaultCover.jpg" : coverPhotoPath),
                        bio,
                        friends
                );

                profiles.put(profile.getUserId(), profile);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

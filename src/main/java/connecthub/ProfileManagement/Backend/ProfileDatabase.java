package connecthub.ProfileManagement.Backend;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileDatabase {
    private static String PROFILE_FILEPATH = "Profiles.JSON";
    private static Map<String, UserProfile> profiles = new HashMap<>();
    private static ProfileDatabase profileDatabase = null;

    public ProfileDatabase() {
    }
    public static ProfileDatabase getInstance() {
        //only one instance
        if (profileDatabase == null) {
            profileDatabase = new ProfileDatabase();
            profileDatabase.loadProfiles();
        }
        return profileDatabase;
    }

    public void updateProfile(UserProfile profile) {
        profiles.put(profile.getUserId(), profile);
        saveProfilesToJsonFile();
    }

    public static void saveProfilesToJsonFile() {
        JSONArray profilesArray = new JSONArray(); // Create a JSONArray to store profiles

        for (UserProfile profile : profiles.values()) {
            JSONObject profileObject = new JSONObject(); // Create a JSONObject for each profile
            JSONArray friendsArray = new JSONArray(); // Create a JSONArray for friends

            // Add friends to the friendsArray
            for (String friend : UserProfile.getFriends()) {
                friendsArray.put(friend);
            }

            // Populate the profileObject
            profileObject.put("userId", profile.getUserId());
            profileObject.put("profilePhotoPath", profile.getProfilePhotoPath());
            profileObject.put("coverPhotoPath", profile.getCoverPhotoPath());
            profileObject.put("bio", profile.getBio());
            profileObject.put("friends", friendsArray); // Add the friends array

            profilesArray.put(profileObject); // Add the profile to the profiles array
        }

        // Write the profiles array to a JSON file
        try (FileWriter file = new FileWriter(PROFILE_FILEPATH)) {
            file.write(profilesArray.toString(4)); // Use indentation for better readability
            file.flush();
        } catch (IOException e) {
            e.printStackTrace(); // Print the exception stack trace
        }
    }


    void loadProfiles() {
        File file = new File(PROFILE_FILEPATH);
        if (!file.exists()) {
            System.out.println("Profiles file not found. Creating a new file.");
            saveProfilesToJsonFile(); // Save an empty profiles file
            return;
        }

        try {
            // Read the JSON file into a string
            String json = new String(Files.readAllBytes(Paths.get(PROFILE_FILEPATH)));
            JSONArray profilesArray = new JSONArray(json); // Parse the JSON array

            for (int i = 0; i < profilesArray.length(); i++) {
                JSONObject jsonObject = profilesArray.getJSONObject(i); // Get each profile object
                JSONArray friendsArray = jsonObject.getJSONArray("friends"); // Get the friends array

                // Convert JSONArray to a List<String>
                List<String> friends = new ArrayList<>();
                for (int j = 0; j < friendsArray.length(); j++) {
                    friends.add(friendsArray.getString(j));
                }

                // Create the UserProfile object
                UserProfile profile = new UserProfile(
                        jsonObject.getString("userId"),
                        jsonObject.getString("profilePhotoPath"),
                        jsonObject.getString("coverPhotoPath"),
                        jsonObject.getString("bio"),
                        friends
                );

                // Add the profile to the map
                profiles.put(profile.getUserId(), profile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
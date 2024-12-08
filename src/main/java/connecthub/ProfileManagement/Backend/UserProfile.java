package connecthub.ProfileManagement.Backend;

import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.UserAccountManagement.Backend.User;

import javax.swing.*;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class UserProfile implements Serializable {
    private final String userId;
    private String profilePhotoPath;
    private String coverPhotoPath;
    private String bio;
    private FriendManager friendManager = FriendManager.getInstance();
    private List<String> friends; // Keep a list of friends in this class as well.

    public UserProfile(String userId, String profilePhotoPath, String coverPhotoPath, String bio, List<String> friends) {
        this.userId = userId;
        this.profilePhotoPath = profilePhotoPath;
        this.coverPhotoPath = coverPhotoPath;
        this.bio = bio;
        this.friends = new ArrayList<>(friends);

        // Initialize friends in FriendManager
        friendManager.initializeFriends(userId, friends);
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }


    public String getCoverPhotoPath() {
        return coverPhotoPath;
    }

    public void setCoverPhotoPath(String coverPhotoPath) {
        this.coverPhotoPath = coverPhotoPath;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    // Friend Management Methods
    public List<String> getFriends() {
        return friends;
    }

    public void addFriend(String friendId) {
        if (!friends.contains(friendId)) {
            friends.add(friendId);  // Add friend to list
            updateProfileAndSave();  // Update the profile and save
        }
    }

    public void deleteFriend(String friendId) {
        if (friends.contains(friendId)) {
            friends.remove(friendId);  // Remove friend from list
            updateProfileAndSave();  // Update the profile and save
        }
    }

    private void updateProfileAndSave() {
        // Save the updated profile after modifying friends list
        ProfileDatabase profileDb = ProfileDatabase.getInstance();
        profileDb.updateProfile(this);  // Update profile in database
        profileDb.saveProfilesToJsonFile();  // Save to JSON file
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", profilePhotoPath='" + profilePhotoPath + '\'' +
                ", coverPhotoPath='" + coverPhotoPath + '\'' +
                ", bio='" + bio + '\'' +
                ", friends=" + friends +
                '}';
    }
}

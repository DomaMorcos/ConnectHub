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
import java.util.List;

public class UserProfile implements Serializable {
    private final String userId;
    private ImageIcon profilePhoto;
    private ImageIcon coverPhoto;
    private String bio;

    public UserProfile(String userId, ImageIcon profilePhoto, ImageIcon coverPhoto, String bio, List<String> friends) {
        this.userId = userId;
        this.profilePhoto = profilePhoto;
        this.coverPhoto = coverPhoto;
        this.bio = bio;

        // Initialize friends in FriendManager
        FriendManager.getInstance().initializeFriends(userId, friends);
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    public ImageIcon getProfilePhotoPath() {
        return profilePhoto;
    }
    public void setProfilePhotoPath(String profilePhotoPath) {
        Path source = Paths.get(profilePhotoPath);
        Path destination = Paths.get("imagesDatabase/ProfilePicture/" + userId + ".png");
        try {
            if (Files.exists(source)) {
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } else {
                System.out.println("Profile photo path does not exist: " + profilePhotoPath);
                this.profilePhoto = new ImageIcon("imagesDatabase/defaultProfile.png"); // Set a default image
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ImageIcon getCoverPhotoPath() {
        return coverPhoto;
    }
    public void setCoverPhotoPath(String coverPhotoPath) {
        Path source = Paths.get(coverPhotoPath);
        Path destination = Paths.get("imagesDatabase/CoverPicture/" + userId + ".png");
        try {
            if (Files.exists(source)) {
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } else {
                System.out.println("Cover photo path does not exist: " + coverPhotoPath);
                this.coverPhoto = new ImageIcon("imagesDatabase/defaultCover.png"); // Set a default image
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    // Friend Management Methods
    public List<User> getFriends() {
        return FriendManager.getInstance().getFriendsList(userId);
    }
    public void addFriend(String friendId) {
        FriendManager.getInstance().addFriend(userId, friendId);
    }

    public void deleteFriend(String friendId) {
        FriendManager.getInstance().removeFriend(userId, friendId);
    }

    @Override
    public String toString() {
        // Get the list of friends using the getFriends method
        List<User> friendsList = getFriends();

        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", profilePhotoPath='" + profilePhoto.getDescription() + '\'' +
                ", coverPhotoPath='" + coverPhoto.getDescription() + '\'' +
                ", bio='" + bio + '\'' +
                ", friends=" + friendsList +
                '}';
    }
}
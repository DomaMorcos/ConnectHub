package connecthub.ProfileManagement.Backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserProfile implements Serializable {
    private final String userId;
    private String profilePhotoPath;
    private String coverPhotoPath;
    private String bio;
    private static List<String> friends = new ArrayList<>();


    public UserProfile(String userId, String profilePhotoPath, String coverPhotoPath, String bio,List<String> friends) {
        this.userId = userId;
        this.profilePhotoPath = profilePhotoPath;
        this.coverPhotoPath = coverPhotoPath;
        this.bio = bio;
        UserProfile.friends = friends != null ? friends : new ArrayList<>();
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

    public static List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        UserProfile.friends = friends;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return String.format("UserProfile{id=%s, bio=%s}", userId, bio);
    }
}

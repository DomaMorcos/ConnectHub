package connecthub.ProfileManagement.Backend;

import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.UserAccountManagement.Backend.User;

import java.io.Serializable;
import java.util.List;

public class UserProfile implements Serializable {
    private final String userId;
    private String profilePhotoPath;
    private String coverPhotoPath;
    private String bio;

    public UserProfile(String userId, String profilePhotoPath, String coverPhotoPath, String bio, List<String> friends) {
        this.userId = userId;
        this.profilePhotoPath = profilePhotoPath;
        this.coverPhotoPath = coverPhotoPath;
        this.bio = bio;

        // Initialize friends in FriendManager
        FriendManager.getInstance().initializeFriends(userId, friends);
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
    public List<User> getFriends() {

        return FriendManager.getInstance().getFriendsList(userId);

    }
    public void addFriend(String friendId) {
        connecthub.FriendManagement.Backend.FriendManager.getInstance().addFriend(userId, friendId);
    }

    public void deleteFriend(String friendId) {
        connecthub.FriendManagement.Backend.FriendManager.getInstance().removeFriend(userId, friendId);
    }

    @Override
    public String toString() {
        // Get the list of friends using the getFriends method
        List<User> friendsList = getFriends();

        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", profilePhotoPath='" + profilePhotoPath + '\'' +
                ", coverPhotoPath='" + coverPhotoPath + '\'' +
                ", bio='" + bio + '\'' +
                ", friends=" + friendsList +
                '}';
    }


}
package connecthub.ProfileManagement.Backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserProfile implements Serializable {
    private final String userId;
    private String profilePhotoPath;
    private String coverPhotoPath;
    private String bio;
    private List<String> friends;
    private List<String> blockedUsers;

    public UserProfile(String userId, String profilePhotoPath, String coverPhotoPath, String bio, List<String> friends, List <String> blockedUsers) {
        this.userId = userId;
        this.profilePhotoPath = profilePhotoPath;
        this.coverPhotoPath = coverPhotoPath;
        this.bio = bio;
        this.friends = new ArrayList<>(friends);
        this.blockedUsers = new ArrayList<>(blockedUsers);
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

    public List<String> getBlockedUsers() {
        return blockedUsers;
    }

    public void blockFriend(String blockedUserId) {
        if (!blockedUsers.contains(blockedUserId)) {
            blockedUsers.add(blockedUserId);  // Add blockedUser to list
            updateProfileAndSave();  // Update the profile and save
        }
    }

    public void unblockFriend(String blockedUserId) {
        if (blockedUsers.contains(blockedUserId)) {
            blockedUsers.remove(blockedUserId);  // Remove friend from list
            updateProfileAndSave();  // Update the profile and save
        }
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
                ", blockedUsers=" + blockedUsers +
                '}';
    }
}

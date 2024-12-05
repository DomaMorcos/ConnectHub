package connecthub.ProfileManagement.Backend;

import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.UserAccountManagement.Backend.HashPassword;
import org.json.JSONObject;
import connecthub.ContentCreation.Backend.*;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;

import java.util.*;

public class ProfileManager {
    private ContentDatabase contentDatabase = ContentDatabase.getInstance();
    private UserDatabase userDatabase = UserDatabase.getInstance();
    private  ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
    private  FriendManager friendManager = FriendManager.getInstance();

    public ProfileManager(ContentDatabase contentDb, UserDatabase userDb) {
        this.contentDatabase = contentDb;
        this.userDatabase = userDb;
        profileDatabase.loadProfiles();
    }


    public List<JSONObject> getFriendsWithStatus(String email) {
        User user = userDatabase.getUser(email);
        if (user == null) {
            return new ArrayList<>();
        }
        List<JSONObject> friendsWithStatus = new ArrayList<>();
        // Use FriendManager to get friends list for the user
        List<String> friends = friendManager.getFriendsList(user.getUserId());
        for (String friendId : friends) {
            User friend = userDatabase.getUserById(friendId);
            if (friend != null) {
                JSONObject friendInfo = new JSONObject();
                friendInfo.put("email", friend.getEmail());
                friendInfo.put("username", friend.getUsername());
                friendInfo.put("status", friend.getStatus());
                friendsWithStatus.add(friendInfo);
            }
        }
        return friendsWithStatus;
    }
}
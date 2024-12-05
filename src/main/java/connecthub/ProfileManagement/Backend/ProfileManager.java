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
    private static final String PROFILE_FILEPATH = "Profiles.JSON";
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
            System.err.println("User not found for email: " + email);
            return new ArrayList<>();
        }

        List<JSONObject> friendsWithStatus = new ArrayList<>();
        List<User> friends = friendManager.getFriendsList(user.getUserId()); // Now returns User objects

        for (User friend : friends) {
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
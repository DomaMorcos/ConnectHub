package connecthub.NotificationSystem.backend;

import connecthub.UserAccountManagement.Backend.UserDatabase;
import org.json.JSONObject;

public class FriendRequestNotification extends Notification {
    private String senderId;
    UserDatabase userDatabase = UserDatabase.getInstance();

    public FriendRequestNotification(String userId, String senderId) {
        UserDatabase userDatabase = UserDatabase.getInstance();
        super(userId, "You received a friend request from username: " + userDatabase.getUserById(senderId).getUsername());
        this.senderId = senderId;
    }

    @Override
    public String getType() {
        return "FriendRequest";
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson();
        obj.put("senderId", senderId);
        return obj;
    }

    @Override
    public String getGroupId() {
        return "";
    }
    // Getters
    public String getSenderId() {
        return senderId;
    }
}
package connecthub.NotificationSystem.backend;

import org.json.JSONObject;

public class FriendRequestNotification extends Notification {
    private String senderId;

    public FriendRequestNotification(String userId, String senderId) {
        super(userId, "You received a friend request from user ID: " + senderId);
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
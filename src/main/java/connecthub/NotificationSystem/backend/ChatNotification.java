package connecthub.NotificationSystem.backend;

import org.json.JSONObject;

public class ChatNotification extends Notification {
    private String senderId;
    private String message;

    public ChatNotification(String userId, String senderId, String message) {
        super(userId, "New message from " + senderId + ": " + message);
        this.senderId = senderId;
        this.message = message;
    }

    @Override
    public String getType() {
        return "Chat";
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson();
        obj.put("senderId", senderId);
        obj.put("message", message);
        return obj;
    }

    @Override
    public String getGroupId() {
        return "";
    }
    @Override
    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }
}
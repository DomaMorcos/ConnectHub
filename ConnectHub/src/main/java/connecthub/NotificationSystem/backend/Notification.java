package connecthub.NotificationSystem.backend;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class Notification implements NotificationSystem{
    private String notificationId;
    private String userId; // The user receiving the notification
    private String message;  // Notification message
    private String type;   // Notification type (subclass will set the specific type)
    private String timestamp;
    private NotificationDatabase notificationDatabase = NotificationDatabase.getInstance();

    public Notification(String userId, String message) {
        this.notificationId = generateNotificationId();
        this.userId = userId;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
        this.type = getType(); // Each child class can define this
    }

    // Generate a unique ID for the notification
    private String generateNotificationId() {
        if (notificationDatabase.notifications == null) {
            notificationDatabase.notifications = new ArrayList<>();
        }
        return Integer.toString(notificationDatabase.notifications.size() + 1); // Simplified unique ID generation
    }

    // Convert the notification to JSON for storage/retrieval
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("notificationId", notificationId);
        obj.put("userId", userId);
        obj.put("message", message);
        obj.put("type", type);
        obj.put("timestamp", timestamp);
        return obj;
    }

    // Getters for shared fields
    public String getNotificationId() {
        return notificationId;
    }
    public String getUserId() {
        return userId;
    }
    public String getMessage() {
        return message;
    }
    public String getType() {
        return type;
    }
    public String getTimestamp() {
        return timestamp;
    }
}
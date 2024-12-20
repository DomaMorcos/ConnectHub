package connecthub.NotificationSystem.backend;

import org.json.JSONObject;

public interface NotificationSystem {
    String getNotificationId();
    String getUserId();
    String getMessage();
    String getType();
    String getTimestamp();
    JSONObject toJson();
    String getGroupId();
    String getSenderId();
}
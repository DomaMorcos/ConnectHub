package connecthub.NotificationSystem.backend;

import connecthub.Groups.Backend.GroupDatabase;
import org.json.JSONObject;

public class NewPostNotification extends Notification {
    private String groupId;

    public NewPostNotification(String userId, String groupId) {
        GroupDatabase groupDatabase = GroupDatabase.getInstance();
        super(userId, "New post added in Group: " + groupDatabase.getGroupById(groupId).getName());
        this.groupId = groupId;
    }

    @Override
    public String getType() {
        return "NewPost";
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson();
        obj.put("groupId", groupId);
        return obj;
    }

    // Getters
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getSenderId() {
        return "";
    }
}
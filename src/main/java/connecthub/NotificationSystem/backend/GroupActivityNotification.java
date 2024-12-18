package connecthub.NotificationSystem.backend;

import org.json.JSONObject;

public class GroupActivityNotification extends Notification {
    private String groupId;
    private String activity; // Description of the group activity

    public GroupActivityNotification(String userId, String groupId, String activity) {
        super(userId, "Group Activity: " + activity + " in Group ID: " + groupId);
        this.groupId = groupId;
        this.activity = activity;
    }

    @Override
    public String getType() {
        return "GroupActivity";
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson();
        obj.put("groupId", groupId);
        obj.put("activity", activity);
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

    public String getActivity() {
        return activity;
    }
}
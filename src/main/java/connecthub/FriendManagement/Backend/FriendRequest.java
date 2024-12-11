package connecthub.FriendManagement.Backend;

import org.json.JSONObject;

public class FriendRequest {
    private String senderId;
    private String receiverId;
    private String status; // Pending, Accepted, Declined

    public FriendRequest(String senderId, String receiverId, String status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }

    // Convert to JSON Object
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("senderId", senderId);
        jsonObject.put("receiverId", receiverId);
        jsonObject.put("status", status);
        return jsonObject;
    }

    // Create FriendRequest from JSON
    public static FriendRequest fromJson(JSONObject jsonObject) {
        return new FriendRequest(
                jsonObject.getString("senderId"),
                jsonObject.getString("receiverId"),
                jsonObject.getString("status")
        );
    }

    // Getters and Setters
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @Override
    public String toString() {
        return "FriendRequest{senderId='" + senderId + "', receiverId='" + receiverId + "', status='" + status + "'}";
    }
}

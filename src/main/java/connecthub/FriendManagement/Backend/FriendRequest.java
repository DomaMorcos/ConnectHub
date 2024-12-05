package connecthub.FriendManagement.Backend;

public class FriendRequest {
    private String senderId;
    private String receiverId;
    private String status; // Possible values: "Pending", "Accepted", "Declined"

    public FriendRequest(String senderId, String receiverId, String status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendRequest{senderId='%s', receiverId='%s', status='%s'}".formatted(senderId, receiverId, status);
    }
}


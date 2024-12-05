package connecthub.FriendManagement.Backend;

public class FriendStatus {
    private String friendId;
    private String status; // online/offline

    public FriendStatus(String friendId, String status) {
        this.friendId = friendId;
        this.status = status;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "FriendStatus{friendId='" + friendId + "', status='" + status + "'}";
    }

}

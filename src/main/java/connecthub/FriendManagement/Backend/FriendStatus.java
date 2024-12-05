package connecthub.FriendManagement.Backend;

import connecthub.UserAccountManagement.Backend.User;

public class FriendStatus {
    private String friendId;
    private String status; // online/offline

    public FriendStatus(User friendId, String status) {
        this.friendId = friendId.getUserId();
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
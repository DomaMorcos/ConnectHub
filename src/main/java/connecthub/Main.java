import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.FriendManagement.Backend.FriendRequest;
import connecthub.UserAccountManagement.Backend.CreateUser;
import connecthub.UserAccountManagement.Backend.User;

import java.util.List;
public class Main {
    public static void main(String[] args) {
        // Load existing friend requests from the JSON file
        FriendRequest.loadRequestsFromJson();

        // Create and initialize users for testing
        CreateUser createUser = new CreateUser();
        createUser.signup("user1@example.com", "Alice", "Password123", "1990-01-01");
        createUser.signup("user2@example.com", "Bob", "Password123", "1991-01-01");
        createUser.signup("user3@example.com", "Charlie", "Password123", "1992-01-01");

        // Test sending friend requests
        System.out.println("\n--- Sending Friend Requests ---");
        FriendRequest.sendFriendRequest("1", "3"); // Alice sends request to Charlie
        FriendRequest.sendFriendRequest("2", "3"); // Bob sends request to Charlie
        FriendRequest.sendFriendRequest("1", "2"); // Alice sends request to Bob

        // Test saving friend requests to JSON
        System.out.println("\n--- Saving Friend Requests to JSON ---");
        FriendRequest.saveRequestsToJson();

        // Load and display pending requests for a specific user
        System.out.println("\n--- Loading and Displaying Pending Requests for User 3 ---");
        FriendRequest.loadRequestsFromJson();
        List<FriendRequest> pendingRequestsForUser3 = FriendRequest.getPendingRequests("3");
        if (pendingRequestsForUser3.isEmpty()) {
            System.out.println("No pending friend requests for User 3.");
        } else {
            for (FriendRequest request : pendingRequestsForUser3) {
                System.out.println(request.getSenderId() + " sent a friend request to " + request.getReceiverId());
            }
        }

        // Test responding to a friend request
        System.out.println("\n--- Responding to Friend Requests for User 3 ---");
        FriendRequest.respondToRequest("3", "1", true); // Charlie accepts Alice's request
        FriendRequest.respondToRequest("3", "2", false); // Charlie declines Bob's request

        // Save the updated friend requests
        System.out.println("\n--- Saving Updated Friend Requests to JSON ---");
        FriendRequest.saveRequestsToJson();

        // Display the updated friend list for Alice
        System.out.println("\n--- Updated Friends of Alice (User 1) ---");
        List<User> friendsOfAlice = FriendManager.getInstance().getFriendsList("1");
        for (User friend : friendsOfAlice) {
            System.out.println(friend);
        }

        // Display the updated pending requests for Charlie (should be empty)
        System.out.println("\n--- Pending Requests for User 3 (Charlie) After Response ---");
        List<FriendRequest> updatedPendingRequests = FriendRequest.getPendingRequests("3");
        if (updatedPendingRequests.isEmpty()) {
            System.out.println("No pending friend requests for User 3.");
        } else {
            for (FriendRequest request : updatedPendingRequests) {
                System.out.println(request.getSenderId() + " sent a friend request to " + request.getReceiverId());
            }
        }
    }
}
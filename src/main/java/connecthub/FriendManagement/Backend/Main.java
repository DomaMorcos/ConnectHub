package connecthub.FriendManagement.Backend;

import connecthub.FriendManagement.Backend.*;
import connecthub.UserAccountManagement.Backend.*;
import org.json.JSONObject;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize User Database and Friend Manager
            UserDatabase userDatabase = UserDatabase.getInstance();
            FriendManager friendManager = FriendManager.getInstance();

            // Create test users
            System.out.println("Creating users...");
            CreateUser userCreator = new CreateUser();
            for (int i = 1; i <= 10; i++) { // Create 10 test users
                String email = "user" + i + "@example.com";
                String username = "user" + i;
                String password = "password" + i;
                String dob = "2000-01-" + (i % 28 + 1); // Simple fixed DOB format
                userCreator.signup(email, username, password, dob);
            }
            System.out.println("Users created: " + userDatabase.users.size());

            // Test Friend Manager Functionality
            System.out.println("\n--- Testing Friend Management ---");

            // Add Friends
            System.out.println("\nAdding friends...");
            for (int i = 1; i < 10; i++) {
                friendManager.addFriend(String.valueOf(i), String.valueOf(i + 1)); // Chain friendships
            }
            System.out.println("Friendships created.");

            // Friend Requests
            System.out.println("\nSending friend requests...");
            friendManager.sendFriendRequest("1", "10");
            friendManager.sendFriendRequest("3", "6");
            System.out.println("Friend requests sent.");

            // Accept Friend Requests
            System.out.println("\nResponding to friend requests...");
            friendManager.respondToRequest("10", "1", true); // Accept request from user 1 to user 10
            friendManager.respondToRequest("6", "3", false); // Decline request from user 3 to user 6
            System.out.println("Friend requests responded.");

            // Blocking Users
            System.out.println("\nBlocking users...");
            friendManager.blockUser("2", "3"); // User 2 blocks user 3
            friendManager.blockUser("5", "7"); // User 5 blocks user 7
            System.out.println("Users blocked.");

            // Unblocking Users
            System.out.println("\nUnblocking users...");
            friendManager.unblockUser("2", "3"); // User 2 unblocks user 3
            System.out.println("Users unblocked.");

            // Retrieve Friend Lists
            System.out.println("\nRetrieving friend lists...");
            for (int i = 1; i <= 10; i++) {
                List<User> friends = friendManager.getFriendsList(String.valueOf(i));
                System.out.println("User " + i + " friends: " + friends);
            }

            // Suggest Friends
            System.out.println("\nSuggesting friends...");
            for (int i = 1; i <= 5; i++) {
                List<User> suggestions = friendManager.suggestFriends(String.valueOf(i));
                System.out.println("Friend suggestions for user " + i + ": " + suggestions);
            }

            // Save and Load Friend Data
            System.out.println("\nSaving friends to JSON...");
            FriendRequest.saveDataToJson();

            System.out.println("Loading friends from JSON...");
            FriendRequest.loadDataFromJson();

            System.out.println("\n--- Friend Management Testing Complete ---");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

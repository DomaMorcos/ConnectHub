package connecthub;

import connecthub.ContentCreation.Backend.*;
import connecthub.FriendManagement.Backend.*;
import connecthub.ProfileManagement.Backend.*;
import connecthub.UserAccountManagement.Backend.*;

import java.util.ArrayList;

public class TestMain {
    public static void main(String[] args) {
        // Initialize databases
        UserDatabase userDatabase = UserDatabase.getInstance();
        ContentDatabase contentDatabase = ContentDatabase.getInstance();
        FriendManager friendManager = FriendManager.getInstance();
        ProfileDatabase profileDatabase = ProfileDatabase.getInstance();

        // User creation and sign-up
        CreateUser createUser = new CreateUser();
        createUser.signup("user1@example.com", "user1", "Password123", "1990-01-01");
        createUser.signup("user2@example.com", "user2", "Password123", "1991-01-01");
        createUser.signup("user3@example.com", "user3", "Password123", "1992-01-01");
        createUser.signup("user4@example.com", "user4", "Password123", "1993-01-01");

        // Print all users
        System.out.println("\n--- All Users ---");
        userDatabase.printUsers();

        // Log in users
        LogUser logUser = new LogUser();
        logUser.login("user1@example.com", "Password123");
        logUser.login("user2@example.com", "Password123");

        // Print statuses after login
        System.out.println("\n--- User Status After Login ---");
        userDatabase.printUsers();

        // Create Content
        ContentFactory contentFactory = ContentFactory.getInstance();
        contentFactory.createContent("Post", "1", "First Post!", "path/to/image1.jpg");
        contentFactory.createContent("Story", "1", "My First Story!", "path/to/image2.jpg");

        // Fetch and print all contents
        GetContent getContent = GetContent.getInstance();
        System.out.println("\n--- All Posts ---");
        for (Post post : getContent.getAllPosts()) {
            System.out.println(post);
        }
        System.out.println("\n--- All Stories ---");
        for (Story story : getContent.getAllStories()) {
            System.out.println(story);
        }

        // Friend management
        friendManager.addFriend("1", "2");
        friendManager.sendFriendRequest("1", "3"); // User 1 sends a friend request to User 3
        friendManager.sendFriendRequest("1", "4"); // User 1 sends a friend request to User 4
        friendManager.sendFriendRequest("2", "3"); // User 2 sends a friend request to User 3

        // Display pending requests for User 3
        System.out.println("\n--- Pending Friend Requests for User 3 ---");
        for (FriendRequest request : FriendRequest.getPendingRequests("3")) {
            System.out.println(request.getSenderId() + " sent a friend request to " + request.getReceiverId());
        }

        // Respond to friend requests
        FriendRequest.respondToRequest("3", "1", true); // User 3 accepts request from User 1
        FriendRequest.respondToRequest("3", "2", false); // User 3 rejects request from User 2

        // Display updated friends of User 1
        System.out.println("\n--- Friends of User 1 ---");
        for (User friend : friendManager.getFriendsList("1")) {
            System.out.println(friend);
        }

        // Update profiles
        UserProfile user1Profile = profileDatabase.getProfile("1");
        user1Profile.setBio("Updated Bio for User 1");
        profileDatabase.updateProfile(user1Profile);

        // Fetch and print updated profiles
        System.out.println("\n--- User Profiles ---");
        System.out.println(user1Profile);

        // Save all changes
        UserDatabase.saveUsersToJsonFile();
        ContentDatabase.saveContents();
        profileDatabase.saveProfilesToJsonFile();
        FriendRequest.saveRequestsToJson();

        // Logout
        logUser.logout("user1@example.com");
        System.out.println("\n--- User Status After Logout ---");
        userDatabase.printUsers();
    }
}

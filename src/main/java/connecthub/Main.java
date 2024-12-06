package connecthub;

import connecthub.ContentCreation.Backend.Content;
import connecthub.ContentCreation.Backend.ContentFactory;
import connecthub.ContentCreation.Backend.GetContent;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Backend.UserProfile;
import connecthub.UserAccountManagement.Backend.CreateUser;
import connecthub.UserAccountManagement.Backend.LogUser;

public class Main {
    public static void main(String[] args) {
        // User creation and login
        CreateUser createUser = new CreateUser();
        System.out.println("Testing user signup for User 1:");
        if (createUser.signup("user1@example.com", "user1", "password123", "1990-01-01")) {
            System.out.println("Signup for User 1 successful.");
        } else {
            System.out.println("Signup for User 1 failed.");
        }

        System.out.println("Testing user signup for User 2:");
        if (createUser.signup("user2@example.com", "user2", "password123", "1992-02-02")) {
            System.out.println("Signup for User 2 successful.");
        } else {
            System.out.println("Signup for User 2 failed.");
        }

        LogUser logUser = new LogUser();
        System.out.println("Testing login for User 1:");
        if (logUser.login("user1@example.com", "password123")) {
            System.out.println("Login successful for User 1.");
        } else {
            System.out.println("Login failed for User 1.");
        }

        // Create and retrieve content
        ContentFactory contentFactory = ContentFactory.getInstance();
        System.out.println("Creating a post:");
        Content post = contentFactory.createContent("Post", "1", "This is a test post.", null);
        System.out.println("Post created: " + post);

        System.out.println("Creating a story:");
        Content story = contentFactory.createContent("Story", "1", "This is a test story.", null);
        System.out.println("Story created: " + story);

        GetContent getContent = GetContent.getInstance();
        System.out.println("Retrieving all posts:");
        getContent.getAllPosts().forEach(System.out::println);

        System.out.println("Retrieving all stories:");
        getContent.getAllStories().forEach(System.out::println);

        // Friend management
        FriendManager friendManager = FriendManager.getInstance();
        System.out.println("Adding a friend:");
        if (friendManager.addFriend("1", "2")) {
            System.out.println("Friend added successfully.");
        } else {
            System.out.println("Failed to add friend.");
        }

        // Profile management
        ProfileDatabase profileDB = ProfileDatabase.getInstance();
        UserProfile profile = profileDB.getProfile("1");
        profile.setBio("Updated bio for user 1.");
        profileDB.updateProfile(profile);
        System.out.println("Updated profile: " + profile);

        // Logout
        logUser.logout("user1@example.com");
        System.out.println("User 1 logged out successfully.");
    }
}

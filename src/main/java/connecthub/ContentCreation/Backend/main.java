package connecthub.ContentCreation.Backend;

import connecthub.ContentCreation.Backend.*;
import connecthub.UserAccountManagement.Backend.*;

import java.util.ArrayList;

public class main {

    public static void main(String[] args) {
        // Initialize UserDatabase
        UserDatabase userDB = UserDatabase.getInstance();

        // Create user instances and sign up
        CreateUser createUser = new CreateUser();
        createUser.signup("john.doe@example.com", "johnnyD", "password123", "1990-05-15");
        createUser.signup("jane.smith@example.com", "janeS", "password456", "1992-08-25");

        // Print all users after signup
        System.out.println("All Users After Signup:");
        userDB.printUsers();

        // Log in users
        LogUser logUser = new LogUser();
        logUser.login("john.doe@example.com", "password123");
        logUser.login("jane.smith@example.com", "password456");

        // Print user status after login
        System.out.println("\nUser Status After Login:");
        userDB.printUsers();

        // Create some posts and stories for user1 (John) and user2 (Jane)
        ContentFactory contentFactory = ContentFactory.getInstance();

        // John creates content
        contentFactory.createContent("Post", "1", "John's first post!", "/images/john_post1.jpg");
        contentFactory.createContent("Story", "1", "John's first story!", "/images/john_story1.jpg");

        // Jane creates content
        contentFactory.createContent("Post", "2", "Jane's first post!", "/images/jane_post1.jpg");
        contentFactory.createContent("Story", "2", "Jane's first story!", "/images/jane_story1.jpg");

        // Load all contents and print
        System.out.println("\nAll Contents After Creation:");
        ArrayList<Content> allContents = ContentDatabase.loadContents();
        for (Content content : allContents) {
            System.out.println(content.toJson().toString(4));  // Pretty print JSON
        }

        // Get all posts for user1 (John) and user2 (Jane)
        GetContent getContent = GetContent.getInstance();
        System.out.println("\nJohn's Posts:");
        ArrayList<Post> johnPosts = getContent.getAllPostsForUser(new User("1", "john.doe@example.com", "johnnyD", "hashed_password", "1990-05-15", "online"));
        for (Post post : johnPosts) {
            System.out.println(post.toJson().toString(4));  // Pretty print JSON
        }

        System.out.println("\nJane's Stories:");
        ArrayList<Story> janeStories = getContent.getAllStoriesForUser(new User("2", "jane.smith@example.com", "janeS", "hashed_password", "1992-08-25", "online"));
        for (Story story : janeStories) {
            System.out.println(story.toJson().toString(4));  // Pretty print JSON
        }

        // Remove expired stories and verify
        ContentDatabase.removeExpiredStories();
        System.out.println("\nContents After Removing Expired Stories:");
        ArrayList<Content> remainingContents = ContentDatabase.loadContents();
        for (Content content : remainingContents) {
            System.out.println(content.toJson().toString(4));  // Pretty print JSON
        }

        // Log out users
        logUser.logout("john.doe@example.com");
        logUser.logout("jane.smith@example.com");

        // Print user status after logout
        System.out.println("\nUser Status After Logout:");
        userDB.printUsers();
    }
}
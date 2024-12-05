package connecthub;

import connecthub.ContentCreation.Backend.*;
import connecthub.FriendManagement.Backend.*;
import connecthub.UserAccountManagement.Backend.*;
import connecthub.ProfileManagement.Backend.*;
import connecthub.NewsfeedPage.Backend.*;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        System.out.println("== HARD TEST FOR CONNECT HUB SYSTEM ==");

        // ===== USER MANAGEMENT =====
        System.out.println("\n== User Management Tests ==");
        CreateUser createUser = new CreateUser();
        LogUser logUser = new LogUser();

        // Signup Multiple Users
        createUser.signup("john.doe@example.com", "johnnyD", "password123", "1990-05-15");
        createUser.signup("jane.smith@example.com", "janeS", "securePass", "1992-08-25");
        createUser.signup("alice.wonderland@example.com", "aliceW", "wonder123", "1995-07-30");
        createUser.signup("bob.martin@example.com", "bobM", "bobsecure", "1991-11-20");
        createUser.signup("carol.white@example.com", "carolW", "carolpass", "1988-05-10");
        createUser.signup("daniel.jones@example.com", "danielJ", "daniel123", "1993-04-11");
        createUser.signup("eva.green@example.com", "evaG", "evapass", "1996-02-17");

        // Display All Users
        System.out.println("\nAll Users After Signup:");
        UserDatabase.getInstance().printUsers();

        // Login and Logout Tests
        System.out.println("\nTesting Login...");
        System.out.println("John's Login: " + logUser.login("john.doe@example.com", "password123"));
        System.out.println("Jane's Login: " + logUser.login("jane.smith@example.com", "securePass"));
        System.out.println("Alice's Login: " + logUser.login("alice.wonderland@example.com", "wonder123"));
        System.out.println("Bob's Login: " + logUser.login("bob.martin@example.com", "bobsecure"));
        System.out.println("Carol's Login: " + logUser.login("carol.white@example.com", "carolpass"));

        System.out.println("\nTesting Logout...");
        logUser.logout("john.doe@example.com");
        logUser.logout("jane.smith@example.com");
        logUser.logout("alice.wonderland@example.com");

        // ===== CONTENT CREATION =====
        System.out.println("\n== Content Creation Tests ==");
        ContentFactory contentFactory = ContentFactory.getInstance();

        // Creating Posts and Stories for Multiple Users
        Post johnPost1 = (Post) contentFactory.createContent("Post", "1", "John's first post!", "/images/john_post1.jpg");
        Story johnStory1 = (Story) contentFactory.createContent("Story", "1", "John's first story!", "/images/john_story1.jpg");
        Post janePost1 = (Post) contentFactory.createContent("Post", "2", "Jane's first post!", "/images/jane_post1.jpg");
        Story janeStory1 = (Story) contentFactory.createContent("Story", "2", "Jane's first story!", "/images/jane_story1.jpg");
        Post alicePost1 = (Post) contentFactory.createContent("Post", "3", "Alice's first post!", "/images/alice_post1.jpg");
        Story aliceStory1 = (Story) contentFactory.createContent("Story", "3", "Alice's first story!", "/images/alice_story1.jpg");
        Post bobPost1 = (Post) contentFactory.createContent("Post", "4", "Bob's first post!", "/images/bob_post1.jpg");
        Story bobStory1 = (Story) contentFactory.createContent("Story", "4", "Bob's first story!", "/images/bob_story1.jpg");
        Post carolPost1 = (Post) contentFactory.createContent("Post", "5", "Carol's first post!", "/images/carol_post1.jpg");
        Story carolStory1 = (Story) contentFactory.createContent("Story", "5", "Carol's first story!", "/images/carol_story1.jpg");

        // Saving and Loading Content
        ContentDatabase.saveContents();
        ArrayList<Content> allContents = ContentDatabase.loadContents();
        System.out.println("\nAll Contents After Creation and Loading:");
        allContents.forEach(content -> System.out.println(content.toJson().toString(4)));

        // Test Story Expiration
        System.out.println("\nTesting Expired Story Removal...");
        ContentDatabase.removeExpiredStories();
        ArrayList<Content> contentsAfterExpiry = ContentDatabase.loadContents();
        System.out.println("Contents After Removing Expired Stories:");
        contentsAfterExpiry.forEach(content -> System.out.println(content.toJson().toString(4)));

        // ===== FRIEND MANAGEMENT =====
        System.out.println("\n== Friend Management Tests ==");
        FriendManager friendManager = FriendManager.getInstance();

        // Sending Friend Requests
        friendManager.sendFriendRequest("1", "2");
        friendManager.sendFriendRequest("1", "3");
        friendManager.sendFriendRequest("1", "4");
        friendManager.sendFriendRequest("2", "3");

        // Accepting Friend Requests
        friendManager.respondToRequest("2", "1", true); // John & Jane
        friendManager.respondToRequest("3", "1", true); // John & Alice
        friendManager.respondToRequest("4", "1", true); // John & Bob
        friendManager.respondToRequest("3", "2", true); // Jane & Alice

        // Blocking Users
        friendManager.blockUser("1", "5"); // John blocks Carol
        friendManager.blockUser("2", "4"); // Jane blocks Bob

        // Display Friends and Blocked Users
        System.out.println("\nJohn's Friends: " + friendManager.getFriendsList("1"));
        System.out.println("Jane's Friends: " + friendManager.getFriendsList("2"));

        // ===== PROFILE MANAGEMENT =====
        System.out.println("\n== Profile Management Tests ==");
        ProfileDatabase profileDatabase = ProfileDatabase.getInstance();

        // Updating Profiles
        UserProfile johnProfile = new UserProfile("1", "/profile/john.jpg", "/cover/john.jpg", "Hello, I'm John!", new ArrayList<>());
        profileDatabase.updateProfile(johnProfile);

        UserProfile janeProfile = new UserProfile("2", "/profile/jane.jpg", "/cover/jane.jpg", "Hey, I'm Jane!", new ArrayList<>());
        profileDatabase.updateProfile(janeProfile);

        System.out.println("\nJohn's Profile: " + profileDatabase.getProfile("1"));
        System.out.println("Jane's Profile: " + profileDatabase.getProfile("2"));

        // ===== NEWSFEED TESTS =====
        System.out.println("\n== Newsfeed Tests ==");
        ImplementedNewsfeedBack newsfeed = new ImplementedNewsfeedBack();

        // Get Friend Posts and Stories
        System.out.println("John's Newsfeed (Posts): " + newsfeed.getFriendsPosts("1"));
        System.out.println("John's Newsfeed (Stories): " + newsfeed.getFriendsStories("1"));

        // Friend Suggestions
        System.out.println("Friend Suggestions for John: " + newsfeed.getFriendSuggestions("1"));

        System.out.println("\n== HARD TEST COMPLETED ==");
    }
}
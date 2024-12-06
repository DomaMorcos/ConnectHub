package connecthub;

import connecthub.ContentCreation.Backend.*;
import connecthub.FriendManagement.Backend.*;
import connecthub.ProfileManagement.Backend.*;
import connecthub.UserAccountManagement.Backend.*;

import javax.swing.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Initialize databases
        UserDatabase userDatabase = UserDatabase.getInstance();
        ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
        ContentDatabase contentDatabase = ContentDatabase.getInstance();
        FriendManager friendManager = FriendManager.getInstance();
        ContentFactory contentFactory = ContentFactory.getInstance();
        GetContent getContent = GetContent.getInstance();

        CreateUser userCreator = new CreateUser();
        LogUser logUser = new LogUser();

        // 1. Create multiple users
        System.out.println("Creating Users...");
        for (int i = 1; i <= 10; i++) {
            String email = "user" + i + "@example.com";
            String username = "User" + i;
            String password = "password" + i;
            String dob = "1990-0" + (i % 9 + 1) + "-01"; // Random DOB
            userCreator.signup(email, username, password, dob);
        }
        userDatabase.printUsers();

        // 2. Login users and test status
        System.out.println("\nLogging in Users...");
        for (int i = 1; i <= 10; i++) {
            String email = "user" + i + "@example.com";
            if (logUser.login(email, "password" + i)) {
                System.out.println(email + " logged in successfully.");
            } else {
                System.err.println("Failed to log in: " + email);
            }
        }

        // 3. Create content (posts & stories) for each user
        System.out.println("\nCreating Content...");
        for (int i = 1; i <= 10; i++) {
            String userId = String.valueOf(i);
            contentFactory.createContent("Post", userId, "Post content from User" + i, null);
            contentFactory.createContent("Story", userId, "Story content from User" + i, null);
        }

        // Display all content
        System.out.println("\nDisplaying All Content:");
        List<Content> allContents = getContent.getAllContents();
        allContents.forEach(System.out::println);

        // 4. Send friend requests between users
        System.out.println("\nSending Friend Requests...");
        for (int i = 1; i <= 10; i++) {
            for (int j = i + 1; j <= 10; j++) {
                FriendRequest.sendFriendRequest(String.valueOf(i), String.valueOf(j));
            }
        }

        // Display pending requests for user 10
        System.out.println("\nPending Friend Requests for User10:");
        List<FriendRequest> pendingRequests = FriendRequest.getPendingRequests("10");
        pendingRequests.forEach(System.out::println);

        // 5. Accept requests for some users
        System.out.println("\nAccepting Friend Requests...");
        for (int i = 9; i >= 6; i--) {
            FriendRequest.respondToRequest("10", String.valueOf(i), true);
        }

        // Display friends of User10
        System.out.println("\nFriends of User10:");
        List<User> friendsOfUser10 = friendManager.getFriendsList("10");
        friendsOfUser10.forEach(friend -> System.out.println(friend.getUsername()));

        // 6. Block users and validate
        System.out.println("\nBlocking Users...");
        friendManager.blockUser("10", "1");
        friendManager.blockUser("10", "2");

        // Display updated friend list for User10
        System.out.println("\nFriends of User10 after blocking:");
        friendsOfUser10 = friendManager.getFriendsList("10");
        friendsOfUser10.forEach(friend -> System.out.println(friend.getUsername()));

        // 7. Update and view profiles
        System.out.println("\nUpdating Profiles...");
        for (int i = 1; i <= 10; i++) {
            String userId = String.valueOf(i);
            UserProfile profile = profileDatabase.getProfile(userId);
            profile.setBio("This is User" + i + "'s bio.");
            profile.setProfilePhotoPath("path/to/profile" + i + ".jpg");
            profile.setCoverPhotoPath("path/to/cover" + i + ".jpg");
            profileDatabase.updateProfile(profile);
        }

        // Display profiles
        System.out.println("\nDisplaying Profiles:");
        for (int i = 1; i <= 10; i++) {
            UserProfile profile = profileDatabase.getProfile(String.valueOf(i));
            System.out.println(profile);
        }

        // 8. Test content expiration
        System.out.println("\nRemoving Expired Stories...");
        ContentDatabase.removeExpiredStories();
        List<Story> remainingStories = getContent.getAllStories();
        remainingStories.forEach(System.out::println);

        // 9. Logout all users
        System.out.println("\nLogging out Users...");
        for (int i = 1; i <= 10; i++) {
            String email = "user" + i + "@example.com";
            logUser.logout(email);
            System.out.println(email + " logged out.");
        }

        System.out.println("\nAll tests completed.");
    }
}

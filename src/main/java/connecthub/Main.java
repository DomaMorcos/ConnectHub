package connecthub;

import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.FriendManagement.Backend.FriendRequest;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Backend.UserProfile;
import connecthub.UserAccountManagement.Backend.CreateUser;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        // Initialize the profile and friend manager
        ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
        FriendManager friendManager = FriendManager.getInstance();
        CreateUser userCreator = new CreateUser();

        // 1. Create Users
        System.out.println("Creating users...");
        for (int i = 1; i <= 5; i++) {
            userCreator.signup(
                    "user" + i + "@example.com",
                    "User" + i,
                    "password" + i,
                    "1990-01-0" + i
            );
        }

        // 2. Send Friend Requests
        System.out.println("\nSending friend requests...");
        FriendRequest.sendFriendRequest("1", "2");
        FriendRequest.sendFriendRequest("1", "3");

        // 3. Accept Friend Requests
        System.out.println("\nAccepting friend requests...");
        FriendRequest.respondToRequest("2", "1", true); // User2 accepts User1's request
        FriendRequest.respondToRequest("3", "1", true); // User3 accepts User1's request

        // 4. Loop through profiles and verify friends
        System.out.println("\nVerifying profiles...");
        for (int i = 1; i <= 5; i++) {
            UserProfile profile = profileDatabase.getProfile(String.valueOf(i));
            if (profile != null) {
                System.out.println("Profile of User" + i + ":");
                System.out.println("Bio: " + profile.getBio());
                System.out.println("Friends: " + profile.getFriends());
            } else {
                System.out.println("Profile for User" + i + " not found!");
            }
        }

        // 5. Check JSON File Directly
        System.out.println("\nValidating JSON file content...");
        try {
            String jsonContent = Files.readString(Paths.get("Profiles.JSON"));
            System.out.println("Profiles.JSON content:");
            System.out.println(jsonContent);
        } catch (Exception e) {
            System.err.println("Error reading Profiles.JSON: " + e.getMessage());
        }
    }
}

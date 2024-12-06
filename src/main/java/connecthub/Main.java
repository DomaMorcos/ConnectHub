package connecthub;
import connecthub.ContentCreation.Backend.*;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Backend.UserProfile;
import connecthub.UserAccountManagement.Backend.*;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize Databases
            UserDatabase userDatabase = UserDatabase.getInstance();
            ContentDatabase contentDatabase = ContentDatabase.getInstance();
            FriendManager friendManager = FriendManager.getInstance();
            ProfileDatabase profileDatabase = ProfileDatabase.getInstance();

            // Formatter for dates
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // --- User Account Management ---
            CreateUser userCreator = new CreateUser();
            System.out.println("Creating users...");
            for (int i = 1; i <= 100; i++) {
                String email = "user" + i + "@example.com";
                String username = "user" + i;
                String password = "password" + i;
                String dob = LocalDate.of(1990 + (i % 25), (i % 12) + 1, (i % 28) + 1).format(dateFormatter);

                userCreator.signup(email, username, password, dob);
            }
            System.out.println("Total users created: " + userDatabase.users.size());

            // --- Profile Management ---
            System.out.println("\nSetting up user profiles...");
            for (User user : userDatabase.users) {
                UserProfile profile = profileDatabase.getProfile(user.getUserId());
                profile.setBio("This is the bio for " + user.getUsername());
                //profile.setProfilePhotoPath("imagesDatabase/ProfilePicture/" + user.getUserId() + ".png");
                //profile.setCoverPhotoPath("imagesDatabase/CoverPicture/" + user.getUserId() + ".png");
            }
            System.out.println("Profiles updated for all users.");

            // --- Content Creation (Posts and Stories) ---
            ContentFactory contentFactory = ContentFactory.getInstance();
            System.out.println("\nCreating posts and stories...");
            for (User user : userDatabase.users) {
                for (int j = 0; j < 5; j++) { // 5 posts per user
                    contentFactory.createContent("Post", user.getUserId(), "Post content from " + user.getUsername() + " #" + j, null);
                }
                for (int j = 0; j < 3; j++) { // 3 stories per user
                    contentFactory.createContent("Story", user.getUserId(), "Story content from " + user.getUsername() + " #" + j, null);
                }
            }
            System.out.println("Total contents created: " + contentDatabase.getContents().size());

            // --- Friend Management ---
            System.out.println("\nManaging friendships...");
            for (int i = 1; i <= 50; i++) { // Create friendships for half the users
                friendManager.addFriend(Integer.toString(i), Integer.toString(i + 1));
            }
            System.out.println("Friendships created.");

            // --- User Actions ---
            System.out.println("\nSimulating user actions...");
            LogUser logUser = new LogUser();
            for (int i = 1; i <= 10; i++) {
                String email = "user" + i + "@example.com";
                logUser.login(email, "password" + i);
            }
            System.out.println("10 users logged in.");

            // Save All Changes
            System.out.println("\nSaving changes...");
            UserDatabase.saveUsersToJsonFile();
            ContentDatabase.saveContents();
            profileDatabase.saveProfilesToJsonFile();
            System.out.println("All data saved.");

            // Verify Output
            System.out.println("\n--- Data Summary ---");
            System.out.println("Total users: " + userDatabase.users.size());
            System.out.println("Total contents: " + contentDatabase.getContents().size());
            System.out.println("Total friends for user 1: " + friendManager.getFriendsList("1").size());
            System.out.println("Posts for user 1: " + GetContent.getInstance().getAllPostsForUser(userDatabase.getUserById("1")).size());
            System.out.println("Stories for user 1: " + GetContent.getInstance().getAllStoriesForUser(userDatabase.getUserById("1")).size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

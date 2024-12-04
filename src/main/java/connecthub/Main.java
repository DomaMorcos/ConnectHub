package connecthub;

import connecthub.UserAccountManagement.Backend.*;
import connecthub.ProfileManagement.Backend.*;
import connecthub.ContentCreation.Backend.*;

import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static UserDatabase userDatabase = new UserDatabase();
    private static ContentDatabase contentDatabase = new ContentDatabase();
    private static ProfileManager profileManager = new ProfileManager(contentDatabase, userDatabase);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Update Profile");
            System.out.println("2. View Own Posts");
            System.out.println("3. View Friends");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    updateProfile(scanner);
                    break;
                case 2:
                    viewOwnPosts(scanner);
                    break;
                case 3:
                    viewFriends(scanner);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void updateProfile(Scanner scanner) {
        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine();
        UserProfile profile = profileManager.getProfile(userId);

        if (profile == null) {
            System.out.println("Profile not found.");
            return;
        }

        System.out.print("Enter new profile photo path: ");
        String profilePhotoPath = scanner.nextLine();
        System.out.print("Enter new cover photo path: ");
        String coverPhotoPath = scanner.nextLine();
        System.out.print("Enter new bio: ");
        String bio = scanner.nextLine();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();

        profile.setProfilePhotoPath(profilePhotoPath);
        profile.setCoverPhotoPath(coverPhotoPath);
        profile.setBio(bio);
        profileManager.updateProfile(profile);
        profileManager.updatePassword(userId, password);

        System.out.println("Profile updated successfully.");
    }

    private static void viewOwnPosts(Scanner scanner) {
        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine();
        List<Post> posts = profileManager.getOwnPosts(userId);

        if (posts.isEmpty()) {
            System.out.println("No posts found.");
            return;
        }

        for (Post post : posts) {
            System.out.println("Post ID: " + post.getContentId());
            //System.out.println("Content: " + post.getContent());
            System.out.println("Image Path: " + post.getImagePath());
            System.out.println("Timestamp: " + post.getTimestamp());
            System.out.println("---------------------------");
        }
    }

    private static void viewFriends(Scanner scanner) {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        List<JsonObject> friends = profileManager.getFriendsWithStatus(email);

        if (friends.isEmpty()) {
            System.out.println("No friends found.");
            return;
        }

        for (JsonObject friend : friends) {
            System.out.println("Email: " + friend.getString("email"));
            System.out.println("Username: " + friend.getString("username"));
            System.out.println("Status: " + friend.getString("status"));
            System.out.println("---------------------------");
        }
    }
}
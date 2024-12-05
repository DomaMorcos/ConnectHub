package connecthub;

import connecthub.ContentCreation.Backend.*;
import connecthub.FriendManagement.Backend.*;
import connecthub.UserAccountManagement.Backend.*;
import connecthub.ProfileManagement.Backend.*;

import java.util.ArrayList;


public class Test {
    public static void main(String[] args) {
        ContentFactory contentFactory = ContentFactory.getInstance();
        System.out.println("\nCreating Content...");
        Post post1 = (Post) contentFactory.createContent("Post", "1", "John's first post!", "/images/john_post1.jpg");
        Story story1 = (Story) contentFactory.createContent("Story", "1", "John's first story!", "/images/john_story1.jpg");

        if (post1 != null) {
            System.out.println("Post Created: " + post1.toJson());
        } else {
            System.out.println("Post Creation Failed!");
        }

        if (story1 != null) {
            System.out.println("Story Created: " + story1.toJson());
        } else {
            System.out.println("Story Creation Failed!");
        }

// Save and Load to Verify Persistence
        System.out.println("\nSaving Content...");
        ContentDatabase.saveContents();

        System.out.println("\nLoading Content...");
        ArrayList<Content> loadedContents = ContentDatabase.loadContents();
        if (loadedContents.isEmpty()) {
            System.out.println("No content loaded. Check file operations.");
        } else {
            loadedContents.forEach(content -> System.out.println(content.toJson().toString(4)));
        }}
}
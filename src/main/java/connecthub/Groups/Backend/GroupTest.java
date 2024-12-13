package connecthub.Groups.Backend;

import java.util.ArrayList;

public class GroupTest {

    public static void main(String[] args) {
        // Initialize GroupDatabase
        GroupDatabase groupDatabase = GroupDatabase.getInstance();

        // Create Groups
        Group group1 = new Group("Tech Enthusiasts", "A group for tech lovers.", "tech.jpg", "admin1");
        Group group2 = new Group("Book Club", "A place to discuss books.", "books.jpg", "admin2");

        // Add Groups to Database
        groupDatabase.addGroup(group1);
        groupDatabase.addGroup(group2);

        // Add Members and Admins
        group1.addMemberToGroup(group1.getGroupId(), "user1");
        group1.requestToJoinGroup(group1.getGroupId(), "user2");
        group1.approveJoinRequest(group1.getGroupId(), "user2", "admin1");

        // Create Posts
        GroupPost post1 = GroupPost.createPost("user1", "Hello, Tech World!", null);
        GroupPost post2 = GroupPost.createPost("user2", "What are your thoughts on AI?", null);

        group1.addPost(group1.getGroupId(), post1);
        group1.addPost(group1.getGroupId(), post2);

        // Test Promote to Admin
        try {
            group1.promoteToAdmin(group1.getGroupId(), "user1", "admin1");
            System.out.println("User1 promoted to admin successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error during promotion: " + e.getMessage());
        }

        // Test Leave Group
        try {
            group1.leaveGroup(group1.getGroupId(), "user2");
            System.out.println("User2 left the group successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error during leave: " + e.getMessage());
        }

        // Test Demote Admin
        try {
            group1.demoteToMember(group1.getGroupId(), "admin1", "user1");
            System.out.println("User1 demoted to member successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error during demotion: " + e.getMessage());
        }

        // Test Remove Post
        try {
            group1.removePost(group1.getGroupId(), post2.getPostId(), "admin1");
            System.out.println("Post2 removed successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error during post removal: " + e.getMessage());
        }

        // Test Edit Post
        try {
            group1.editPost(group1.getGroupId(), post1.getPostId(), "user1", "Updated Hello, Tech World!");
            System.out.println("Post1 edited successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error during post edit: " + e.getMessage());
        }

        // Display Groups and Posts
        System.out.println("\nGroups:");
        for (Group group : groupDatabase.getGroups()) {
            System.out.println(group.getName() + " - Members: " + group.getMembersId() + ", Admins: " + group.getAdminsId());
            for (GroupPost post : group.getGroupPosts()) {
                System.out.println(post);
            }
        }

        // Test Group Suggestions for User
        ArrayList<Group> suggestions = groupDatabase.getGroupSuggestionsForUser("user3");
        System.out.println("\nGroup Suggestions for user3:");
        for (Group group : suggestions) {
            System.out.println(group.getName());
        }
    }
}
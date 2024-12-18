package connecthub.ContentCreation.Backend;

import org.json.JSONObject;

import java.util.ArrayList;

public class ContentTest {

    public static void main(String[] args) {
        // Initialize Content Database
        ContentDatabase contentDB = ContentDatabase.getInstance();

        // Create a new post
        ContentFactory contentFactory = ContentFactory.getInstance();
        Post post = (Post) contentFactory.createContent("Post", "user1", "This is a test post", "/path/to/image");

        // Add likes to the post
        post.addLike("user2");
        post.addLike("user3");

        // Add comments to the post
        Post comment1 = (Post) contentFactory.createContent("Comment", "10", "Test Comment", null);
        post.addPostComment(comment1);

        Post comment2 = (Post) contentFactory.createContent("Comment", "user5", "Another comment", null);
        post.addPostComment(comment2);

        // Save the database
        contentDB.saveContents();

        // Clear the database and reload
        contentDB.getContents().clear();
        contentDB.loadContents();

        // Verify the data
        for (Content content : contentDB.getContents()) {
            if (content instanceof Post) {
                Post loadedPost = (Post) content;

//                System.out.println("Loaded Post: " + loadedPost.toJson().toString(4));
//                System.out.println("Number of likes: " + loadedPost.getLikedUsers().size());
//                System.out.println("Number of comments: " + loadedPost.getPostComments().size());

                ArrayList<String> likedUsers = loadedPost.getLikedUsers();
//                System.out.println("Liked Users: " + likedUsers);

                for (Post comment : loadedPost.getPostComments()) {
//                    System.out.println("Comment: " + comment.getContent());
                }
            }
        }
    }
}
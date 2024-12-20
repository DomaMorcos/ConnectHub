package connecthub.ContentCreation.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Post extends AbstractContent {
    private ArrayList<Post> postComments;
    private ArrayList<String> likedUsers;
    ContentDatabase cdb = ContentDatabase.getInstance();
    public Post(String contentId, String authorId, String content, String imagePath, String timestamp) {
        super(contentId, authorId, content, imagePath, timestamp);
        this.postComments = new ArrayList<>();
        this.likedUsers = new ArrayList<>();
    }

    public int getNumberLikes() {
        return likedUsers.size();
    }

    public boolean hasLiked(String userId) {
        return likedUsers.contains(userId);
    }

    public void addLike(String userId) {
        if (!hasLiked(userId)) {

            likedUsers.add(userId);
            cdb.saveContents();
            System.out.println(userId + "Liked this post");
        }
    }

    public void removeLike(String userId) {
        if (hasLiked(userId)) {
            likedUsers.remove(userId);
            System.out.println(userId + "unLiked this post");
            cdb.saveContents();
        }
    }

    public ArrayList<String> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(ArrayList<String> likedUsers) {
        this.likedUsers = likedUsers;
    }

    public ArrayList<Post> getPostComments() {
        return postComments;
    }

    public void setPostComments(ArrayList<Post> postComments) {
        this.postComments = postComments;
    }

    public void addPostComment(Post postComment) {
        postComments.add(postComment);
        cdb.saveContents();
    }

    public void removePostComment(Post postComment) {
        postComments.removeIf(comment -> comment.getContentId().equals(postComment.getContentId()));
        cdb.saveContents();
    }

    public void editPostComment(Post postComment, String newContent) {
        for (Post comment : postComments) {
            if (comment.getContentId().equals(postComment.getContentId())) {
                comment.setContent(newContent);
                cdb.saveContents();
                return;
            }
        }
    }


    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson(); // Assuming this initializes common fields
        obj.put("type", "post");


        JSONArray commentsArray = new JSONArray();
        for (Post comment : postComments) {
            // Debug: Print each comment being processed

            JSONObject commentJson = new JSONObject();
            commentJson.put("contentId", comment.getContentId());
            commentJson.put("authorId", comment.getAuthorId());
            commentJson.put("content", comment.getContent());
            commentJson.put("timestamp", comment.getTimestamp());
            commentJson.put("imagePath", comment.getImagePath());
            commentsArray.put(commentJson);
        }
        obj.put("postComments", commentsArray);

        // Debug: Print final post JSON

        JSONArray likedUsersArray = new JSONArray(likedUsers);
        obj.put("likedUsers", likedUsersArray);

        return obj;
    }



    public static Post readFromJson(JSONObject jsonObject) {
        // Basic fields
        String contentId = jsonObject.optString("contentId", "");
        String authorId = jsonObject.optString("authorId", "unknown");
        String content = jsonObject.optString("content", "");
        String imagePath = jsonObject.optString("imagePath", "");
        String timestamp = jsonObject.optString("timestamp", "");
        Post post = new Post(contentId, authorId, content, imagePath, timestamp);
        JSONArray commentsArray = jsonObject.optJSONArray("postComments");
        if (commentsArray != null) {
            for (int i = 0; i < commentsArray.length(); i++) {
                JSONObject commentJson = commentsArray.getJSONObject(i);
                String commentId = commentJson.optString("contentId", "");
                String commentAuthorId = commentJson.optString("authorId", "unknown");
                String commentContent = commentJson.optString("content", "");
                String commentImagePath = commentJson.optString("imagePath", "");
                String commentTimestamp = commentJson.optString("timestamp", "");
                Post comment = new Post(commentId, commentAuthorId, commentContent, commentImagePath, commentTimestamp);
                post.postComments.add(comment);
            }
        }
        JSONArray likedUsersArray = jsonObject.optJSONArray("likedUsers");
        if (likedUsersArray != null) {
            for (int i = 0; i < likedUsersArray.length(); i++) {
                post.likedUsers.add(likedUsersArray.getString(i));
            }
        }

        return post;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Post{")
                .append("contentId='").append(getContentId() != null ? getContentId() : "null").append('\'')
                .append(", authorId='").append(getAuthorId() != null ? getAuthorId() : "null").append('\'')
                .append(", content='").append(getContent() != null ? getContent() : "null").append('\'')
                .append(", imagePath='").append(getImagePath() != null ? getImagePath() : "null").append('\'')
                .append(", timestamp='").append(getTimestamp() != null ? getTimestamp() : "null").append('\'')
                .append(", likedUsers=").append(likedUsers)
                .append(", comments=[");

        for (Post comment : postComments) {
            sb.append(comment.getContentId()).append(", ");
        }

        if (!postComments.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }

        sb.append("]}");
        return sb.toString();
    }
}
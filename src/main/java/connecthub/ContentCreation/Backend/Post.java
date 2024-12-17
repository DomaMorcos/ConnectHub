package connecthub.ContentCreation.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Post extends AbstractContent {
    private ArrayList<Post> postComments;

    public Post(String contentId, String authorId, String content, String imagePath, String timestamp) {
        super(contentId, authorId, content, imagePath, timestamp);
        this.postComments = new ArrayList<Post>();
    }

    public ArrayList<Post> getPostComments() {
        return postComments;
    }

    public void setPostComments(ArrayList<Post> postComments) {
        this.postComments = postComments;
    }

    public void addPostComment(Post postComment) {
        postComments.add(postComment);
        ContentDatabase cdb = ContentDatabase.getInstance();
        cdb.saveContents();
    }

    public void removePostComment(Post postComment) {
        postComments.removeIf(comment -> comment.getContentId().equals(postComment.getContentId()));
        ContentDatabase cdb = ContentDatabase.getInstance();
        cdb.saveContents();
    }

    public void editPostComment(Post postComment, String newContent) {
        for (Post comment : postComments) {
            if (comment.getContentId().equals(postComment.getContentId())) {
                comment.setContent(newContent);
                ContentDatabase cdb = ContentDatabase.getInstance();
                cdb.saveContents();
                return;
            }
        }
    }


    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson(); // Get the basic fields from AbstractContent
        obj.put("type", "post"); // Add a type field to distinguish Post objects

        // Serialize comments into a JSON array
        JSONArray commentsArray = new JSONArray();
        for (Post comment : postComments) {
            JSONObject commentJson = new JSONObject();
            commentJson.put("contentId", comment.getContentId());
            commentJson.put("authorId", comment.getAuthorId());
            commentJson.put("content", comment.getContent());
            commentJson.put("timestamp", comment.getTimestamp());
            commentJson.put("imagePath", comment.getImagePath());
            commentsArray.put(commentJson);
        }
        obj.put("postComments", commentsArray);

        return obj;
    }

    
    public static Post readFromJson(JSONObject jsonObject) {
        // Basic fields
        String contentId = jsonObject.optString("contentId", "");
        String authorId = jsonObject.optString("authorId", "unknown");
        String content = jsonObject.optString("content", "");
        String imagePath = jsonObject.optString("imagePath", "");
        String timestamp = jsonObject.optString("timestamp", "");

        // Create the main Post object
        Post post = new Post(contentId, authorId, content, imagePath, timestamp);

        // Deserialize comments
        JSONArray commentsArray = jsonObject.optJSONArray("postComments");
        if (commentsArray != null) {
            for (int i = 0; i < commentsArray.length(); i++) {
                JSONObject commentJson = commentsArray.getJSONObject(i);
                String commentId = commentJson.optString("contentId", "");
                String commentAuthorId = commentJson.optString("authorId", "unknown");
                String commentContent = commentJson.optString("content", "");
                String commentImagePath = commentJson.optString("imagePath", "");
                String commentTimestamp = commentJson.optString("timestamp", "");

                // Create a Post object for the comment
                Post comment = new Post(commentId, commentAuthorId, commentContent, commentImagePath, commentTimestamp);
                post.postComments.add(comment);
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
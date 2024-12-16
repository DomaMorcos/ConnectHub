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
        //add to the json object the type
        JSONObject obj = super.toJson();
        obj.put("type", "post");
        JSONArray commentsArray = new JSONArray();
        for (Post comment : postComments) {
            commentsArray.put(comment.toJson());
        }
        obj.put("postComments", commentsArray);
        return obj;
    }

    public static Post readFromJson(JSONObject jsonObject) {
        // make post from json object
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
                Post comment = Post.readFromJson(commentJson);
                post.postComments.add(comment);
            }
        }
        return post;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Post{contentId='").append(getContentId())
                .append("', content='").append(getContent())
                .append("', authorId='").append(getAuthorId())
                .append("', imagePath='").append(getImagePath())
                .append("', timestamp='").append(getTimestamp())
                .append("', comments=[");
        for (Post comment : postComments) {
            sb.append(comment.toString()).append(", ");
        }
        if (!postComments.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]}");
        return sb.toString();
    }
}
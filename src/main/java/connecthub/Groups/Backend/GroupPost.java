package connecthub.Groups.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupPost {
    private String postId;
    private String authorId;
    private String content;
    private String imagePath;
    private String timestamp;
    private String groupID;
    private ArrayList<GroupPost> groupPostComments;
    private ArrayList<String> likedGroupUsers;
    GroupDatabase gdb = GroupDatabase.getInstance();

    public GroupPost(String postId, String authorId, String content, String imagePath, String timestamp) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
        this.groupPostComments = new ArrayList<GroupPost>();
        this.likedGroupUsers = new ArrayList<>();
    }

    public GroupPost(String authorId, String content, String imagePath, String timestamp,String groupID,boolean trueF) {
        this.postId = authorId + "_" + timestamp;
        this.authorId = authorId;
        this.content = content;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
        this.groupPostComments = new ArrayList<GroupPost>();
        this.likedGroupUsers = new ArrayList<>();
        this.groupID = groupID;
    }

    public int getGroupNumberLikes() {
        return likedGroupUsers.size();
    }

    public boolean hasLiked(String userId) {
        return likedGroupUsers.contains(userId);
    }

    public void addGroupLike(String userId) {
        if (!hasLiked(userId)) {
            likedGroupUsers.add(userId);
            gdb.saveGroupsToJsonFile();
            System.out.println(userId + " liked this post");
        }
    }

    public void removeGroupLike(String userId) {
        if (hasLiked(userId)) {
            likedGroupUsers.remove(userId);
            gdb.saveGroupsToJsonFile();
            System.out.println(userId + " unliked this post");
        }
    }

    public ArrayList<String> getGroupLikedUsers() {
        return likedGroupUsers;
    }

    public void setGroupLikedUsers(ArrayList<String> likedUsers) {
        this.likedGroupUsers = likedUsers;
    }

    public ArrayList<GroupPost> getGroupPostComments() {
        return groupPostComments;
    }

    public void setGroupPostComments(ArrayList<GroupPost> groupPostComments) {
        this.groupPostComments = groupPostComments;
        gdb.saveGroupsToJsonFile();
    }

    public void addGroupPostComment(GroupPost groupPostComment) {
        groupPostComments.add(groupPostComment);

        gdb.saveGroupsToJsonFile();
    }

    public void removeGroupPostComment(GroupPost groupPostComment) {
        groupPostComments.removeIf(comment -> comment.getPostId().equals(groupPostComment.getPostId()));
        GroupDatabase gdb = GroupDatabase.getInstance();
        gdb.saveGroupsToJsonFile();
    }

    public void editGroupPostComment(GroupPost postComment, String newContent) {
        for (GroupPost comment : groupPostComments) {
            if (comment.getPostId().equals(postComment.getPostId())) {
                comment.setContent(newContent);
                GroupDatabase gdb = GroupDatabase.getInstance();
                gdb.saveGroupsToJsonFile();
                return;
            }
        }
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("postId", postId != null ? postId : "");
        jsonObject.put("authorId", authorId != null ? authorId : "");
        jsonObject.put("content", content != null ? content : "");
        jsonObject.put("imagePath", imagePath != null ? imagePath : "");
        jsonObject.put("timestamp", timestamp != null ? timestamp : "");

        // Convert comments to JSON
        JSONArray commentsArray = new JSONArray();
        for (GroupPost comment : groupPostComments) {
            JSONObject commentJson = new JSONObject();
            commentJson.put("postId", comment.getPostId());
            commentJson.put("authorId", comment.getAuthorId());
            commentJson.put("content", comment.getContent());
            commentJson.put("timestamp", comment.getTimestamp());
            commentsArray.put(commentJson);
        }
        jsonObject.put("groupPostComments", commentsArray);

        JSONArray likedUsersArray = new JSONArray(likedGroupUsers);
        jsonObject.put("likedGroupUsers", likedUsersArray);
        return jsonObject;
    }

    public static GroupPost fromJson(JSONObject jsonObject) {
        String postId = jsonObject.optString("postId", "");
        String authorId = jsonObject.optString("authorId", "");
        String content = jsonObject.optString("content", "");
        String imagePath = jsonObject.optString("imagePath", "");
        String timestamp = jsonObject.optString("timestamp", "");

        GroupPost groupPost = new GroupPost(postId, authorId, content, imagePath, timestamp);

        // Parse comments from JSON
        JSONArray commentsArray = jsonObject.optJSONArray("groupPostComments");
        if (commentsArray != null) {
            for (int i = 0; i < commentsArray.length(); i++) {
                JSONObject commentJson = commentsArray.getJSONObject(i);
                String commentId = commentJson.optString("postId", "");
                String commentAuthorId = commentJson.optString("authorId", "");
                String commentContent = commentJson.optString("content", "");
                String commentTimestamp = commentJson.optString("timestamp", "");
                GroupPost comment = new GroupPost(commentId, commentAuthorId, commentContent, null, commentTimestamp);
                groupPost.addGroupPostComment(comment);
            }
        }

        JSONArray likedUsersArray = jsonObject.optJSONArray("likedGroupUsers");
        if (likedUsersArray != null) {
            for (int i = 0; i < likedUsersArray.length(); i++) {
                groupPost.likedGroupUsers.add(likedUsersArray.getString(i));
            }
        }
        return groupPost;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GroupPost{")
                .append("postId='").append(postId != null ? postId : "null").append('\'')
                .append(", authorId='").append(authorId != null ? authorId : "null").append('\'')
                .append(", content='").append(content != null ? content : "null").append('\'')
                .append(", imagePath='").append(imagePath != null ? imagePath : "null").append('\'')
                .append(", timestamp='").append(timestamp != null ? timestamp : "null").append('\'')
                .append(", likedUsers=").append(likedGroupUsers)
                .append(", groupPostComments=[");

        for (GroupPost comment : groupPostComments) {
            sb.append(comment.getPostId()).append(", ");
        }
        if (!groupPostComments.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]}");

        return sb.toString();
    }
    public String getGroupID(){
        return  groupID;
    }
}
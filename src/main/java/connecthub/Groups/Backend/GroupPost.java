package connecthub.Groups.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupPost {
    private String postId;
    private String authorId;
    private String content;
    private String imagePath; // Optional field for image path
    private String timestamp;
    private ArrayList<GroupPost> groupPostComments;

    public GroupPost(String postId, String authorId, String content, String imagePath, String timestamp) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
        this.groupPostComments = new ArrayList<GroupPost>();
    }

    public GroupPost(String authorId, String content, String imagePath, String timestamp) {
        this.postId = authorId + "_" + timestamp;
        this.authorId = authorId;
        this.content = content;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
        this.groupPostComments = new ArrayList<GroupPost>();
    }

    public ArrayList<GroupPost> getGroupPostComments() {
        return groupPostComments;
    }

    public void setGroupPostComments(ArrayList<GroupPost> groupPostComments) {
        this.groupPostComments = groupPostComments;
    }

    public void addGroupPostComment(GroupPost groupPostComment) {
        groupPostComments.add(groupPostComment);
        GroupDatabase gdb = GroupDatabase.getInstance();
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
        JSONArray commentsArray = new JSONArray();
        for (GroupPost comment : groupPostComments) {
            commentsArray.put(comment.toJson());
        }
        jsonObject.put("groupPostComments", commentsArray);
        return jsonObject;
    }

    public static GroupPost fromJson(JSONObject jsonObject) {
        String postId = jsonObject.getString("postId");
        String authorId = jsonObject.getString("authorId");
        String content = jsonObject.getString("content");
        String imagePath = jsonObject.optString("imagePath", "");
        String timestamp = jsonObject.getString("timestamp");

        GroupPost groupPost = new GroupPost(postId, authorId, content, imagePath, timestamp);
        JSONArray commentsArray = jsonObject.optJSONArray("postComments");
        if (commentsArray != null) {
            for (int i = 0; i < commentsArray.length(); i++) {
                JSONObject commentJson = commentsArray.getJSONObject(i);
                GroupPost comment = GroupPost.fromJson(commentJson);
                groupPost.groupPostComments.add(comment);
            }
        }
        return groupPost;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Post{contentId='").append(getPostId())
                .append("', author='").append(getAuthorId())
                .append("', content='").append(getContent())
                .append("', imagePath='").append(getImagePath())
                .append("', timestamp='").append(getTimestamp())
                .append("', comments=[");
        for (GroupPost comment : groupPostComments) {
            sb.append(comment.toString()).append(", ");
        }
        if (!groupPostComments.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]}");
        return sb.toString();
    }
}
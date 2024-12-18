
package connecthub.NotificationSystem.backend;

import org.json.JSONObject;

public class CommentNotification extends Notification {
    private String commenterId;
    private String comment;

    public CommentNotification(String userId, String commenterId, String comment) {
        super(userId, "New comment from " + commenterId + ": " + comment);
        this.commenterId = commenterId;
        this.comment = comment;
    }

    @Override
    public String getType() {
        return "Comment";
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson();
        obj.put("commenterId", commenterId);
        obj.put("comment", comment);
        return obj;
    }

    @Override
    public String getGroupId() {
        return "";
    }

    @Override
    public String getSenderId() {
        return "";
    }

    public String getCommenterId() {
        return commenterId;
    }

    public String getComment() {
        return comment;
    }
}
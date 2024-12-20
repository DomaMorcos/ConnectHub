package connecthub.NotificationSystem.backend;

import java.util.HashMap;
import java.util.Map;

public class NotificationManager {
    private static final NotificationDatabase notificationDatabase = NotificationDatabase.getInstance();
    private static NotificationManager notificationManager=null;


    private NotificationManager() {}

    public static  NotificationManager getInstance() {
        if (notificationManager == null) {
            notificationManager = new NotificationManager();
            notificationDatabase.loadNotificationsFromFile();
            return notificationManager;
        }
        return notificationManager;
    }

    // Methods to send notifications to the database
    public void sendFriendRequestNotification(String receiverUserId, String senderId) {
        FriendRequestNotification notification = new FriendRequestNotification(receiverUserId, senderId);
        notificationDatabase.addNotification(notification);
    }

    public void sendGroupActivityNotification(String receiverUserId, String groupId, String activity) {
        GroupActivityNotification notification = new GroupActivityNotification(receiverUserId, groupId, activity);
        notificationDatabase.addNotification(notification);
    }

    public void sendNewPostNotification(String receiverUserId, String groupId) {
        NewPostNotification notification = new NewPostNotification(receiverUserId, groupId);
        notificationDatabase.addNotification(notification);
    }

    public void sendChatNotification(String receiverUserId, String senderUserId, String message) {
        ChatNotification notification = new ChatNotification(receiverUserId, senderUserId, message);
        notificationDatabase.addNotification(notification);
    }

    public void sendCommentNotification(String postOwnerId, String commenterId, String comment) {
        CommentNotification notification = new CommentNotification(postOwnerId, commenterId, comment);
        notificationDatabase.addNotification(notification);
    }
}

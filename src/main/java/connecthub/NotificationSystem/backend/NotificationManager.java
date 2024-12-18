package connecthub.NotificationSystem.backend;

import connecthub.NotificationSystem.frontend.NotificationPage;
import connecthub.NotificationSystem.frontend.NotificationPage2;

import java.util.HashMap;
import java.util.Map;

public class NotificationManager {
    private static final NotificationDatabase notificationDatabase = NotificationDatabase.getInstance();
    private static NotificationManager notificationManager=null;

    // A synchronized map to store user-specific threads
    private final Map<String, Thread> userThreads = new HashMap<>();

    private NotificationManager() {}

    public static synchronized NotificationManager getInstance() {
        if (notificationManager == null) {
            notificationManager = new NotificationManager();
            notificationDatabase.loadNotificationsFromFile();
            return notificationManager;
        }
        return notificationManager;
    }

    // Start a notification thread for a user
    public synchronized void startUserNotificationThread(String userId, NotificationPage2 notificationPage) {
        if (!userThreads.containsKey(userId)) {
            NotificationTask task = new NotificationTask(userId, notificationPage);
            Thread thread = new Thread(task);
            thread.setDaemon(true); // Daemon thread ensures it terminates with the app
            thread.start();
            userThreads.put(userId, thread);
        }
    }

    // Stop a notification thread for a user
    public synchronized void stopUserNotificationThread(String userId) {
        Thread thread = userThreads.get(userId);
        if (thread != null && thread.isAlive()) {
            thread.interrupt(); // Interrupt the thread to stop it
            userThreads.remove(userId);
        }
    }

    // Stop all threads
    public synchronized void stopAllThreads() {
        for (String userId : userThreads.keySet()) {
            stopUserNotificationThread(userId);
        }
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

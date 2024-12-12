package connecthub.NotificationSystem.backend;


public class NotificationManager {
    private static  NotificationDatabase notificationDatabase = NotificationDatabase.getInstance();
    private static NotificationManager notificationManager = null;
    
    private NotificationManager(){
        
    }
    public static NotificationManager getInstance(){
        
        if(notificationManager == null){
            notificationManager = new NotificationManager();
            notificationDatabase.loadNotificationsFromFile();
            return notificationManager;
        }
        return notificationManager;
    }

    public void sendFriendRequestNotification(String ReceiverUserId, String senderId) {
        FriendRequestNotification notification = new FriendRequestNotification(ReceiverUserId, senderId);
        notificationDatabase.addNotification(notification);
    }

    public void sendGroupActivityNotification(String ReceiverUserId, String groupId, String activity) {
        GroupActivityNotification notification = new GroupActivityNotification(ReceiverUserId, groupId, activity);
        notificationDatabase.addNotification(notification);
    }

    public void sendNewPostNotification(String ReceiverUserId, String groupId) {
        NewPostNotification notification = new NewPostNotification(ReceiverUserId, groupId);
        notificationDatabase.addNotification(notification);
    }
}
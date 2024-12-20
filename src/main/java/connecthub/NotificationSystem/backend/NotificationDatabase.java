package connecthub.NotificationSystem.backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NotificationDatabase {
    private static final String NOTIFICATION_FILEPATH = "Notifications.JSON";
    List<NotificationSystem> notifications = new ArrayList<>();
    private static NotificationDatabase notificationDatabase = null;

    private NotificationDatabase() {}

    public static synchronized NotificationDatabase getInstance() {
        if (notificationDatabase == null) {
            notificationDatabase = new NotificationDatabase();
            notificationDatabase.loadNotificationsFromFile();
        }
        return notificationDatabase;
    }

    public synchronized void addNotification(NotificationSystem notification) {
        notifications.add(notification);
        saveNotificationsToFile();
    }

    public synchronized List<NotificationSystem> getNotificationsForUser(String userId) {
        List<NotificationSystem> userNotifications = new ArrayList<>();
        loadNotificationsFromFile();
        for (NotificationSystem notification : notifications) {
            if (notification.getUserId().equals(userId)) {
                userNotifications.add(notification);
            }
        }
        return userNotifications;
    }

    private synchronized void saveNotificationsToFile() {
        JSONArray jsonArray = new JSONArray();
        for (NotificationSystem notification : notifications) {
            jsonArray.put(notification.toJson());
        }
        try (FileWriter file = new FileWriter(NOTIFICATION_FILEPATH)) {
            file.write(jsonArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void loadNotificationsFromFile() {
        try {
            if (!Files.exists(Paths.get(NOTIFICATION_FILEPATH))) {
                Files.createFile(Paths.get(NOTIFICATION_FILEPATH));
                try (FileWriter writer = new FileWriter(NOTIFICATION_FILEPATH)) {
                    writer.write("[]");
                }
            }

            notifications.clear();
            String jsonString = new String(Files.readAllBytes(Paths.get(NOTIFICATION_FILEPATH)));
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String type = json.getString("type");

                switch (type) {
                    case "FriendRequest":
                        notifications.add(new FriendRequestNotification(
                                json.getString("userId"),
                                json.getString("senderId")
                        ));
                        break;
                    case "GroupActivity":
                        notifications.add(new GroupActivityNotification(
                                json.getString("userId"),
                                json.getString("groupId"),
                                json.getString("activity")
                        ));
                        break;
                    case "NewPost":
                        notifications.add(new NewPostNotification(
                                json.getString("userId"),
                                json.getString("groupId")
                        ));
                        break;
                    case "Chat":
                        notifications.add(new ChatNotification(
                                json.getString("userId"),
                                json.getString("senderId"),
                                json.getString("message")
                        ));
                        break;
                    case "Comment":
                        notifications.add(new CommentNotification(
                                json.getString("userId"),
                                json.getString("commenterId"),
                                json.getString("comment")
                        ));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
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
    private static NotificationDatabase instance;
    private static final String NOTIFICATION_FILEPATH = "Notifications.JSON";
    List<NotificationSystem> notifications = new ArrayList<>();
    private static NotificationDatabase notificationDatabase = null;

    private NotificationDatabase() {
        loadNotificationsFromFile();
    }

    public static NotificationDatabase getInstance() {
        if (notificationDatabase == null) {
            notificationDatabase = new NotificationDatabase();
            notificationDatabase.saveNotificationsToFile();
        }
        return notificationDatabase;
    }

    // Add a notification to the database
    public void addNotification(NotificationSystem notification) {
        notifications.add(notification);
        saveNotificationsToFile();
    }

    // Get notifications for a specific user
    public List<NotificationSystem> getNotificationsForUser(String userId) {
        List<NotificationSystem> userNotifications = new ArrayList<>();
        for (NotificationSystem notification : notifications) {
            if (notification.getUserId().equals(userId)) {
                userNotifications.add(notification);
            }
        }
        return userNotifications;
    }

    // Save notifications to a JSON file
    private void saveNotificationsToFile() {
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

    // Load notifications from a JSON file
     void loadNotificationsFromFile() {
        try {
            // Check if the file exists
            if (!Files.exists(Paths.get(NOTIFICATION_FILEPATH))) {
                // If the file does not exist, create an empty file
                Files.createFile(Paths.get(NOTIFICATION_FILEPATH));
                // Optionally, initialize with an empty JSON array
                try (FileWriter file = new FileWriter(NOTIFICATION_FILEPATH)) {
                    file.write("[]");
                }
                System.out.println("Notifications.JSON file was not found and has been created.");
            }

            // Read notification data from the file
            String jsonString = new String(Files.readAllBytes(Paths.get(NOTIFICATION_FILEPATH)));
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String type = json.getString("type");

                // Load the appropriate subclass based on the type
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
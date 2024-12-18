package connecthub.NotificationSystem.backend;

import connecthub.NotificationSystem.frontend.NotificationPage;
import connecthub.NotificationSystem.frontend.NotificationPage2;
import javafx.application.Platform;

import java.util.List;

public class NotificationTask implements Runnable {
    private final String userId;
    private final NotificationDatabase notificationDatabase;
    private final NotificationPage2 notificationPage;
    private boolean running = true;

    public NotificationTask(String userId, NotificationPage2 notificationPage) {
        this.userId = userId;
        this.notificationDatabase = NotificationDatabase.getInstance();
        this.notificationPage = notificationPage;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Sleep for a specific time to simulate polling
                Thread.sleep(500);

                // Fetch new notifications for the user
                List<NotificationSystem> newNotifications = notificationDatabase.getNotificationsForUser(userId);

                if (!newNotifications.isEmpty()) {
                    Platform.runLater(() -> {
                        try {
                            // Update the NotificationPage with new notifications
                            notificationPage.fetchAndDisplayNotifications(userId);

                            // Trigger alert for the new notifications
                            for (NotificationSystem notification : newNotifications) {
                                notificationPage.showAlert(notification.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Notification thread interrupted for user: " + userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

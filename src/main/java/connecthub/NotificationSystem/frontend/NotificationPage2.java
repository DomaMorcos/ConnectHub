package connecthub.NotificationSystem.frontend;

import connecthub.AlertUtils;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.FriendManagement.Frontend.FriendsPage;
import connecthub.Groups.Backend.GroupDatabase;
import connecthub.Groups.Frontend.GroupPage;
import connecthub.NotificationSystem.backend.NotificationDatabase;
import connecthub.NotificationSystem.backend.NotificationManager;
import connecthub.NotificationSystem.backend.NotificationSystem;
import connecthub.NewsfeedPage.Frontend.NewsFeedFront;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class NotificationPage2 implements Runnable {

    private FriendManager friendManager;
    private GroupDatabase groupManager;
    private NotificationDatabase notificationDatabase = NotificationDatabase.getInstance();

    private VBox notificationsList;
    private VBox actionSection;

    private boolean running = true;
    private String userId;

    public NotificationPage2() {}

    public void start(String userId) {
        this.userId = userId;

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Notifications");

        VBox mainLayout = createMainLayout(userId);
        Scene scene = new Scene(mainLayout, 600, 400);

        // Fetch notifications immediately when the page loads
        fetchAndDisplayNotifications(userId);

        primaryStage.setScene(scene);

        // Start background thread for real-time updates
        Thread notificationThread = new Thread(this);
        notificationThread.setDaemon(true); // Ensure the thread stops when the application closes
        notificationThread.start();

        primaryStage.setOnCloseRequest(event -> {
            running = false; // Stop the thread
            NewsFeedFront newsFeedFront = new NewsFeedFront();
            try {
                newsFeedFront.start(userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        primaryStage.show();
    }

    private VBox createMainLayout(String userId) {
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setStyle("-fx-background-color: #f4f4f4;");

        Label notificationsHeader = new Label("Notifications");
        notificationsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        notificationsList = createNotificationsList();
        ScrollPane notificationsScrollPane = new ScrollPane(notificationsList);
        notificationsScrollPane.setFitToWidth(true);

        Label actionHeader = new Label("Actions");
        actionHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        actionSection = createActionSection();
        ScrollPane actionScrollPane = new ScrollPane(actionSection);
        actionScrollPane.setFitToWidth(true);

        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        refreshButton.setOnAction(e -> fetchAndDisplayNotifications(userId));

        mainLayout.getChildren().addAll(refreshButton, notificationsHeader, notificationsScrollPane, actionHeader, actionScrollPane);
        return mainLayout;
    }

    private VBox createNotificationsList() {
        VBox notificationsList = new VBox(10);
        notificationsList.setPadding(new Insets(10));
        notificationsList.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        return notificationsList;
    }

    private VBox createActionSection() {
        VBox actionSection = new VBox(10);
        actionSection.setPadding(new Insets(10));
        actionSection.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        return actionSection;
    }

    public void fetchAndDisplayNotifications(String userId) {
        notificationsList.getChildren().clear();
        actionSection.getChildren().clear();

        try {
            List<NotificationSystem> notifications = notificationDatabase.getNotificationsForUser(userId);

            for (NotificationSystem notification : notifications) {
                VBox notificationCard = new VBox(5);
                notificationCard.setPadding(new Insets(10));
                notificationCard.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #4CAF50; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

                Label notificationLabel = new Label(notification.getMessage());
                notificationLabel.setStyle("-fx-font-size: 14px; -fx-cursor: hand;");

                notificationLabel.setOnMouseClicked(e -> displayActions(notification, userId));

                notificationCard.getChildren().add(notificationLabel);
                notificationsList.getChildren().add(notificationCard);
            }

            if (notifications.isEmpty()) {
                Label noNotificationsLabel = new Label("No notifications available.");
                noNotificationsLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
                notificationsList.getChildren().add(noNotificationsLabel);
            }
        } catch (Exception e) {
            AlertUtils.showErrorMessage("Error", "Failed to load notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayActions(NotificationSystem notification, String userId) {
        actionSection.getChildren().clear();

        Label actionLabel = new Label("Action for: " + notification.getMessage());
        actionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        if (notification.getType().equals("FriendRequest")) {
            try {
                FriendsPage friendPage = new FriendsPage();
                friendPage.start(userId);
            } catch (Exception ex) {
                AlertUtils.showErrorMessage("Error", "Failed to load FriendPage: " + ex.getMessage());
            }
        } else if (notification.getType().equals("GroupActivity")) {
            try {
                GroupPage groupPage = new GroupPage();
                groupPage.start(userId, notification.getGroupId());
            } catch (Exception ex) {
                AlertUtils.showErrorMessage("Error", "Failed to load GroupPage: " + ex.getMessage());
            }
        }

        actionSection.getChildren().add(actionLabel);
    }
    public void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("New Notification");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Fetch new notifications every 5 seconds
                Thread.sleep(5000);

                // Check for new notifications
                List<NotificationSystem> newNotifications = notificationDatabase.getNotificationsForUser(userId);

                if (!newNotifications.isEmpty()) {
                    Platform.runLater(() -> fetchAndDisplayNotifications(userId)); // Update UI on JavaFX Application Thread
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Notification thread interrupted.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

package connecthub.NotificationSystem.frontend;

import connecthub.AlertUtils;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.FriendManagement.Frontend.FriendsPage;
import connecthub.Groups.Backend.GroupDatabase;
import connecthub.Groups.Backend.GroupDatabase;
import connecthub.NotificationSystem.backend.NotificationDatabase;
import connecthub.NotificationSystem.backend.NotificationSystem;
import connecthub.NotificationSystem.backend.NotificationManager;
import connecthub.NewsfeedPage.Frontend.NewsFeedFront;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class NotificationPage {

    private  FriendManager friendManager;
    private GroupDatabase groupManager;
    private NotificationDatabase notificationDatabase = NotificationDatabase.getInstance();

    private VBox notificationsList;
    private VBox actionSection;

    public NotificationPage() {
    }

    public void start(String userId) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Notifications");

        VBox mainLayout = createMainLayout(userId);
        Scene scene = new Scene(mainLayout, 600, 400);

        // Fetch notifications immediately when the page loads
        fetchAndDisplayNotifications(userId);

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
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
        refreshButton.setOnAction(e -> {
            fetchAndDisplayNotifications(userId);
        });

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

    private void fetchAndDisplayNotifications(String userId) {
        notificationsList.getChildren().clear();
        actionSection.getChildren().clear();

        try {
            List<NotificationSystem> notifications =  notificationDatabase.getNotificationsForUser(userId);

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
        
        if(notification.getType().equals("FriendRequest")) {
            try {
                FriendsPage friendPage = new FriendsPage();
                friendPage.start(userId); // Open FriendPage
                Stage stage = (Stage) actionSection.getScene().getWindow();
                stage.close(); // Close NotificationPage
            } catch (Exception ex) {
                AlertUtils.showErrorMessage("Error", "Failed to load FriendPage: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else if (notification.getType().equals("GroupActivity")) {
            try {
                FriendsPage friendPage = new FriendsPage();
                friendPage.start(userId); // Open FriendPage
                Stage stage = (Stage) actionSection.getScene().getWindow();
                stage.close(); // Close NotificationPage
            } catch (Exception ex) {
                AlertUtils.showErrorMessage("Error", "Failed to load FriendPage: " + ex.getMessage());
                ex.printStackTrace();
            }
            
        } else if (notification.getType().equals("NewPost") && notification.getUserId().equals(userId) ) {
            try {
                FriendsPage friendPage = new FriendsPage();
                friendPage.start(userId); // Open FriendPage
                Stage stage = (Stage) actionSection.getScene().getWindow();
                stage.close(); // Close NotificationPage
            } catch (Exception ex) {
                AlertUtils.showErrorMessage("Error", "Failed to load FriendPage: " + ex.getMessage());
                ex.printStackTrace();
            }
            
        }

        actionSection.getChildren().add(actionLabel);
    }

    private HBox createFriendRequestButtons(String senderId, String receiverId) {
        HBox buttonBox = new HBox(10);

        Button acceptButton = new Button("Accept");
        Button declineButton = new Button("Decline");

        acceptButton.setOnAction(e -> {
            try {
                if (friendManager.handleFriendRequest(receiverId, senderId, true)) {
                    AlertUtils.showInformationMessage("Request Accepted", "You are now friends with " + senderId + "!");
                } else {
                    AlertUtils.showErrorMessage("Error", "Failed to accept the request.");
                }
            } catch (Exception ex) {
                AlertUtils.showErrorMessage("Error", "An error occurred: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        declineButton.setOnAction(e -> {
            try {
                if (friendManager.handleFriendRequest(receiverId, senderId, false)) {
                    AlertUtils.showInformationMessage("Request Declined", "Friend request declined successfully.");
                } else {
                    AlertUtils.showErrorMessage("Error", "Failed to decline the request.");
                }
            } catch (Exception ex) {
                AlertUtils.showErrorMessage("Error", "An error occurred: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        buttonBox.getChildren().addAll(acceptButton, declineButton);
        return buttonBox;
    }

    private HBox createGroupActivityButton(String userId, String groupId) {
        HBox buttonBox = new HBox(10);

        Button viewGroupButton = new Button("View Group");
        viewGroupButton.setOnAction(e -> {
            try {
                AlertUtils.showInformationMessage("Group Activity", "Navigating to group updates...");
            } catch (Exception ex) {
                AlertUtils.showErrorMessage("Error", "Failed to process group activity.");
                ex.printStackTrace();
            }
        });

        buttonBox.getChildren().add(viewGroupButton);
        return buttonBox;
    }

    private HBox createViewPostButton(String groupId) {
        HBox buttonBox = new HBox(10);

        Button viewPostButton = new Button("View Post");
        viewPostButton.setOnAction(e -> {
            try {
                AlertUtils.showInformationMessage("View Post", "Post content is now accessible!");
            } catch (Exception ex) {
                AlertUtils.showErrorMessage("Error", "Failed to view post.");
                ex.printStackTrace();
            }
        });

        buttonBox.getChildren().add(viewPostButton);
        return buttonBox;
    }
}

package connecthub.FriendManagement.Frontend;

import connecthub.AlertUtils;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.FriendManagement.Backend.FriendRequest;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FriendsPage {

    private FriendManager friendManager = FriendManager.getInstance();
    private UserDatabase userDatabase = UserDatabase.getInstance();
    private VBox friendsVBox;
    private VBox friendRequestsVBox;
    private VBox friendSuggestionsVBox;

    public void start(String userID) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Friend Management");

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Left: Friend List
        VBox friendList = createFriendList(userID);
        mainLayout.setLeft(friendList);

        // Center: Friend Requests
        ScrollPane friendRequests = createFriendRequests(userID);
        mainLayout.setCenter(friendRequests);

        // Right: Friend Suggestions
        VBox friendSuggestions = createFriendSuggestions(userID);
        mainLayout.setRight(friendSuggestions);

        // Scene setup
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createFriendList(String userID) {
        friendsVBox = new VBox(10);
        friendsVBox.setPadding(new Insets(10));

        Label friendsLabel = new Label("My Friends");
        friendsVBox.getChildren().add(friendsLabel);

        refreshFriendList(userID);
        return friendsVBox;
    }

    private void refreshFriendList(String userID) {
        friendsVBox.getChildren().clear();
        Label friendsLabel = new Label("My Friends");
        friendsVBox.getChildren().add(friendsLabel);

        for (User friend : friendManager.getFriendsList(userID)) {
            Label friendName = new Label(friend.getUsername());
            Button removeButton = new Button("Remove");
            Button blockButton = new Button("Block");

            removeButton.setOnAction(e -> {
                if (friendManager.removeFriend(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Removed", friend.getUsername() + " was removed from your friend list.");
                    refreshFriendList(userID);
                    refreshFriendSuggestions(userID); // Refresh suggestions dynamically
                }
            });

            blockButton.setOnAction(e -> {
                if (friendManager.blockFriend(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Blocked", friend.getUsername() + " was blocked.");
                    refreshFriendList(userID);
                    refreshFriendSuggestions(userID); // Refresh suggestions dynamically
                }
            });

            HBox friendBox = new HBox(10);
            friendBox.getChildren().addAll(friendName, removeButton, blockButton);
            friendsVBox.getChildren().add(friendBox);
        }
    }

    private ScrollPane createFriendRequests(String userID) {
        friendRequestsVBox = new VBox(10);
        friendRequestsVBox.setPadding(new Insets(10));

        Label friendRequestsLabel = new Label("Friend Requests");
        friendRequestsVBox.getChildren().add(friendRequestsLabel);

        refreshFriendRequests(userID);

        ScrollPane scrollPane = new ScrollPane(friendRequestsVBox);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private void refreshFriendRequests(String userID) {
        friendRequestsVBox.getChildren().clear();
        Label friendRequestsLabel = new Label("Friend Requests");
        friendRequestsVBox.getChildren().add(friendRequestsLabel);

        for (FriendRequest friendRequest : friendManager.getPendingRequests(userID)) {
            HBox requestBox = new HBox(10);
            User friend = userDatabase.getUserById(friendRequest.getSenderId());
            Label friendRequestSender = new Label(friend.getUsername());
            Button acceptButton = new Button("Accept");
            Button rejectButton = new Button("Reject");

            acceptButton.setOnAction(e -> {
                if (friendManager.handleFriendRequest(userID, friend.getUserId(), true)) {
                    AlertUtils.showInformationMessage("Request Accepted", "Friend request accepted!");
                    refreshFriendRequests(userID);
                    refreshFriendList(userID);
                    refreshFriendSuggestions(userID);
                }
            });

            rejectButton.setOnAction(e -> {
                if (friendManager.handleFriendRequest(userID, friend.getUserId(), false)) {
                    AlertUtils.showInformationMessage("Request Rejected", "Friend request rejected!");
                    refreshFriendRequests(userID);
                }
            });

            requestBox.getChildren().addAll(friendRequestSender, acceptButton, rejectButton);
            friendRequestsVBox.getChildren().add(requestBox);
        }
    }

    private VBox createFriendSuggestions(String userID) {
        friendSuggestionsVBox = new VBox(10);
        friendSuggestionsVBox.setPadding(new Insets(10));

        Label suggestionsLabel = new Label("Friend Suggestions");
        friendSuggestionsVBox.getChildren().add(suggestionsLabel);

        refreshFriendSuggestions(userID);
        return friendSuggestionsVBox;
    }

    private void refreshFriendSuggestions(String userID) {
        friendSuggestionsVBox.getChildren().clear();
        Label suggestionsLabel = new Label("Friend Suggestions");
        friendSuggestionsVBox.getChildren().add(suggestionsLabel);

        for (User friend : friendManager.suggestFriends(userID)) {
            Label username = new Label(friend.getUsername());
            Button sendFriendRequest = new Button("Send Request");

            sendFriendRequest.setOnAction(e -> {
                if (friendManager.sendFriendRequest(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Request", "Friend request sent to " + friend.getUsername() + "!");
                    refreshFriendSuggestions(userID);
                }
            });

            VBox suggestionBox = new VBox(5);
            suggestionBox.getChildren().addAll(username, sendFriendRequest);
            friendSuggestionsVBox.getChildren().add(suggestionBox);
        }
    }
}
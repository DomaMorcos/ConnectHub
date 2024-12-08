package connecthub.FriendManagement.Frontend;

import connecthub.AlertUtils;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.FriendManagement.Backend.FriendRequest;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FriendsPage {

    private FriendManager friendManager = FriendManager.getInstance();
    private UserDatabase userDatabase = UserDatabase.getInstance();
    private TextField searchField;
    private Button searchButton;
    private VBox friendsVBox;
    private VBox friendRequestsVBox;
    private VBox friendSuggestionsVBox;


    public void start(String userID) throws Exception {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Friend Management");

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));



        // Left: Friend List
        VBox friendList = createFriendList(primaryStage,userID);
        mainLayout.setLeft(friendList);

        // Center: Friend Requests
        ScrollPane friendRequests = createFriendRequests(primaryStage,userID);
        mainLayout.setCenter(friendRequests);

        // Right: Friend Suggestions
        VBox friendSuggestions = createFriendSuggestions(primaryStage,userID);
        mainLayout.setRight(friendSuggestions);

        // Scene setup
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.showAndWait();
    }






    private VBox createFriendList(Stage stage , String userID) {
        friendsVBox = new VBox(10);
        friendsVBox.setPadding(new Insets(10));

        Label friendsLabel = new Label("My Friends");
        friendsVBox.getChildren().add(friendsLabel);

        // Simulate loading friends (in a real app, fetch from backend)
        for (User friend : friendManager.getFriendsList(userID)) {
            Label friendName = new Label(friend.getUsername());
            Button removeButton = new Button("Remove");
            Button blockButton = new Button("Block");

            removeButton.setOnAction(e -> {
                // Handle removing friend
                if (friendManager.removeFriend(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Removed", friend.getUsername() + "is succesfully removed from the friends list");

                    FriendsPage friendsPage = new FriendsPage();
                    try {
                        stage.close();
                        friendsPage.start(userID);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            blockButton.setOnAction(e -> {
                // Handle blocking friend
                if (friendManager.blockFriend(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Blocked", friend.getUsername() + "is succesfully block from the friends list");

                    FriendsPage friendsPage = new FriendsPage();
                    try {
                        stage.close();
                        friendsPage.start(userID);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            HBox friendBox = new HBox(10);
            friendBox.getChildren().addAll(friendName, removeButton, blockButton);
            friendsVBox.getChildren().add(friendBox);
        }

        return friendsVBox;
    }

    private ScrollPane createFriendRequests(Stage stage,String userID) {
        friendRequestsVBox = new VBox(10);
        friendRequestsVBox.setPadding(new Insets(10));

        Label friendRequestsLabel = new Label("Friend Requests");
        friendRequestsVBox.getChildren().add(friendRequestsLabel);

        // Simulate loading friend requests (in a real app, fetch from backend)
        for (FriendRequest friendRequest : friendManager.getPendingRequests(userID)) {
            System.out.println(friendRequest.toString());
            HBox requestBox = new HBox(10);
            User friend = userDatabase.getUserById(friendRequest.getSenderId());
            Label friendRequestSender = new Label(friend.getUsername());
            Label status = new Label();
            Button acceptButton = new Button("Accept");
            Button rejectButton = new Button("Reject");

            acceptButton.setOnAction(e -> {
                // Handle accepting the friend request
                if (friendManager.handleFriendRequest(userID, friend.getUserId(), true)) {
                    AlertUtils.showInformationMessage("Request Accepted", "Friend Request Accepted!");

                    FriendsPage friendsPage = new FriendsPage();
                    try {
                        stage.close();
                        friendsPage.start(userID);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }

            });

            rejectButton.setOnAction(e -> {
                // Handle rejecting the friend request
                if (friendManager.handleFriendRequest(userID, friend.getUserId(), false)) {
                    AlertUtils.showInformationMessage("Request Rejected", "Friend Request Rejected!");

                    FriendsPage friendsPage = new FriendsPage();
                    try {
                        stage.close();
                        friendsPage.start(userID);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }

            });

            requestBox.getChildren().addAll(friendRequestSender, acceptButton, rejectButton);
            friendRequestsVBox.getChildren().add(requestBox);
        }

        ScrollPane scrollPane = new ScrollPane(friendRequestsVBox);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private VBox createFriendSuggestions(Stage stage , String userID) {
        friendSuggestionsVBox = new VBox(10);
        friendSuggestionsVBox.setPadding(new Insets(10));

        Label suggestionsLabel = new Label("Friend Suggestions");
        friendSuggestionsVBox.getChildren().add(suggestionsLabel);


        for (User friend : friendManager.suggestFriends(userID)) {
            VBox friendSuggestion = new VBox();
            friendSuggestion.setSpacing(10);
            friendSuggestion.setPadding(new Insets(10));
            Label username = new Label(friend.getUsername());
            Button sendFriendRequest = new Button("Send Request");
            sendFriendRequest.setOnAction(e -> {
                if (friendManager.sendFriendRequest(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Request", "Friend Request is sent to " + friend.getUsername());
                    FriendsPage friendsPage = new FriendsPage();
                    try {
                        stage.close();
                        friendsPage.start(userID);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            friendSuggestion.getChildren().addAll(username, sendFriendRequest);
            friendSuggestionsVBox.getChildren().add(friendSuggestion);
        }

        return friendSuggestionsVBox;
    }


}

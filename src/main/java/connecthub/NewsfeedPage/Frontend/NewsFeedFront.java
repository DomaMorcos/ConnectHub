package connecthub.NewsfeedPage.Frontend;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class NewsFeedFront extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Newsfeed Page");

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Left side: Friend List
        VBox friendList = createFriendList();
        mainLayout.setLeft(friendList);

        // Center: Posts and Stories
        VBox postsAndStories = createPostsAndStories();
        mainLayout.setCenter(postsAndStories);

        // Right side: Friend Suggestions
        VBox friendSuggestions = createFriendSuggestions();
        mainLayout.setRight(friendSuggestions);

        // Bottom: Content Creation Area
        HBox contentCreationArea = createContentCreationArea();
        mainLayout.setBottom(contentCreationArea);

        // Scene setup
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createFriendList() {
        VBox friendList = new VBox();
        friendList.setPadding(new Insets(10));
        friendList.setSpacing(5);
        friendList.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        Label friendListLabel = new Label("Friends");
        friendList.getChildren().add(friendListLabel);

        // Sample friends
        for (int i = 1; i <= 5; i++) {
            String friendStatus = (i % 2 == 0) ? "Online" : "Offline";
            Label friendLabel = new Label("Friend " + i + " - " + friendStatus);
            friendList.getChildren().add(friendLabel);
        }
        return friendList;
    }

    private VBox createPostsAndStories() {
        VBox postsAndStories = new VBox();
        postsAndStories.setPadding(new Insets(10));
        postsAndStories.setSpacing(10);
        postsAndStories.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        Label postsLabel = new Label("Recent Posts and Stories");
        postsAndStories.getChildren().add(postsLabel);

        // Sample posts
        for (int i = 1; i <= 3; i++) {
            TextArea postArea = new TextArea("Post content " + i);
            postArea.setEditable(false);
            postsAndStories.getChildren().add(postArea);
        }

        return postsAndStories;
    }

    private VBox createFriendSuggestions() {
        VBox friendSuggestions = new VBox();
        friendSuggestions.setPadding(new Insets(10));
        friendSuggestions.setSpacing(5);
        friendSuggestions.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        Label suggestionsLabel = new Label("Friend Suggestions");
        friendSuggestions.getChildren().add(suggestionsLabel);

        // Sample suggestions
        for (int i = 1; i <= 3; i++) {
            Button suggestButton = new Button("Add Friend " + i);
            friendSuggestions.getChildren().add(suggestButton);
        }

        return friendSuggestions;
    }

    private HBox createContentCreationArea() {
        HBox contentCreationArea = new HBox();
        contentCreationArea.setPadding(new Insets(10));
        contentCreationArea.setSpacing(10);
        contentCreationArea.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        TextArea newPostArea = new TextArea();
        newPostArea.setPromptText("What's on your mind?");
        newPostArea.setPrefHeight(100);
        contentCreationArea.getChildren().add(newPostArea);

        Button postButton = new Button("Post");
        contentCreationArea.getChildren().add(postButton);

        Button refreshButton = new Button("Refresh");
        contentCreationArea.getChildren().add(refreshButton);

        return contentCreationArea;
    }

    public static void main(String[] args) {
        launch(args);
    }
}


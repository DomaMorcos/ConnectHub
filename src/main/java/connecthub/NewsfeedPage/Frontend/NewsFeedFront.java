package connecthub.NewsfeedPage.Frontend;

import connecthub.AlertUtils;
import connecthub.ContentCreation.Backend.ContentDatabase;
import connecthub.ContentCreation.Backend.GetContent;
import connecthub.ContentCreation.Backend.Post;
import connecthub.ContentCreation.Backend.Story;
import connecthub.ContentCreation.Frontend.AddPost;
import connecthub.ContentCreation.Frontend.AddStory;
import connecthub.ContentCreation.Frontend.DisplayStory;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.FriendManagement.Backend.FriendRequest;
import connecthub.FriendManagement.Frontend.FriendsPage;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Frontend.ProfilePage;
import connecthub.TimestampFormatter;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class NewsFeedFront {
    private FriendManager friendManager = FriendManager.getInstance();
    ContentDatabase contentDatabase = ContentDatabase.getInstance();
    UserDatabase userDatabase = UserDatabase.getInstance();
    GetContent getContent = GetContent.getInstance();
    ProfileDatabase profileDatabase = ProfileDatabase.getInstance();

    public void start(String userID) throws Exception {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Newsfeed Page");

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Top: Stories
        HBox storiesSection = createStoriesSection(userID);
        mainLayout.setTop(storiesSection);

        // Left side: Friend List
        VBox friendList = createFriendList(primaryStage,userID);
        mainLayout.setLeft(friendList);

        // Center: Posts
        ScrollPane posts = createPosts(userID);
        mainLayout.setCenter(posts);

        // Right side: Friend Suggestions
        VBox friendSuggestions = createFriendSuggestions(primaryStage,userID);
        mainLayout.setRight(friendSuggestions);

        // Bottom: Content Creation Area
        HBox contentCreationArea = createContentCreationArea(primaryStage, userID);
        mainLayout.setBottom(contentCreationArea);

        // Add CSS class names
        storiesSection.getStyleClass().add("stories-section");
        friendList.getStyleClass().add("friend-list");
        posts.getStyleClass().add("posts-and-stories");
        friendSuggestions.getStyleClass().add("friend-suggestions");
        contentCreationArea.getStyleClass().add("content-creation-area");
        mainLayout.getStyleClass().add("main-layout");

        // Scene setup
        Scene scene = new Scene(mainLayout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("NewsFeedFront.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createStoriesSection(String userID) {
        // Container for all stories
        HBox storiesContainer = new HBox();
        storiesContainer.setPadding(new Insets(10));
        storiesContainer.setSpacing(10);

        // Add a "Stories" label with fixed width
        Label storiesLabel = new Label("Stories");
        storiesLabel.getStyleClass().add("label-heading");
        storiesLabel.setMinWidth(100); // Fixed width for the "Stories" label

        // Create a horizontal list of stories
        HBox storiesList = new HBox();
        storiesList.setSpacing(15);
        storiesList.setPadding(new Insets(10));

        User user = userDatabase.getUserById(userID);

        // Populate the stories list
        for (Story story : getContent.getAllStoriesForUser(user)) {
            VBox singleStory = new VBox();
            singleStory.setSpacing(5);

            // Create story thumbnail
            ImageView storyImage = new ImageView(
                    new Image(getClass().getResource(profileDatabase.getProfile(userID).getProfilePhotoPath()).toExternalForm())
            );
            storyImage.setFitHeight(80);
            storyImage.setFitWidth(80);
            storyImage.getStyleClass().add("story-image");

            // Story label and timestamp
            Label username = new Label("My Story");
            Label storyDate = new Label(TimestampFormatter.formatTimestamp(story.getTimestamp()));

            // Add click event to open the story
            storyImage.setOnMouseClicked(e -> {
                DisplayStory displayStory = new DisplayStory();
                displayStory.start(story);
            });

            // Combine elements for a single story
            singleStory.getChildren().addAll(storyImage, username, storyDate);
            storiesList.getChildren().add(singleStory);
        }

        // Wrap the stories list in a horizontal ScrollPane
        ScrollPane storiesScrollPane = new ScrollPane(storiesList);
        storiesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Enable horizontal scrolling
        storiesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Disable vertical scrolling
        storiesScrollPane.setFitToHeight(true);
        storiesScrollPane.setPannable(true);
        storiesScrollPane.setPrefWidth(600); // Fixed width for the ScrollPane
        storiesScrollPane.getStyleClass().add("stories-scroll-pane");

        // Add the label and scroll pane to the container
        storiesContainer.getChildren().addAll(storiesLabel, storiesScrollPane);

        return storiesContainer;
    }


    private VBox createFriendList(Stage stage , String userID) {
        VBox friendsVBox = new VBox(10);
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

                }
            });

            blockButton.setOnAction(e -> {
                // Handle blocking friend
                if (friendManager.blockUser(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Blocked", friend.getUsername() + "is succesfully block from the friends list");


                }
            });

            HBox friendBox = new HBox(10);
            friendBox.getChildren().addAll(friendName, removeButton, blockButton);
            friendsVBox.getChildren().add(friendBox);
        }

        return friendsVBox;
    }

    private ScrollPane createPosts(String userID) {
        VBox postsBox = new VBox();
        postsBox.setSpacing(10);
        postsBox.setPadding(new Insets(10));

        Label postsLabel = new Label("Recent Posts");
        postsLabel.getStyleClass().add("label-heading");
        postsBox.getChildren().add(postsLabel);

        User user = userDatabase.getUserById(userID);

        // Populate the posts list
        for (Post post : getContent.getAllPostsForUser(user)) {
            VBox singlePost = new VBox();
            singlePost.setSpacing(5);

            // Author image and username
            ImageView authorImage = new ImageView(new Image(getClass().getResource(profileDatabase.getProfile(userID).getProfilePhotoPath()).toExternalForm()));
            authorImage.setFitWidth(30);
            authorImage.setFitHeight(30);
            Label username = new Label(user.getUsername());
            Label time = new Label(TimestampFormatter.formatTimestamp(post.getTimestamp()));



            // Post content (TextArea) with fixed size and scrollable
            TextArea postText = new TextArea(post.getContent());
            postText.setEditable(false);
            postText.setWrapText(true); // Allow text wrapping
            postText.setPrefHeight(100); // Set fixed height
            postText.setPrefWidth(400); // Set fixed width
            postText.setScrollTop(0); // Ensure the content is scrollable

            // Add components to the single post VBox
            singlePost.getChildren().addAll(authorImage, username, time);
            // Optional post thumbnail image
            if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
                try {
                    ImageView postImage = new ImageView(
                            new Image(getClass().getResource(post.getImagePath()).toExternalForm())
                    );
                    postImage.setFitHeight(80);
                    postImage.setFitWidth(80);
                    postImage.getStyleClass().add("post-image");
                    singlePost.getChildren().add(postImage);
                } catch (Exception e) {
                    // Log or handle the invalid image path
                    System.err.println("Invalid image path for post: " + post.getImagePath());
                }
            }
            singlePost.getChildren().add(postText);

            // Add the single post to the postsBox
            postsBox.getChildren().add(singlePost);
        }

        // Create a ScrollPane to make posts scrollable
        ScrollPane scrollPane = new ScrollPane(postsBox);
        scrollPane.setFitToWidth(true); // Ensure the ScrollPane stretches to fit the width of the postsBox
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show vertical scrollbar
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Disable horizontal scrollbar
        scrollPane.getStyleClass().add("scroll-pane");

        return scrollPane;
    }




    private VBox createFriendSuggestions(Stage stage , String userID) {
        VBox friendSuggestionsVBox = new VBox(10);
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
                if (FriendRequest.sendFriendRequest(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Request", "Friend Request is sent to " + friend.getUsername());


                }
            });
            friendSuggestion.getChildren().addAll(username, sendFriendRequest);
            friendSuggestionsVBox.getChildren().add(friendSuggestion);
        }

        return friendSuggestionsVBox;
    }

    private HBox createContentCreationArea(Stage stage, String userID) {
        HBox contentCreationArea = new HBox();
        contentCreationArea.setPadding(new Insets(10));
        contentCreationArea.setSpacing(10);
        Button storyButton = new Button("Story");
        storyButton.getStyleClass().add("button");
        storyButton.setOnAction(e -> {
            AddStory addStory = new AddStory();
            addStory.start(userID);
        });

        Button postButton = new Button("Post");
        postButton.getStyleClass().add("button");
        postButton.setOnAction(e -> {
            AddPost addPost = new AddPost();
            addPost.start(userID);
        });
        contentCreationArea.getChildren().addAll(storyButton, postButton);

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("button");
        refreshButton.setOnAction(e -> {
            NewsFeedFront newsFeedFront = new NewsFeedFront();
            try {
                newsFeedFront.start(userID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            stage.close();
        });
        contentCreationArea.getChildren().add(refreshButton);
        Button profileButton = new Button("Profile");
        profileButton.getStyleClass().add("button");
        profileButton.setOnAction(e ->{
            ProfilePage profilePage = new ProfilePage();
            try {
                profilePage.start(userID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            stage.close();
        });
        contentCreationArea.getChildren().add(profileButton);
        return contentCreationArea;
    }
}

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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;

import static java.awt.Color.black;

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
//        mainLayout.setPadding(new Insets(10));

        // Top: Stories
        HBox storiesSection = createStoriesSection(userID);

        // Top Center: Content Creation Area
        HBox contentCreationArea = createContentCreationArea(primaryStage, userID);


        VBox topSection = new VBox(storiesSection,contentCreationArea);


        mainLayout.setTop(topSection);


        // Left side: Friend List
        VBox friendList = createFriendList(primaryStage,userID);
        mainLayout.setLeft(friendList);

        // Center: Posts
        ScrollPane posts = createPosts(userID);
        mainLayout.setCenter(posts);

        // Right side: Friend Suggestions
        VBox friendSuggestions = createFriendSuggestions(primaryStage,userID);
        mainLayout.setRight(friendSuggestions);



        // Add CSS class names
        storiesSection.getStyleClass().add("stories-section");
        friendList.getStyleClass().add("friend-list");
        posts.getStyleClass().add("posts");
        friendSuggestions.getStyleClass().add("friend-suggestions");
        contentCreationArea.getStyleClass().add("content-creation-area");
        mainLayout.getStyleClass().add("main-layout");

        // Scene setup
        Scene scene = new Scene(mainLayout, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("NewsFeedFront.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createStoriesSection(String userID) {
        // Container for all stories
        HBox storiesContainer = new HBox();
        storiesContainer.getStyleClass().add("stories-container");
//        storiesContainer.setPadding(new Insets(10));
//        storiesContainer.setSpacing(10);

        // Add a "Stories" label with fixed width
        Label storiesLabel = new Label("Stories");
        storiesLabel.getStyleClass().add("stories-label");
        storiesLabel.setMinWidth(100); // Fixed width for the "Stories" label

        // Create a horizontal list of stories
        HBox storiesList = new HBox();
        storiesList.getStyleClass().add("stories-list");
//        storiesList.setSpacing(15);
//        storiesList.setPadding(new Insets(10));

        User user = userDatabase.getUserById(userID);

        // Populate the stories list
        for (Story story : getContent.getAllStoriesForUser(user)) {
            VBox singleStory = new VBox();
//            singleStory.setSpacing(5);
            singleStory.getStyleClass().add("single-story");
            // Create story thumbnail
            ImageView storyImage = new ImageView(
                    new Image(getClass().getResource(profileDatabase.getProfile(userID).getProfilePhotoPath()).toExternalForm())
            );
            storyImage.setFitHeight(80);
            storyImage.setFitWidth(80);
            storyImage.setPreserveRatio(true); // Ensures the aspect ratio is maintained
            storyImage.getStyleClass().add("story-image");
// Create a circular clip
            Circle clip = new Circle(40, 40, 40); // x, y are the center of the circle, radius is 40 (half of width/height)
            storyImage.setClip(clip);

            // Story label and timestamp
            Label username = new Label("My Story");
            Label storyDate = new Label(TimestampFormatter.formatTimeAgo(story.getTimestamp()));

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
        storiesScrollPane.setMaxWidth(900); // Fixed width for the ScrollPane
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

                    FriendsPage friendsPage = new FriendsPage();
                    try {
                        friendsPage.start(userID);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    stage.close();
                }
            });

            blockButton.setOnAction(e -> {
                // Handle blocking friend
                if (friendManager.blockFriend(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Blocked", friend.getUsername() + "is succesfully block from the friends list");

                    FriendsPage friendsPage = new FriendsPage();
                    try {
                        friendsPage.start(userID);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    stage.close();
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
        postsBox.getStyleClass().add("posts-box");

        Label postsLabel = new Label("Recent Posts");
        postsLabel.getStyleClass().add("posts-label");
        postsBox.getChildren().add(postsLabel);

        User user = userDatabase.getUserById(userID);

        // Populate the posts list
        for (Post post : getContent.getAllPostsForUser(user)) {
            VBox singlePost = new VBox();
            singlePost.getStyleClass().add("single-post");

            // Author image and username
            File authorImageFile = new File("src/main/resources" + profileDatabase.getProfile(userID).getProfilePhotoPath());
            ImageView authorImage = new ImageView(new Image(authorImageFile.toURI().toString()));
            authorImage.setFitWidth(35);
            authorImage.setFitHeight(35);
            Label username = new Label(user.getUsername());
            username.getStyleClass().add("post-authorname");
            Label time = new Label(TimestampFormatter.formatTimestamp(post.getTimestamp()));
            time.getStyleClass().add("post-time");
            HBox imageAndName = new HBox(authorImage,username);
            imageAndName.getStyleClass().add("image-and-name");

            // Post content (TextArea) with fixed size and scrollable
            TextArea postText = new TextArea(post.getContent());
            postText.getStyleClass().add("post-text");
            postText.setEditable(false);
            postText.setWrapText(true); // Allow text wrapping
            postText.setPrefHeight(50); // Set fixed height
            postText.setPrefWidth(400); // Set fixed width
            postText.setScrollTop(0); // Ensure the content is scrollable

            // Add components to the single post VBox
            singlePost.getChildren().addAll(imageAndName);
            singlePost.getChildren().add(time);
            // Optional post thumbnail image
            if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
                try {
                    File postImageFile = new File("src/main/resources" + post.getImagePath());
                    Image postImageContent = new Image(postImageFile.toURI().toString());
                    ImageView postImage = new ImageView(postImageContent);
                    postImage.getStyleClass().add("post-image");

// Check the actual width of the image
                    if (postImageContent.getWidth() > 300) {
                        postImage.setFitWidth(300);
                        postImage.setPreserveRatio(true);
                    }

                    HBox imageBox = new HBox(postImage);
                    imageBox.getStyleClass().add("image-box");
                    singlePost.getChildren().add(imageBox);
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
        scrollPane.getStyleClass().add("post-scroll-pane");

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
                if (friendManager.sendFriendRequest(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Request", "Friend Request is sent to " + friend.getUsername());
                    FriendsPage friendsPage = new FriendsPage();
                    try {
                        friendsPage.start(userID);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    stage.close();
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
//        contentCreationArea.setSpacing(10);
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

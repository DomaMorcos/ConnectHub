package connecthub.NewsfeedPage.Frontend;

import connecthub.AlertUtils;
import connecthub.Chatting.Frontend.ChattingPage;
import connecthub.ContentCreation.Backend.ContentDatabase;
import connecthub.ContentCreation.Backend.GetContent;
import connecthub.ContentCreation.Backend.Post;
import connecthub.ContentCreation.Backend.Story;
import connecthub.ContentCreation.Frontend.AddPost;
import connecthub.ContentCreation.Frontend.AddStory;
import connecthub.ContentCreation.Frontend.DisplayStory;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.FriendManagement.Frontend.SearchGroupPage;
import connecthub.FriendManagement.Frontend.SearchUserPage;
import connecthub.Groups.Backend.Group;
import connecthub.Groups.Backend.GroupDatabase;
import connecthub.Groups.Backend.GroupPost;
import connecthub.Groups.Frontend.AddGroupPostComment;
import connecthub.Groups.Frontend.CreateGroup;
import connecthub.Groups.Frontend.DisplayGroupPostComments;
import connecthub.Groups.Frontend.GroupPage;
import connecthub.NewsfeedPage.Backend.ImplementedNewsfeedBack;
import connecthub.NotificationSystem.frontend.NotificationPage;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Frontend.ProfilePage;
import connecthub.TimestampFormatter;
import connecthub.UserAccountManagement.Backend.LogUser;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;

public class NewsFeedFront {
    private FriendManager friendManager = FriendManager.getInstance();
    ContentDatabase contentDatabase = ContentDatabase.getInstance();
    UserDatabase userDatabase = UserDatabase.getInstance();
    GetContent getContent = GetContent.getInstance();
    ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
    ImplementedNewsfeedBack newsfeed = new ImplementedNewsfeedBack();
    GroupDatabase groupDatabase = GroupDatabase.getInstance();
    private VBox postsBox , friendSuggestionsVBox,friendsVBox,joinedGroups,suggestedGroups;
    private HBox storiesContainer;

    public void start(String userID) throws Exception {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Newsfeed Page");

        // Main layout
        BorderPane mainLayout = new BorderPane();

        // Top: Stories
        HBox storiesSection = createStoriesSection(userID);

        // Top Center: Content Creation Area
        HBox contentCreationArea = createContentCreationArea(primaryStage, userID);


        VBox topSection = new VBox(storiesSection, contentCreationArea);


        mainLayout.setTop(topSection);


        // Left side: Friend List
        VBox friendList = createFriendList(primaryStage, userID);
        VBox friendSuggestions = createFriendSuggestions(primaryStage, userID);
        HBox leftSection = new HBox(friendList, friendSuggestions);
        mainLayout.setLeft(leftSection);

        // Center: Posts
        ScrollPane posts = createPosts(userID, primaryStage);
        mainLayout.setCenter(posts);

        // Right side: Joined Groups
        VBox joinedGroups = createJoinedGroups(primaryStage, userID);
        VBox suggestedGroups = createSuggestedGroup(primaryStage, userID);
        VBox groupsSection = new VBox(joinedGroups, suggestedGroups);
        mainLayout.setRight(groupsSection);

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
        primaryStage.setOnCloseRequest(e -> {
            User user = userDatabase.getUserById(userID);
            LogUser logUser = new LogUser();
            logUser.logout(user.getEmail());
        });
        primaryStage.show();
    }

    private HBox createStoriesSection(String userID) {
        // Container for all stories
        storiesContainer = new HBox();

        refreshStoriesSection(userID);

        return storiesContainer;
    }
    private void refreshStoriesSection(String userID) {
        storiesContainer.getChildren().clear();
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

        for (Story story : getContent.getAllStoriesForUser(user)) {
            VBox singleStory = new VBox();
//            singleStory.setSpacing(5);
            singleStory.getStyleClass().add("single-story");
            // Create story thumbnail
            File storyImageFile = new File("src/main/resources" + profileDatabase.getProfile(user.getUserId()).getProfilePhotoPath());
            ImageView storyImage = new ImageView(new Image(storyImageFile.toURI().toString()));
            storyImage.setFitHeight(80);
            storyImage.setFitWidth(80);
            storyImage.setPreserveRatio(true); // Ensures the aspect ratio is maintained
            storyImage.getStyleClass().add("story-image");
            // Create a circular clip
            Circle clip = new Circle(40, 40, 40); // x, y are the center of the circle, radius is 40 (half of width/height)
            storyImage.setClip(clip);

            // Story label and timestamp
            Label username = new Label(user.getUsername());
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


        // Populate the stories list

        for (Story story : newsfeed.getFriendsStories(userID)) {
            VBox singleStory = new VBox();
            User friend = userDatabase.getUserById(story.getAuthorId());
//            singleStory.setSpacing(5);
            singleStory.getStyleClass().add("single-story");
            // Create story thumbnail
            File storyImageFile = new File("src/main/resources" + profileDatabase.getProfile(friend.getUserId()).getProfilePhotoPath());
            ImageView storyImage = new ImageView(new Image(storyImageFile.toURI().toString()));
            storyImage.setFitHeight(80);
            storyImage.setFitWidth(80);
            storyImage.setPreserveRatio(true); // Ensures the aspect ratio is maintained
            storyImage.getStyleClass().add("story-image");

            // Create a circular clip
            Circle clip = new Circle(40, 40, 40); // x, y are the center of the circle, radius is 40 (half of width/height)
            storyImage.setClip(clip);

            // Story label and timestamp
            Label username = new Label(friend.getUsername());
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

    }

    private VBox createFriendList(Stage stage, String userID) {
        friendsVBox = new VBox(10);
        friendsVBox.setPadding(new Insets(10));

        refreshFriendList(userID);
        stage.close();
        return friendsVBox;
    }

    private void refreshFriendList(String userID) {
        friendsVBox.getChildren().clear(); // Clear existing children to refresh the list

        Label friendsLabel = new Label("My Friends");
        friendsVBox.getChildren().add(friendsLabel);
        profileDatabase.loadProfiles();

        userDatabase.readUsersFromJsonFile();
        // Simulate loading friends (in a real app, fetch from backend)
        for (User friend : friendManager.getFriendsList(userID)) {
            Label friendName = new Label(friend.getUsername());

            File friendImageFile = new File("src/main/resources" + profileDatabase.getProfile(friend.getUserId()).getProfilePhotoPath());
            ImageView friendImage = new ImageView(new Image(friendImageFile.toURI().toString()));

            File statusImageFile = new File(
                    friend.getStatus().equals("online") ? "src/main/resources/Images/greenDot.png" : "src/main/resources/Images/redDot.png"
            );
            ImageView statusImage = new ImageView(new Image(statusImageFile.toURI().toString()));
            statusImage.setFitWidth(10);
            statusImage.setFitHeight(10);

            friendImage.setFitWidth(25);
            friendImage.setFitHeight(25);

            Label status = new Label(friend.getStatus());
            Button Chatting = new Button("Chatting");
            Chatting.setOnAction(e -> {
                ChattingPage chattingPage = new ChattingPage();
                try {
                    chattingPage.start(userID,friend.getUserId());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                //stage.close();
            });
            HBox userFriend = new HBox(friendImage, friendName, statusImage, status ,Chatting);
            userFriend.setSpacing(10);

            friendsVBox.getChildren().add(userFriend);
        }
    }

    private ScrollPane createPosts(String userID, Stage stage) {
        // Create a ScrollPane to make posts scrollable
        postsBox = new VBox();
        refreshPosts(userID, stage);

        ScrollPane scrollPane = new ScrollPane(postsBox);
        scrollPane.setFitToWidth(true); // Ensure the ScrollPane stretches to fit the width of the postsBox
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show vertical scrollbar
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Disable horizontal scrollbar
        scrollPane.getStyleClass().add("post-scroll-pane");

        return scrollPane;
    }

    private void refreshPosts(String userID, Stage stage) {
        postsBox.getChildren().clear();
        postsBox.getStyleClass().add("posts-box");

        Label postsLabel = new Label("Recent Posts");
        postsLabel.getStyleClass().add("posts-label");
        postsBox.getChildren().add(postsLabel);
        User user = userDatabase.getUserById(userID);

        for (Post post : getContent.getAllPostsForUser(user)) {
            VBox singlePost = new VBox();
            singlePost.getStyleClass().add("single-post");
            // Author image and username
            File authorImageFile = new File("src/main/resources" + profileDatabase.getProfile(user.getUserId()).getProfilePhotoPath());
            ImageView authorImage = new ImageView(new Image(authorImageFile.toURI().toString()));
            authorImage.setFitWidth(35);
            authorImage.setFitHeight(35);
            Label username = new Label(user.getUsername());
            username.getStyleClass().add("post-authorname");
            Label time = new Label(TimestampFormatter.formatTimestamp(post.getTimestamp()));
            time.getStyleClass().add("post-time");
            HBox imageAndName = new HBox(authorImage, username);
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
            Label myPostNumOfLikes = new Label(String.valueOf(post.getNumberLikes()));
            Button myPostLikeButton = new Button("Like");
            Button commentsButton = new Button("Comments");
            boolean[] isClicked = {post.hasLiked(userID)}; // Mutable flag
            if (isClicked[0]) {
                myPostLikeButton.setStyle("-fx-background-color: yellow; -fx-text-fill: red;");
            } else {
                myPostLikeButton.setStyle("-fx-background-color: lightblue; -fx-text-fill: black;");
            }
            myPostLikeButton.setOnAction(event -> {
                if (isClicked[0]) {
                    myPostLikeButton.setStyle("-fx-background-color: lightblue; -fx-text-fill: black;");
                    post.removeLike(userID);
                    contentDatabase.saveContents();
                    myPostNumOfLikes.setText(String.valueOf(post.getNumberLikes())); // Update like count

//                    refreshPosts(userID, stage);
                } else {
                    myPostLikeButton.setStyle("-fx-background-color: yellow; -fx-text-fill: red;");
                    post.addLike(userID);
                    contentDatabase.saveContents();
//                    refreshPosts(userID, stage);
                    myPostNumOfLikes.setText(String.valueOf(post.getNumberLikes())); // Update like count



                }
                isClicked[0] = !isClicked[0]; // Toggle flag
            });

            commentsButton.setOnAction(e -> {
                // Create a new dialog for search options
                Dialog<String> searchDialog = new Dialog<>();
                searchDialog.setTitle("Comments");
                searchDialog.setHeaderText("Add / View Comments");

                // Add buttons for choices
                ButtonType addComments = new ButtonType("Add Comment");
                ButtonType viewComments = new ButtonType("View Comments");
                ButtonType cancel = ButtonType.CANCEL;

                searchDialog.getDialogPane().getButtonTypes().addAll(addComments, viewComments, cancel);

                // Handle button clicks
                searchDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == addComments) {
                        return "add";
                    } else if (dialogButton == viewComments) {
                        return "view";
                    }
                    return null;
                });

                searchDialog.showAndWait().ifPresent(result -> {
                    if ("add".equals(result)) {
                        AddPostComments addPostComments = new AddPostComments();
                        addPostComments.start(userID, post);
                    } else if ("view".equals(result)) {
                        DisplayPostComments displayPostComments = new DisplayPostComments();
                        displayPostComments.start(userID, post);
                    }
                });
            });

            HBox likesAndCommentsBar = new HBox(myPostNumOfLikes, myPostLikeButton, commentsButton);
            singlePost.getChildren().add(likesAndCommentsBar);
            // Add the single post to the postsBox
            postsBox.getChildren().add(singlePost);
        }


        // Populate the posts list

        for (Post post : newsfeed.getFriendsPosts(userID)) {
            VBox singlePost = new VBox();
            singlePost.getStyleClass().add("single-post");
            User friend = userDatabase.getUserById(post.getAuthorId());
            // Author image and username
            File authorImageFile = new File("src/main/resources" + profileDatabase.getProfile(friend.getUserId()).getProfilePhotoPath());
            ImageView authorImage = new ImageView(new Image(authorImageFile.toURI().toString()));
            authorImage.setFitWidth(35);
            authorImage.setFitHeight(35);
            Label username = new Label(friend.getUsername());
            username.getStyleClass().add("post-authorname");
            Label time = new Label(TimestampFormatter.formatTimestamp(post.getTimestamp()));
            time.getStyleClass().add("post-time");
            HBox imageAndName = new HBox(authorImage, username);
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
            Label friendsPostNumOfLikes = new Label(String.valueOf(post.getNumberLikes()));
            Button friendsPostLikeButton = new Button("Like");
            Button commentsButton = new Button("Comments");
            boolean[] isClicked = {post.hasLiked(userID)}; // Mutable flag
            if (isClicked[0]) {
                friendsPostLikeButton.setStyle("-fx-background-color: yellow; -fx-text-fill: red;");
            } else {
                friendsPostLikeButton.setStyle("-fx-background-color: lightblue; -fx-text-fill: black;");
            }
            friendsPostLikeButton.setOnAction(event -> {
                if (isClicked[0]) {
                    friendsPostLikeButton.setStyle("-fx-background-color: lightblue; -fx-text-fill: black;");
                    post.removeLike(userID);
                    contentDatabase.saveContents();
//                    refreshPosts(userID, stage);
                    friendsPostNumOfLikes.setText(String.valueOf(post.getNumberLikes())); // Update like count




                } else {
                    friendsPostLikeButton.setStyle("-fx-background-color: yellow; -fx-text-fill: red;");
                    post.addLike(userID);
                    contentDatabase.saveContents();

//                    refreshPosts(userID, stage);
                    friendsPostNumOfLikes.setText(String.valueOf(post.getNumberLikes())); // Update like count


                }
                isClicked[0] = !isClicked[0]; // Toggle flag
            });

            commentsButton.setOnAction(e -> {
                // Create a new dialog for search options
                Dialog<String> searchDialog = new Dialog<>();
                searchDialog.setTitle("Comments");
                searchDialog.setHeaderText("Add / View Comments");

                // Add buttons for choices
                ButtonType addComments = new ButtonType("Add Comment");
                ButtonType viewComments = new ButtonType("View Comments");
                ButtonType cancel = ButtonType.CANCEL;

                searchDialog.getDialogPane().getButtonTypes().addAll(addComments, viewComments, cancel);

                // Handle button clicks
                searchDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == addComments) {
                        return "add";
                    } else if (dialogButton == viewComments) {
                        return "view";
                    }
                    return null;
                });

                searchDialog.showAndWait().ifPresent(result -> {
                    if ("add".equals(result)) {
                        AddPostComments addPostComments = new AddPostComments();
                        addPostComments.start(userID, post);
                    } else if ("view".equals(result)) {
                        DisplayPostComments displayPostComments = new DisplayPostComments();
                        displayPostComments.start(userID, post);
                    }
                });
            });

            HBox likesAndCommentsBar = new HBox(friendsPostNumOfLikes, friendsPostLikeButton, commentsButton);
            singlePost.getChildren().add(likesAndCommentsBar);
            // Add the single post to the postsBox
            postsBox.getChildren().add(singlePost);
        }

        for (GroupPost post : groupDatabase.getAllPostsForAllGroupsForUser(userID)) {
            String groupID = post.getGroupID();
            User postAuthor = userDatabase.getUserById(post.getAuthorId());
            VBox singlePost = new VBox();
            singlePost.getStyleClass().add("single-post");
            // Author image and username
            File authorImageFile = new File("src/main/resources" + profileDatabase.getProfile(postAuthor.getUserId()).getProfilePhotoPath());
            ImageView authorImage = new ImageView(new Image(authorImageFile.toURI().toString()));
            authorImage.setFitWidth(35);
            authorImage.setFitHeight(35);
            Label username = new Label(postAuthor.getUsername());
            username.getStyleClass().add("post-authorname");
            Label groupName = new Label("Group - " + post.getPostId());
            Label time = new Label(TimestampFormatter.formatTimestamp(post.getTimestamp()));
            time.getStyleClass().add("post-time");
            HBox imageAndName = new HBox(authorImage, username, groupName);
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
            Label numOfLikes = new Label(String.valueOf(post.getGroupNumberLikes()));
            Button likeButton = new Button("Like");
            Button commentsButton = new Button("Comments");
            boolean[] isClicked = {post.hasLiked(userID)}; // Mutable flag
            if (isClicked[0]) {
                likeButton.setStyle("-fx-background-color: yellow; -fx-text-fill: red;");
            } else {
                likeButton.setStyle("-fx-background-color: lightblue; -fx-text-fill: black;");
            }
            likeButton.setOnAction(event -> {
                if (isClicked[0]) {
                    likeButton.setStyle("-fx-background-color: lightblue; -fx-text-fill: black;");
                    post.removeGroupLike(userID);
                    refreshPosts(userID, stage);
//                    numOfLikes.setText(String.valueOf(post.getGroupNumberLikes())); // Update like count
                } else {
                    likeButton.setStyle("-fx-background-color: yellow; -fx-text-fill: red;");
                    post.addGroupLike(userID);
                    refreshPosts(userID, stage);
//                    numOfLikes.setText(String.valueOf(post.getGroupNumberLikes())); // Update like count
                }
                isClicked[0] = !isClicked[0]; // Toggle flag
            });

            commentsButton.setOnAction(e -> {
                // Create a new dialog for search options
                Dialog<String> searchDialog = new Dialog<>();
                searchDialog.setTitle("Comments");
                searchDialog.setHeaderText("Add / View Comments");

                // Add buttons for choices
                ButtonType addComments = new ButtonType("Add Comment");
                ButtonType viewComments = new ButtonType("View Comments");
                ButtonType cancel = ButtonType.CANCEL;

                searchDialog.getDialogPane().getButtonTypes().addAll(addComments, viewComments, cancel);

                // Handle button clicks
                searchDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == addComments) {
                        return "add";
                    } else if (dialogButton == viewComments) {
                        return "view";
                    }
                    return null;
                });

                searchDialog.showAndWait().ifPresent(result -> {
                    if ("add".equals(result)) {
                        AddGroupPostComment addGroupPostComment = new AddGroupPostComment();
                        addGroupPostComment.start(groupID, userID, post);
                    } else if ("view".equals(result)) {
                        DisplayGroupPostComments displayGroupPostComments = new DisplayGroupPostComments();
                        displayGroupPostComments.start(userID, groupID, post);
                    }
                });
            });

            HBox likesAndCommentsBar = new HBox(numOfLikes, likeButton, commentsButton);
            singlePost.getChildren().add(likesAndCommentsBar);
            // Add the single post to the postsBox
            postsBox.getChildren().add(singlePost);
        }


    }


    private VBox createFriendSuggestions(Stage stage, String userID) {
        friendSuggestionsVBox = new VBox(10);

        refreshFriendSuggestions(userID);
        return friendSuggestionsVBox;
    }
    private void refreshFriendSuggestions(String userID){
        friendSuggestionsVBox.getChildren().clear();
        friendSuggestionsVBox.setPadding(new Insets(10));

        Label suggestionsLabel = new Label("Friend Suggestions");
        friendSuggestionsVBox.getChildren().add(suggestionsLabel);
        userDatabase.readUsersFromJsonFile();
        profileDatabase.loadProfiles();

        for (User friend : friendManager.suggestFriends(userID)) {
            Label username = new Label(friend.getUsername());
            Button sendFriendRequest = new Button("Send Request");

            sendFriendRequest.setOnAction(e -> {
                if (friendManager.sendFriendRequest(userID, friend.getUserId())) {
                    AlertUtils.showInformationMessage("Friend Request", "Friend request sent to " + friend.getUsername() + "!");
                }
            });

            VBox suggestionBox = new VBox(5);
            suggestionBox.getChildren().addAll(username, sendFriendRequest);
            friendSuggestionsVBox.getChildren().add(suggestionBox);
        }
    }
    private HBox createContentCreationArea(Stage stage, String userID) {
        HBox contentCreationArea = new HBox();
        contentCreationArea.setPadding(new Insets(10));
//        contentCreationArea.setSpacing(10);
        Button createGroupButton = new Button("Create Group");
        createGroupButton.setOnAction(e -> {
            CreateGroup createGroup = new CreateGroup();
            createGroup.start(userID);
            stage.close();
        });
        contentCreationArea.getChildren().add(createGroupButton);
        Button storyButton = new Button("Story");
        storyButton.getStyleClass().add("button");
        storyButton.setOnAction(e -> {
            AddStory addStory = new AddStory();
            addStory.start(userID);
            NewsFeedFront newsFeedFront = new NewsFeedFront();
            try {
                stage.close();
                newsFeedFront.start(userID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        });

        Button postButton = new Button("Post");
        postButton.getStyleClass().add("button");
        postButton.setOnAction(e -> {
            AddPost addPost = new AddPost();
            addPost.start(userID);
            NewsFeedFront newsFeedFront = new NewsFeedFront();
            try {
                stage.close();
                newsFeedFront.start(userID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        });
        contentCreationArea.getChildren().addAll(storyButton, postButton);

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("button");
        refreshButton.setOnAction(e -> {
            contentDatabase.loadContents();
            refreshFriendSuggestions(userID);
            refreshStoriesSection(userID);
            refreshFriendList(userID);
            refreshSuggestedGroups(stage,userID);
            refreshJoinedGroups(stage,userID);
            refreshPosts(userID,stage);
        });
        contentCreationArea.getChildren().add(refreshButton);

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("button");
        searchButton.setOnAction(e -> {
            // Create a new dialog for search options
            Dialog<String> searchDialog = new Dialog<>();
            searchDialog.setTitle("Search Options");
            searchDialog.setHeaderText("Choose your search method:");

            // Add buttons for choices
            ButtonType searchByGroup = new ButtonType("Search by Group");
            ButtonType searchByUser = new ButtonType("Search by User");
            ButtonType cancel = ButtonType.CANCEL;

            searchDialog.getDialogPane().getButtonTypes().addAll(searchByGroup, searchByUser, cancel);

            // Handle button clicks
            searchDialog.setResultConverter(dialogButton -> {
                if (dialogButton == searchByGroup) {
                    return "group";
                } else if (dialogButton == searchByUser) {
                    return "user";
                }
                return null;
            });

            // Show the dialog and process the result
            searchDialog.showAndWait().ifPresent(result -> {
                if ("group".equals(result)) {
                    // Navigate to Search by Group
                    SearchGroupPage searchGroupPage = new SearchGroupPage();
                    try {
                        stage.close();
                        searchGroupPage.start(userID); // Pass userID to the new page
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                } else if ("user".equals(result)) {
                    // Navigate to Search by User
                    SearchUserPage searchUserPage = new SearchUserPage(); //
                    try {
                        stage.close();
                        searchUserPage.start(userID); // Pass userID to the new page
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        });
        contentCreationArea.getChildren().add(searchButton);

        Button profileButton = new Button("Profile");
        profileButton.getStyleClass().add("button");
        profileButton.setOnAction(e -> {
            ProfilePage profilePage = new ProfilePage();
            try {
                stage.close();
                profilePage.start(userID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        contentCreationArea.getChildren().add(profileButton);

        // New Notification button
        Button notificationButton = new Button("Notifications");
        notificationButton.getStyleClass().add("button");
        notificationButton.setOnAction(e -> {
            NotificationPage notificationPage = new NotificationPage(); // Needs to be implemented.
            try {
                stage.close();
                notificationPage.start(userID); // Open the notification page.
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        contentCreationArea.getChildren().add(notificationButton);


        return contentCreationArea;
    }

    private VBox createJoinedGroups(Stage stage, String userID) {
        joinedGroups = new VBox();
        refreshJoinedGroups(stage,userID);
        return joinedGroups;
    }
    private void refreshJoinedGroups(Stage stage, String userID) {
        joinedGroups.getChildren().clear();
        groupDatabase.loadGroupsFromJsonFile();
        Label joinedGroupsLabel = new Label("Groups Joined");
        joinedGroups.getChildren().add(joinedGroupsLabel);
        for (Group group : groupDatabase.getGroupsForUser(userID)) {
            Label groupLabel = new Label(group.getName());
            groupLabel.setOnMouseClicked(e -> {
                GroupPage groupPage = new GroupPage();
                stage.close();
                groupPage.start(userID, group.getGroupId());
            });
            HBox singleGroup = new HBox(groupLabel);
            joinedGroups.getChildren().add(singleGroup);
        }
    }

    private VBox createSuggestedGroup(Stage stage, String userID) {
        suggestedGroups = new VBox();
        refreshSuggestedGroups(stage,userID);
        return suggestedGroups;
    }
    private void refreshSuggestedGroups(Stage stage , String userID){
        suggestedGroups.getChildren().clear();
        groupDatabase.loadGroupsFromJsonFile();
        Label suggestedGroupsLabel = new Label("Groups Suggestions");
        suggestedGroups.getChildren().add(suggestedGroupsLabel);
        for (Group group : groupDatabase.getGroupSuggestionsForUser(userID)) {
            Label groupLabel = new Label(group.getName());
            Button joinButton = new Button("Join");
            joinButton.setOnAction(e -> {
                group.requestToJoinGroup(group.getGroupId(), userID);
                AlertUtils.showInformationMessage("Join Group", "A request is sent to join the group");
                refreshSuggestedGroups(stage, userID);
            });
            HBox singleGroup = new HBox(groupLabel, joinButton);
            suggestedGroups.getChildren().add(singleGroup);
        }
    }

}

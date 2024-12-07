package connecthub.ProfileManagement.Frontend;

import connecthub.ContentCreation.Backend.ContentDatabase;
import connecthub.ContentCreation.Backend.GetContent;
import connecthub.ContentCreation.Backend.Post;
import connecthub.FriendManagement.Frontend.FriendsPage;
import connecthub.NewsfeedPage.Frontend.NewsFeedFront;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Backend.ProfileManager;
import connecthub.ProfileManagement.Backend.UserProfile;
import connecthub.TimestampFormatter;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



public class ProfilePage {
    private ScrollPane scrollPane;
    private HBox photos,management;
    private VBox posts, profileInfo, mainLayout;
    private ImageView profilePhoto, coverPhoto;
    private Label profileName, bio;
    private Button editBio , newsfeedButton , friendsButton;
    private MenuBar settingMenuBar;
    private MenuItem friends, editProfilePhoto, editCoverPhoto, changePassword, logout;
    ContentDatabase contentDatabase = ContentDatabase.getInstance();
    UserDatabase userDatabase = UserDatabase.getInstance();
    ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
    ProfileManager profileManager = new ProfileManager(contentDatabase, userDatabase);
    GetContent getContent = GetContent.getInstance();

    public void start(String userID) throws Exception {
        Stage stage = new Stage();
//        System.out.println(getClass().getResource("ProfilePage.css"));
//        System.out.println(getClass().getResource("DefaultProfilePhoto.jpg"));

        UserProfile userProfile = profileDatabase.getProfile(userID);

        // Cover Photo
        coverPhoto = new ImageView(new Image(getClass().getResource(userProfile.getCoverPhotoPath()).toExternalForm()));

        coverPhoto.setId("CoverPhoto");
        coverPhoto.setFitHeight(200);
        coverPhoto.setFitWidth(600);

        // Profile Photo

        profilePhoto = new ImageView(new Image(getClass().getResource(userProfile.getProfilePhotoPath()).toExternalForm()));

        profilePhoto.setId("ProfilePhoto");
        profilePhoto.setFitHeight(200);
        profilePhoto.setFitWidth(200);

        // Cover and Profile Photo in a Horizontal Box;
        photos = new HBox();
        photos.setId("PhotosBox");
//        photos.setPadding(new Insets(10));
//        photos.setSpacing(10);
        photos.getChildren().addAll(profilePhoto , coverPhoto);

        User user = userDatabase.getUserById(userID);
        // Profile Info
        profileName = new Label(user.getUsername());
        profileName.setId("ProfileName");
        bio = new Label(userProfile.getBio());
        bio.setId("Bio");
        editBio = new Button("Edit Bio");
        editBio.setId("EditBio");
        editBio.setOnAction(e -> {
            Optional<String> result = handleEditBio();
            result.ifPresent(newBio -> {
                userProfile.setBio(newBio);

                profileDatabase.updateProfile(userProfile);

                ProfilePage profilePage = new ProfilePage();
                try {
                    profilePage.start(userID);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                stage.close();

            });
        });


        management = new HBox();
        management.setId("Management");
        newsfeedButton = new Button("NewsFeed");
        newsfeedButton.setOnAction(e -> {
            NewsFeedFront newsFeedFront = new NewsFeedFront();
            try {
                newsFeedFront.start(userID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            stage.close();
        });
        friendsButton = new Button("Friends");
        friendsButton.setOnAction(e ->{
            FriendsPage friendsPage = new FriendsPage();
            try {
                friendsPage.start(userID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        management.getChildren().addAll(editBio,newsfeedButton,friendsButton);

        profileInfo = new VBox();
        profileInfo.setId("ProfileInfo");
//        profileInfo.setPadding(new Insets(10));
//        profileInfo.setSpacing(10);
        profileInfo.getChildren().addAll(photos, profileName,management,bio);

// Initialize the class-level `posts`
        ScrollPane posts = createPosts(userID);
// Scrollable Posts

        posts.setFitToWidth(true);

// Main Layout
        mainLayout = new VBox();
//        mainLayout.setPadding(new Insets(10));
//        mainLayout.setSpacing(20);
        mainLayout.getChildren().addAll(profileInfo, posts);


        VBox root = new VBox();
        root.getChildren().addAll(mainLayout);

        // Menu
        settingMenuBar = new MenuBar();
        Menu settingsMenu = new Menu("Settings");
        editProfilePhoto = new MenuItem("Edit Profile Photo");
        editCoverPhoto = new MenuItem("Edit Cover Photo");
        changePassword = new MenuItem("Change Password");
        logout = new MenuItem("Logout");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Handle editProfilePhoto click event
        editProfilePhoto.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                userProfile.setProfilePhotoPath(file.getAbsolutePath());
                ProfilePage profilePage = new ProfilePage();
                try {
                    profilePage.start(userID);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Handle editCoverPhoto click event
        editCoverPhoto.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                userProfile.setCoverPhotoPath(file.getAbsolutePath());
                ProfilePage profilePage = new ProfilePage();
                try {
                    profilePage.start(userID);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        changePassword.setOnAction(e -> {
            Optional<String> result = handleChangePassword();
            result.ifPresent(newPassword -> profileManager.updatePassword(userID, newPassword));
        });

        settingsMenu.getItems().addAll(editProfilePhoto, editCoverPhoto, changePassword, logout);
        settingMenuBar.getMenus().add(settingsMenu);

        VBox layout = new VBox(settingMenuBar, root);
//        layout.setSpacing(10);

        Scene scene = new Scene(layout, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("ProfilePage.css").toExternalForm());
        stage.setTitle("Profile Page");
        stage.setScene(scene);
        stage.show();
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


    private Optional<String> handleEditBio() {
        TextInputDialog dialog = new TextInputDialog("Enter Bio");
        dialog.setTitle("Edit Bio");
        dialog.setHeaderText("Edit");
        dialog.setContentText("Please enter some text:");
        return dialog.showAndWait();
    }

    private Optional<String> handleChangePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change Password");
        dialog.setContentText("Please enter your new password:");

        while (true) {
            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty() || result.get().trim().isEmpty()) {
                dialog.setHeaderText("Password cannot be empty. Please try again.");
            } else {
                return result;
            }
        }
    }

}

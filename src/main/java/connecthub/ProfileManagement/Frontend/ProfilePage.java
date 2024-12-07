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
import javafx.application.Platform;
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
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



public class ProfilePage {
    private ScrollPane scrollPane;
    private HBox photos,management;
    private VBox posts, profileInfo, mainLayout;
    private ImageView profilePhoto, coverPhoto;
    private Label profileName,bioLabel;
    private TextArea bio;
    private Button editBio , newsfeedButton , friendsButton;
    private MenuBar settingMenuBar;
    private MenuItem friends, editProfilePhoto, editCoverPhoto, changePassword, logout;
    ContentDatabase contentDatabase = ContentDatabase.getInstance();
    UserDatabase userDatabase = UserDatabase.getInstance();
    ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
    ProfileManager profileManager = new ProfileManager(contentDatabase, userDatabase);
    GetContent getContent = GetContent.getInstance();
    private File selectedImage;

    private static final String DESTINATION_FOLDER = "src/main/resources/Images/";


    public void start(String userID) throws Exception {
        Stage stage = new Stage();
//        System.out.println(getClass().getResource("ProfilePage.css"));
//        System.out.println(getClass().getResource("DefaultProfilePhoto.jpg"));

        UserProfile userProfile = profileDatabase.getProfile(userID);

        // Cover Photo

        File coverImageFile = new File("src/main/resources" + userProfile.getCoverPhotoPath());
        Image coverImageContent = new Image(coverImageFile.toURI().toString());
        coverPhoto= new ImageView(coverImageContent);

        coverPhoto.setId("CoverPhoto");
        coverPhoto.setFitHeight(150);
        coverPhoto.setFitWidth(600);

        // Profile Photo

        File profileImageFile = new File("src/main/resources" + userProfile.getProfilePhotoPath());
        Image profileImageContent = new Image(profileImageFile.toURI().toString());
        profilePhoto= new ImageView(profileImageContent);


        profilePhoto.setId("ProfilePhoto");
        profilePhoto.setFitHeight(150);
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

        bioLabel = new Label("Biography");
        bioLabel.getStyleClass().add("bio-label");
        bio = new TextArea(userProfile.getBio());
        bio.setWrapText(true); // Allow text wrapping
        bio.setPrefHeight(100); // Set fixed height
        bio.setPrefWidth(300); // Set fixed width
        bio.setScrollTop(0); // Ensure the content is scrollable
        VBox bioBox = new VBox(bioLabel,bio);
        bioBox.getStyleClass().add("bio-box");
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
        profileInfo.getChildren().addAll(photos, profileName,management,bioBox);
        profileInfo.setMaxHeight(400);
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
// Handle editProfilePhoto click event
        editProfilePhoto.setOnAction(event -> {
            String imagePath = openImageChooser(stage);
            System.out.println(imagePath);
            if (imagePath != null) {
                userProfile.setProfilePhotoPath(imagePath);
                ProfilePage profilePage = new ProfilePage();
                try {
                    profilePage.start(userID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                stage.close();
            } else {
                // Handle case where no image was selected
                System.out.println("No image selected for profile photo.");
            }
        });

// Handle editCoverPhoto click event
        editCoverPhoto.setOnAction(event -> {
            String imagePath = openImageChooser(stage);
            if (imagePath != null) {
                userProfile.setCoverPhotoPath(imagePath);
                ProfilePage profilePage = new ProfilePage();
                try {
                    profilePage.start(userID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                stage.close();
            } else {
                // Handle case where no image was selected
                System.out.println("No image selected for cover photo.");
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




    private String openImageChooser(Stage primaryStage) {
        // Create a FileChooser to filter image files
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif"));

        // Show the FileChooser dialog
        File selectedImage = fileChooser.showOpenDialog(primaryStage);

        // If an image is selected, handle the file copying
        if (selectedImage != null) {
            try {
                // Ensure the destination folder exists
                File destinationDir = new File(DESTINATION_FOLDER);
                if (!destinationDir.exists()) {
                    destinationDir.mkdirs();
                }

                // Create destination file path for the selected image
                Path sourcePath = selectedImage.toPath();
                Path destinationPath = new File(DESTINATION_FOLDER, selectedImage.getName()).toPath();

                // Force file copy and flush immediately
                try (FileChannel sourceChannel = FileChannel.open(sourcePath);
                     FileChannel destinationChannel = FileChannel.open(destinationPath,
                             StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                }

                // Return the relative path of the uploaded image
                return "/Images/" + selectedImage.getName();

            } catch (IOException ex) {
                // Show an error message if file copying fails
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to upload image");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }

        // Return null if no image is selected
        return null;
    }





}

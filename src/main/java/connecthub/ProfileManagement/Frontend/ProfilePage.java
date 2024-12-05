package connecthub.ProfileManagement.Frontend;

import connecthub.ContentCreation.Backend.ContentDatabase;
import connecthub.ContentCreation.Backend.GetContent;
import connecthub.ContentCreation.Backend.Post;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Backend.ProfileManager;
import connecthub.ProfileManagement.Backend.UserProfile;
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
    private HBox photos,bioBox;
    private VBox posts, profileInfo, mainLayout;
    private ImageView profilePhoto, coverPhoto;
    private Label profileName, bio;
    private Button editBio, createPost;
    private MenuBar settingMenuBar;
    private MenuItem friends, editProfilePhoto, editCoverPhoto, changePassword, logout;
    ContentDatabase contentDatabase = ContentDatabase.getInstance();
    UserDatabase userDatabase = UserDatabase.getInstance();
    ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
    ProfileManager profileManager = new ProfileManager(contentDatabase, userDatabase);


    public void start(String userID) throws Exception {
        Stage stage = new Stage();
//        System.out.println(getClass().getResource("ProfilePage.css"));
//        System.out.println(getClass().getResource("DefaultProfilePhoto.jpg"));
<<<<<<< HEAD
        UserProfile userProfile = profileDatabase.getProfile(userID);

        // Cover Photo
        coverPhoto = new ImageView(new Image(getClass().getResource(userProfile.getCoverPhotoPath()).toExternalForm()));
=======
        UserProfile userProfile = profileManager.getProfile(userID);

        // Cover Photo
        coverPhoto = new ImageView(new Image(getClass().getResource("DefaultCoverPhoto.png").toExternalForm()));
>>>>>>> c99c91949913f660f9b281ba65474e2e12ad716a
        coverPhoto.setId("CoverPhoto");
        coverPhoto.setFitHeight(200);
        coverPhoto.setFitWidth(600);

        // Profile Photo
<<<<<<< HEAD
        profilePhoto = new ImageView(new Image(getClass().getResource(userProfile.getProfilePhotoPath()).toExternalForm()));
=======
        profilePhoto = new ImageView(new Image(getClass().getResource("DefaultProfilePhoto.jpg").toExternalForm()));
>>>>>>> c99c91949913f660f9b281ba65474e2e12ad716a
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
<<<<<<< HEAD
                profileDatabase.updateProfile(userProfile);
=======
                profileManager.updateProfile(userProfile);
>>>>>>> c99c91949913f660f9b281ba65474e2e12ad716a
                ProfilePage profilePage = new ProfilePage();
                try {
                    profilePage.start(userID);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                stage.close();

            });
        });

        bioBox = new HBox();
        bioBox.setId("BioBox");
        bioBox.getChildren().addAll(bio,editBio);

        profileInfo = new VBox();
        profileInfo.setId("ProfileInfo");
//        profileInfo.setPadding(new Insets(10));
//        profileInfo.setSpacing(10);
        profileInfo.getChildren().addAll(photos, profileName,bioBox);

        // Initialize the class-level `posts`
        posts = new VBox();
//        for (Pane postPane : loadPosts(user)) {
//            posts.getChildren().add(postPane);
//        }
//        posts.setPadding(new Insets(10));
//        posts.setSpacing(10);

        // Scrollable Posts
        scrollPane = new ScrollPane(posts);
        scrollPane.setFitToWidth(true);

        // Main Layout
        mainLayout = new VBox();
//        mainLayout.setPadding(new Insets(10));
//        mainLayout.setSpacing(20);
        mainLayout.getChildren().addAll(profileInfo, scrollPane);

        VBox root = new VBox();
        root.getChildren().addAll(mainLayout);

        // Menu
        settingMenuBar = new MenuBar();
        Menu settingsMenu = new Menu("Settings");
        friends = new MenuItem("Friends");
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

        settingsMenu.getItems().addAll(friends, editProfilePhoto, editCoverPhoto, changePassword, logout);
        settingMenuBar.getMenus().add(settingsMenu);

        VBox layout = new VBox(settingMenuBar, root);
//        layout.setSpacing(10);

        Scene scene = new Scene(layout, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("ProfilePage.css").toExternalForm());
        stage.setTitle("Profile Page");
        stage.setScene(scene);
        stage.show();
    }


    private ArrayList<Pane> loadPosts(User user) {
        posts.getChildren().clear();
        ArrayList<Pane> userPosts = new ArrayList<>();
        GetContent getContent = GetContent.getInstance();
        ArrayList<Post> postsList = getContent.getAllPostsForUser(user);
        if (postsList.isEmpty()) {
            Label contentString = new Label("No posts yet!");
            Pane postPane = new Pane();
            postPane.getChildren().add(contentString);
            userPosts.add(postPane);
            return userPosts;

        }
        for (Post post : postsList) {
            Label username = new Label(user.getUsername());
            Label time = new Label(post.getTimestamp());
            Label contentString = new Label(post.getContent());
            if (!post.getImagePath().isEmpty()) {
                ImageView contentImage = new ImageView(new Image(post.getImagePath()));
                Pane postPane = new Pane();
                postPane.getChildren().addAll(username, time, contentString, contentImage);
                userPosts.add(postPane);
                continue;
            }
            Pane postPane = new Pane();
            postPane.getChildren().addAll(username, time, contentString);
            userPosts.add(postPane);
        }
        return userPosts;
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

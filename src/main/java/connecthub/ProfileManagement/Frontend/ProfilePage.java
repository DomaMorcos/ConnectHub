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
    private HBox photos;
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

        UserProfile userProfile = profileManager.getProfile(userID);
        // Cover Photo
        coverPhoto = new ImageView(new Image(userProfile.getCoverPhotoPath()));
        coverPhoto.setFitHeight(200);
        coverPhoto.setFitWidth(800);

        // Profile Photo
        profilePhoto = new ImageView(new Image(userProfile.getProfilePhotoPath()));
        profilePhoto.setFitHeight(200);
        profilePhoto.setFitWidth(200);

        // Cover and Profile Photo in a Horizontal Box;
        photos = new HBox();
        photos.setPadding(new Insets(10));
        photos.setSpacing(10);
        photos.getChildren().addAll(coverPhoto, profilePhoto);


        User user = userDatabase.getUserById(userID);
        // Profile Info
        profileName = new Label(user.getUsername());
        bio = new Label(userProfile.getBio());
        editBio = new Button("Edit Bio");
        editBio.setOnAction(e -> {
            Optional<String> result = handleEditBio();
            result.ifPresent(newBio -> userProfile.setBio(newBio));
        });

        profileInfo = new VBox();
        profileInfo.setPadding(new Insets(10));
        profileInfo.setSpacing(10);
        profileInfo.getChildren().addAll(photos, profileName, bio, editBio);

        // Posts
        VBox posts = new VBox();
        for (Pane postPane : loadPosts(user)) {
            posts.getChildren().add(postPane);
        }
        posts.setPadding(new Insets(10));
        posts.setSpacing(10);

        //Scrollable Posts
        scrollPane = new ScrollPane(posts);
        scrollPane.setFitToWidth(true);

        // Main Layout
        mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10));
        mainLayout.setSpacing(20);
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
//                System.out.println("Selected file for profile photo: " + file.getAbsolutePath());
                userProfile.setProfilePhotoPath(file.getAbsolutePath());
            }
        });

        // Handle editCoverPhoto click event
        editCoverPhoto.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
//                System.out.println("Selected file for cover photo: " + file.getAbsolutePath());
                userProfile.setCoverPhotoPath(file.getAbsolutePath());
            }
        });

        changePassword.setOnAction(e->{
            Optional<String> result = handleChangePassword();
            result.ifPresent(newPassword -> profileManager.updatePassword(userID,newPassword));
        });

        settingsMenu.getItems().addAll(friends, editProfilePhoto, editCoverPhoto, changePassword, logout);
        settingMenuBar.getMenus().add(settingsMenu);

        VBox layout = new VBox(settingMenuBar, root);
        layout.setSpacing(10);

        Scene scene = new Scene(layout, 800, 600);
        stage.setTitle("Profile Page");
        stage.setScene(scene);
        stage.show();
    }

    private ArrayList<Pane> loadPosts(User user) {
        posts.getChildren().clear();
        ArrayList<Pane> userPosts = new ArrayList<>();
        GetContent getContent = new GetContent();
        ArrayList<Post> postsList = getContent.getAllPostsForUser(user);
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

    public Optional<String> handleChangePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Changer Password");
        dialog.setContentText("Please enter your New Password:");

        // Validation loop
        Optional<String> result;
        do {
            result = dialog.showAndWait();
            if (result.isEmpty() || result.get().trim().isEmpty()) {
                dialog.setHeaderText("Input is required. Please try again.");
            } else {
                break; // Valid input
            }
        } while (true);

        return result;
    }
}

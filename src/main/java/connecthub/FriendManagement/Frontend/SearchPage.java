package connecthub.FriendManagement.Frontend;

import connecthub.ContentCreation.Backend.ContentDatabase;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.NewsfeedPage.Frontend.NewsFeedFront;
import connecthub.ProfileManagement.Backend.ProfileManager;
import connecthub.ProfileManagement.Frontend.UserProfilePage;
import connecthub.UserAccountManagement.Backend.LogUser;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class SearchPage {
    private TextField searchField;
    private Button backButton;
    private ListView<String> listView;
    private final FriendManager friendManager = FriendManager.getInstance();
    private final ContentDatabase contentDatabase = ContentDatabase.getInstance();
    private final UserDatabase userDatabase = UserDatabase.getInstance();
    private final ProfileManager profileManager = new ProfileManager(contentDatabase, userDatabase);

    public void start(String userID) {
        Stage stage = new Stage();
        stage.setTitle("Search Page");

        searchField = new TextField();
        searchField.setPromptText("Search for users");

        listView = new ListView<>();
        listView.setPrefHeight(200);

        backButton = new Button("Back");
        backButton.setOnAction(e -> {
            NewsFeedFront newsFeedFront = new NewsFeedFront();
            try {
                newsFeedFront.start(userID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            stage.close();
        });

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(searchField, listView, backButton);


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            List<User> users = friendManager.getUsersByName(newValue,userID);

            // Update the list view
            ObservableList<String> items = listView.getItems();
            items.clear(); // Clear existing items
            if (users != null && !users.isEmpty()) {
                users.forEach(user -> items.add(user.getUsername()));
            } else {
                items.add("No users found");
            }
        });

        // Add selection listener to listView
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("No users found")) {
                // Perform functionality for the selected user
                UserProfilePage userProfilePage = new UserProfilePage();
                try {
                    handleUserSelection(newValue, userID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


        Scene scene = new Scene(root, 1280, 720);

        //scene.getStylesheets().add(getClass().getResource("SearchPage.css").toExternalForm());
        stage.setScene(scene);

        // Handle logout on stage close
        stage.setOnCloseRequest(e -> {
            User user = userDatabase.getUserById(userID);
            if (user != null) {
                LogUser logUser = new LogUser();
                logUser.logout(user.getEmail());
            }
        });

        stage.show();
    }

    private void handleUserSelection(String username, String userID) throws Exception {
        // Example: Open the selected user's profile page
        User selectedUser = userDatabase.getUserByUsername(username);
        if (selectedUser != null) {
            // Example action: Open a user profile page
            UserProfilePage userProfilePage = new UserProfilePage();
            userProfilePage.start(userID, selectedUser.getUserId());
        } else {
            System.out.println("User not found!");
        }
    }
}

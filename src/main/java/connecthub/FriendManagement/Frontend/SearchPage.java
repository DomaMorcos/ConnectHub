package connecthub.FriendManagement.Frontend;

import connecthub.ContentCreation.Backend.ContentDatabase;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.ProfileManagement.Backend.ProfileManager;
import connecthub.UserAccountManagement.Backend.LogUser;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.collections.FXCollections;
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
    private ListView<String> listView; // Changed to match observable list type
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
        backButton.setOnAction(e -> stage.close());

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(searchField, listView, backButton);


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            List<User> users = friendManager.getUsersByName(newValue);

            // Update the list view
            ObservableList<String> items = listView.getItems();
            items.clear(); // Clear existing items
            if (users != null && !users.isEmpty()) {
                users.forEach(user -> items.add(user.getUsername()));
            } else {
                items.add("No users found");
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
}

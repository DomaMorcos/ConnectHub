package connecthub.FriendManagement.Frontend;

import connecthub.AlertUtils;
import connecthub.ContentCreation.Backend.ContentDatabase;
import connecthub.Groups.Backend.Group;
import connecthub.Groups.Backend.GroupDatabase;
import connecthub.Groups.Frontend.GroupPage;
import connecthub.NewsfeedPage.Frontend.NewsFeedFront;
import connecthub.ProfileManagement.Backend.ProfileManager;
import connecthub.ProfileManagement.Frontend.UserProfilePage;
import connecthub.UserAccountManagement.Backend.LogUser;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class SearchGroupPage {
    private TextField searchField;
    private Button backButton;
    private ListView<String> listView;
    private final GroupDatabase groupDatabase = GroupDatabase.getInstance();
    private final ContentDatabase contentDatabase = ContentDatabase.getInstance();
    private final UserDatabase userDatabase = UserDatabase.getInstance();
    private final ProfileManager profileManager = new ProfileManager(contentDatabase, userDatabase);

    public void start(String userID) {
        Stage stage = new Stage();
        stage.setTitle("Search Group Page");

        searchField = new TextField();
        searchField.setPromptText("Search for groups");

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
            List<Group> groups = groupDatabase.getGroupsByName(newValue);
            // Update the list view
            ObservableList<String> items = listView.getItems();
            items.clear(); // Clear existing items
            if (groups != null && !groups.isEmpty()) {
                groups.forEach(group -> items.add(group.getName()));
            } else {
                items.add("No groups found");
            }
        });

        // Add selection listener to listView
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("No groups found")) {
                // Perform functionality for the selected group
                try {
                    handleGroupSelection(newValue, userID);
                    stage.close();
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

    private void handleGroupSelection(String groupName, String userID) throws Exception {
        // Retrieve the group by name
        Group selectedGroup = groupDatabase.getGroupByName(groupName);

        if (selectedGroup == null) {
            // Show an alert if the group is not found
            AlertUtils.showErrorMessage("Group Not Found", "The group \"" + groupName + "\" does not exist.");
            return;
        }

        // Check if the user is already part of the group
        if (selectedGroup.isMember(userID) || selectedGroup.isCreator(userID) || selectedGroup.isAdmin(userID)) {
            // Open the group page if the user has access
            GroupPage groupPage = new GroupPage();
            groupPage.start(userID, selectedGroup.getGroupId());
        } else {
            // Prompt the user to join the group
            Dialog<ButtonType> joinDialog = new Dialog<>();
            joinDialog.setTitle("Join Group");
            joinDialog.setHeaderText("You are not a member of this group. Do you want to send a join request?");

            ButtonType joinButton = new ButtonType("Join");
            ButtonType cancelButton = ButtonType.CANCEL;
            joinDialog.getDialogPane().getButtonTypes().addAll(joinButton, cancelButton);

            // Show the dialog and handle the response
            joinDialog.showAndWait().ifPresent(buttonType -> {
                if (buttonType == joinButton) { // Check the button type
                    try {
                        sendJoinRequest(selectedGroup, userID);
                        AlertUtils.showInformationMessage("Join Group", "Your request to join the group has been sent.");
                    } catch (Exception e) {
                        AlertUtils.showErrorMessage("Join Request Failed", "An error occurred while sending your request.");
                    }
                }
            });
        }
    }

    // Encapsulate join request logic
    private void sendJoinRequest(Group group, String userID) throws Exception {
        if (group != null) {
            group.requestToJoinGroup(group.getGroupId(), userID);
        } else {
            throw new IllegalArgumentException("Group cannot be null.");
        }
    }

}

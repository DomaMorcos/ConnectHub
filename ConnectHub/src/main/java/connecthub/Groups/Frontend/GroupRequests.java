package connecthub.Groups.Frontend;

import connecthub.AlertUtils;
import connecthub.Groups.Backend.Group;
import connecthub.Groups.Backend.GroupDatabase;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GroupRequests {
    GroupDatabase groupDatabase = GroupDatabase.getInstance();
    UserDatabase userDatabase = UserDatabase.getInstance();
    private VBox requestLists;

    public void start(String adminID, String groupID) {
        Stage stage = new Stage();
        VBox requests = createRequests(adminID, groupID, stage);
        Scene scene = new Scene(requests, 400, 600);
        stage.setTitle("Group Join Requests");
        stage.setScene(scene);
        stage.showAndWait();
    }

    public VBox createRequests(String adminID, String groupID, Stage stage) {
        requestLists = new VBox();
        refreshRequests(adminID, groupID, stage);
        return requestLists;
    }

    private void refreshRequests(String adminID, String groupID, Stage stage) {
        // Clear the existing list
        requestLists.getChildren().clear();

        // Get group information
        Group group = groupDatabase.getGroupById(groupID);
        for (String requestingUserID : group.getJoinRequests()) {
            HBox singleRequest = new HBox(10);
            User requestingUser = userDatabase.getUserById(requestingUserID);

            Label username = new Label(requestingUser.getUsername());

            Button acceptButton = new Button("Accept");
            acceptButton.setOnAction(e -> {
                group.approveJoinRequest(groupID, requestingUserID, adminID);
                AlertUtils.showInformationMessage("User Added", requestingUser.getUsername() + " added to the group successfully!");
                refreshRequests(adminID, groupID, stage);
            });

            Button rejectButton = new Button("Reject");
            rejectButton.setOnAction(e -> {
                group.rejectJoinRequest(groupID, requestingUserID, adminID);
                AlertUtils.showInformationMessage("Request Rejected", requestingUser.getUsername() + "'s request has been rejected!");
                refreshRequests(adminID, groupID, stage);
            });

            singleRequest.getChildren().addAll(username, acceptButton, rejectButton);
            requestLists.getChildren().add(singleRequest);
        }

        // Show message if there are no join requests
        if (requestLists.getChildren().isEmpty()) {
            Label noRequestsLabel = new Label("No join requests available.");
            requestLists.getChildren().add(noRequestsLabel);
        }
    }
}

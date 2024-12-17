package connecthub.Groups.Frontend;

import connecthub.Groups.Backend.Group;
import connecthub.Groups.Backend.GroupDatabase;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayPostComments {
    private VBox postsBox;
    private GroupDatabase groupDatabase = GroupDatabase.getInstance();
    public void start(String userID , String groupID, String postID){
        Stage stage = new Stage();
        ScrollPane comments = createPosts(userID,groupID,postID);
    }
    private ScrollPane createPosts(String userID, String groupID,String postID) {

        postsBox = new VBox();


        refreshPosts(userID, groupID,postID);
        // Create a ScrollPane to make posts scrollable
        ScrollPane scrollPane = new ScrollPane(postsBox);
        scrollPane.setFitToWidth(true); // Ensure the ScrollPane stretches to fit the width of the postsBox
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show vertical scrollbar
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Disable horizontal scrollbar
        scrollPane.getStyleClass().add("post-scroll-pane");

        return scrollPane;
    }
    private void refreshPosts(String userID, String groupID, String postID) {
        postsBox.getChildren().clear();
        Group group = groupDatabase.getGroupById(groupID);
        System.out.println(group.getGroupId());
        postsBox.getStyleClass().add("posts-box");

        Label postsLabel = new Label("Recent Posts");
        postsLabel.getStyleClass().add("posts-label");
        postsBox.getChildren().add(postsLabel);
    }

}

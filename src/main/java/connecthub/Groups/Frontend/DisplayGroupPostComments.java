package connecthub.Groups.Frontend;

import connecthub.Groups.Backend.Group;
import connecthub.Groups.Backend.GroupDatabase;
import connecthub.Groups.Backend.GroupPost;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.TimestampFormatter;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class DisplayGroupPostComments {
    UserDatabase userDatabase = UserDatabase.getInstance();
    private VBox commentsBox;
    private GroupDatabase groupDatabase = GroupDatabase.getInstance();
    private ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
    public void start(String userID , String groupID, GroupPost groupPost){
        Stage stage = new Stage();
        ScrollPane comments = createComments(userID,groupID,groupPost);

        Scene scene = new Scene(comments,800,600);
        stage.setScene(scene);
        stage.setTitle("Comments");
        stage.showAndWait();
    }
    private ScrollPane createComments(String userID, String groupID,GroupPost groupPost) {

        commentsBox = new VBox();


        refreshComments(userID, groupID,groupPost);
        // Create a ScrollPane to make posts scrollable
        ScrollPane scrollPane = new ScrollPane(commentsBox);
        scrollPane.setFitToWidth(true); // Ensure the ScrollPane stretches to fit the width of the postsBox
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show vertical scrollbar
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Disable horizontal scrollbar
        scrollPane.getStyleClass().add("post-scroll-pane");

        return scrollPane;
    }
    private void refreshComments(String userID, String groupID, GroupPost groupPost) {
        commentsBox.getChildren().clear();
        Group group = groupDatabase.getGroupById(groupID);
        commentsBox.getStyleClass().add("posts-box");
        Label postsLabel = new Label("Recent Posts");
        postsLabel.getStyleClass().add("posts-label");
        commentsBox.getChildren().add(postsLabel);
        for (GroupPost groupComment : groupPost.getGroupPostComments() ) {
            User commentAuthor = userDatabase.getUserById(groupComment.getAuthorId());
            VBox singleComment = new VBox();
            singleComment.getStyleClass().add("single-post");
            // Author image and username
            File authorImageFile = new File("src/main/resources" + profileDatabase.getProfile(commentAuthor.getUserId()).getProfilePhotoPath());
            ImageView authorImage = new ImageView(new Image(authorImageFile.toURI().toString()));
            authorImage.setFitWidth(35);
            authorImage.setFitHeight(35);
            Label username = new Label(commentAuthor.getUsername());
            username.getStyleClass().add("post-authorname");
            Label time = new Label(TimestampFormatter.formatTimestamp(groupPost.getTimestamp()));
            time.getStyleClass().add("post-time");
            HBox imageAndName = new HBox(authorImage,username);
            imageAndName.getStyleClass().add("image-and-name");

            // Post content (TextArea) with fixed size and scrollable
            TextArea postText = new TextArea(groupComment.getContent());
            postText.getStyleClass().add("post-text");
            postText.setEditable(false);
            postText.setWrapText(true); // Allow text wrapping
            postText.setPrefHeight(50); // Set fixed height
            postText.setPrefWidth(400); // Set fixed width
            postText.setScrollTop(0); // Ensure the content is scrollable

            // Add components to the single post VBox
            singleComment.getChildren().addAll(imageAndName);
            singleComment.getChildren().add(time);
            // Optional post thumbnail image
            if (groupComment.getImagePath() != null && !groupComment.getImagePath().isEmpty()) {
                try {
                    File postImageFile = new File("src/main/resources" + groupComment.getImagePath());
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
                    singleComment.getChildren().add(imageBox);
                } catch (Exception e) {
                    // Log or handle the invalid image path
                    System.err.println("Invalid image path for post: " + groupComment.getImagePath());
                }
            }

            singleComment.getChildren().add(postText);
            // Add the single post to the postsBox
            commentsBox.getChildren().add(singleComment);
        }

    }

}

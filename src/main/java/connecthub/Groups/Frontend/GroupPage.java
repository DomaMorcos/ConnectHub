package connecthub.Groups.Frontend;

import connecthub.AlertUtils;
import connecthub.ContentCreation.Backend.Post;
import connecthub.Groups.Backend.Group;
import connecthub.Groups.Backend.GroupDatabase;
import connecthub.Groups.Backend.GroupPost;
import connecthub.NewsfeedPage.Frontend.NewsFeedFront;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Frontend.ProfilePage;
import connecthub.TimestampFormatter;
import connecthub.UserAccountManagement.Backend.LogUser;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Optional;

public class GroupPage {
    ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
    GroupDatabase groupDatabase = GroupDatabase.getInstance();
    UserDatabase userDatabase = UserDatabase.getInstance();
    private static final String DESTINATION_FOLDER = "src/main/resources/Images/";

    public void start(String userID, String groupID) {
        Group group = groupDatabase.getGroupById(groupID);
        Stage stage = new Stage();
        // Main layout
        BorderPane mainLayout = new BorderPane();

        //Group Info and Options
        VBox groupInfoBox = createGroupInfoBox(userID, groupID);
        HBox optionsBox = createOptionsBox(userID,groupID,stage);
        VBox topSection = new VBox(groupInfoBox,optionsBox);
        mainLayout.setTop(topSection);

        // Members List
        VBox groupMembersList = createGroupMembers(userID, groupID);
        mainLayout.setLeft(groupMembersList);

        // Creator and Admins List
        VBox groupAdminsList = createGroupAdmins(userID,groupID);
        mainLayout.setRight(groupAdminsList);

        // Posts Section
        ScrollPane posts = createPosts(userID,groupID);
        mainLayout.setCenter(posts);
        // Scene setup
        Scene scene = new Scene(mainLayout, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("GroupPage.css").toExternalForm());
        stage.setScene(scene);
        stage.setOnCloseRequest( e -> {
            User user = userDatabase.getUserById(userID);
            LogUser logUser = new LogUser();
            logUser.logout(user.getEmail());
        });
        stage.setTitle("Group Page");
        stage.show();
    }

    public VBox createGroupMembers(String userID, String groupID) {
        Label groupMembers = new Label("Group Members");
        VBox memberList = new VBox(groupMembers);
        Group group = groupDatabase.getGroupById(groupID);
        ArrayList<String> memberIDs = group.getMembersId();
        for (String memberID : memberIDs) {
            User member = userDatabase.getUserById(memberID);
            HBox singleMemberBox = new HBox();
            File memberImageFile = new File("src/main/resources" + profileDatabase.getProfile(member.getUserId()).getProfilePhotoPath());
            ImageView memberImage = new ImageView(new Image(memberImageFile.toURI().toString()));
            memberImage.setFitWidth(35);
            memberImage.setFitHeight(35);
            Label memberName = new Label(member.getUsername());
            singleMemberBox.getChildren().addAll(memberImage, memberName);
            if (group.isAdmin(userID) || group.isCreator(userID)) {
                Button removeMemberButton = new Button("Remove");
                removeMemberButton.setOnAction(e ->{
                    group.removeMember(groupID,memberID,userID);
                });
                // Handle remove click
                singleMemberBox.getChildren().add(removeMemberButton);
            }
            if (group.isCreator(userID)) {
                Button promoteMemberButton = new Button("Promote");
                promoteMemberButton.setOnAction(e -> {
                    group.promoteToAdmin(groupID,memberID,userID);
                });
                singleMemberBox.getChildren().add(promoteMemberButton);
                // Handle promote click
            }
            memberList.getChildren().add(singleMemberBox);
        }
        return memberList;
    }

    public VBox createGroupInfoBox(String userID, String groupID) {
        Group group = groupDatabase.getGroupById(groupID);
        File groupImageFile = new File("src/main/resources" + group.getPhoto());
        ImageView groupImage = new ImageView(new Image(groupImageFile.toURI().toString()));
        groupImage.setFitWidth(35);
        groupImage.setFitHeight(35);

        VBox groupInfoBox = new VBox(groupImage);

        Label groupName = new Label(group.getName());

        TextArea groupDescriptionText = new TextArea(group.getDescription());
        groupDescriptionText.getStyleClass().add("post-text");
        groupDescriptionText.setEditable(false);
        groupDescriptionText.setWrapText(true); // Allow text wrapping
        groupDescriptionText.setPrefHeight(50); // Set fixed height
        groupDescriptionText.setPrefWidth(400); // Set fixed width
        groupDescriptionText.setScrollTop(0); // Ensure the content is scrollable

        groupInfoBox.getChildren().addAll(groupName, groupDescriptionText);
        return groupInfoBox;
    }

    public VBox createGroupAdmins(String userID , String groupID) {
        Label groupCreator = new Label("Creator");
        VBox creatorAndAdmins = new VBox(groupCreator);
        Group group = groupDatabase.getGroupById(groupID);
        User creator = userDatabase.getUserById(group.getCreator());
        HBox singleCreatorBox = new HBox();
        File creatorImageFile = new File("src/main/resources" + profileDatabase.getProfile(creator.getUserId()).getProfilePhotoPath());
        ImageView creatorImage = new ImageView(new Image(creatorImageFile.toURI().toString()));
        creatorImage.setFitWidth(35);
        creatorImage.setFitHeight(35);
        Label groupCreatorName = new Label(creator.getUsername());
        singleCreatorBox.getChildren().addAll(creatorImage,groupCreatorName);
        creatorAndAdmins.getChildren().add(singleCreatorBox);

        Label groupAdmins = new Label("Admins");
        creatorAndAdmins.getChildren().add(groupAdmins);
        ArrayList<String> adminIDs = group.getAdminsId();
        for(String adminID : adminIDs){
            HBox singleAdminBox = new HBox();
            User admin = userDatabase.getUserById(adminID);
            File adminImageFile = new File("src/main/resources" + profileDatabase.getProfile(admin.getUserId()).getProfilePhotoPath());
            ImageView adminImage = new ImageView(new Image(adminImageFile.toURI().toString()));
            adminImage.setFitWidth(35);
            adminImage.setFitHeight(35);
            Label adminName = new Label(admin.getUsername());
            singleAdminBox.getChildren().addAll(adminImage,adminName);
            if(group.isCreator(userID)){
                Button demoteButton = new Button("Demote");
                // handle demoteButton
                demoteButton.setOnAction(e -> {
                    group.demoteToMember(groupID,userID,adminID);
                });
                singleAdminBox.getChildren().add(demoteButton);


            }
            creatorAndAdmins.getChildren().add(singleAdminBox);

        }
        return creatorAndAdmins;
    }
    public HBox createOptionsBox(String userID , String groupID,Stage stage){
        Group group = groupDatabase.getGroupById(groupID);
        HBox optionsBox = new HBox();

        Button newsFeedButton = new Button("NewsFeed");
        newsFeedButton.setOnAction(e->{
            NewsFeedFront newsFeedFront = new NewsFeedFront();
            try {
                stage.close();
                newsFeedFront.start(userID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        });
        optionsBox.getChildren().add(newsFeedButton);

        Button postButton = new Button("Add Post");
        // handle Post Button click

        postButton.setOnAction(e ->{
            AddGroupPost addGroupPost = new AddGroupPost();
            addGroupPost.start(userID,groupID);
            GroupPage groupPage = new GroupPage();
            try {
                stage.close();
                groupPage.start(userID , groupID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });


        Button refreshButton = new Button("Refresh");
        // handle Refresh button click
        refreshButton.setOnAction(e -> {
            GroupPage groupPage = new GroupPage();
            try {
                stage.close();
                groupPage.start(groupID,userID);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        optionsBox.getChildren().addAll(postButton,refreshButton);
        if(!group.isCreator(userID)){
            Button leaveGroupButton = new Button("Leave");
            leaveGroupButton.setOnAction(e ->{
                group.leaveGroup(groupID,userID);
                AlertUtils.showInformationMessage("Group Left","You left the " + group.getName() +" group!");
                NewsFeedFront newsFeedFront = new NewsFeedFront();
                try {
                    stage.close();
                    newsFeedFront.start(userID);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            });
            optionsBox.getChildren().add(leaveGroupButton);
        }
        if(group.isCreator(userID) || group.isAdmin(userID)){
            Button requests = new Button("Requests");
            requests.setOnAction(e -> {
                GroupRequests groupRequests = new GroupRequests();
                groupRequests.start(userID,groupID);
            });
        }
        if(group.isCreator(userID)){
            Button changeGroupImage = new Button("Change Image");
            // handle Change group Image
            changeGroupImage.setOnAction(event -> {
                String imagePath = openImageChooser(stage);
                System.out.println(imagePath);
                if (imagePath != null) {
                    group.setPhoto(imagePath);
                    GroupPage groupPage = new GroupPage();

                    try {
                        stage.close();
                        groupPage.start(userID,groupID);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    // Handle case where no image was selected
                    System.out.println("No image selected for profile photo.");
                }
            });
            Button deleteGroupButton = new Button("Delete Group");
            deleteGroupButton.setOnAction(e -> {
                groupDatabase.removeGroup(groupID);
                AlertUtils.showInformationMessage("Delete Group","Group Deleted!");
                NewsFeedFront newsFeedFront = new NewsFeedFront();
                try {
                    stage.close();
                    newsFeedFront.start(userID);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            optionsBox.getChildren().add(deleteGroupButton);
            optionsBox.getChildren().add(changeGroupImage);
        }

        return optionsBox;
    }

    private ScrollPane createPosts(String userID , String groupID) {
        Group group = groupDatabase.getGroupById(groupID);

        VBox postsBox = new VBox();
        postsBox.getStyleClass().add("posts-box");

        Label postsLabel = new Label("Recent Posts");
        postsLabel.getStyleClass().add("posts-label");
        postsBox.getChildren().add(postsLabel);

        User user = userDatabase.getUserById(userID);

        for (GroupPost post : group.getGroupPosts()) {
            User postAuthor = userDatabase.getUserById(post.getAuthorId());
            VBox singlePost = new VBox();
            singlePost.getStyleClass().add("single-post");
            // Author image and username
            File authorImageFile = new File("src/main/resources" + profileDatabase.getProfile(postAuthor.getUserId()).getProfilePhotoPath());
            ImageView authorImage = new ImageView(new Image(authorImageFile.toURI().toString()));
            authorImage.setFitWidth(35);
            authorImage.setFitHeight(35);
            Label username = new Label(postAuthor.getUsername());
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

            Button editPost = new Button("Edit");
            //handle Edit Post;
            editPost.setOnAction(e -> {
                Optional<String> result = handleEditPost();
                result.ifPresent(newPost -> {
                    group.editPost(groupID,post.getPostId(),post.getAuthorId(), String.valueOf(result));
                });
            });
            singlePost.getChildren().add(editPost);
            if(group.isAdmin(userID) || group.isCreator(userID)){

                Button deletePost = new Button("Delete");
                deletePost.setOnAction(e -> {
                    group.removePost(groupID,post.getPostId(),userID);
                });
                // handle delete Post;

                singlePost.getChildren().add(deletePost);
            }


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
    private Optional<String> handleEditPost() {
        TextInputDialog dialog = new TextInputDialog("Enter New Post");
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit");
        dialog.setContentText("Please enter some text:");
        return dialog.showAndWait();
    }
}

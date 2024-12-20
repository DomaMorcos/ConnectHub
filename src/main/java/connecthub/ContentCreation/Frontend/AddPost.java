package connecthub.ContentCreation.Frontend;

import connecthub.AlertUtils;
import connecthub.ContentCreation.Backend.Content;
import connecthub.ContentCreation.Backend.ContentFactory;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class AddPost {

    // Declare attributes
    private TextArea postTextArea;
    private Button uploadButton, createPostButton;
    private Label imageLabel;
    private Label textLabel;
    private File selectedImage;

    // Folder path inside the `resources` directory where images will be saved
    private static final String DESTINATION_FOLDER = "src/main/resources/Images/";

    // Constructor to initialize the UI
    public void start(String userID) {
        // Initialize components
        Stage stage = new Stage();
        postTextArea = new TextArea();
        postTextArea.setPromptText("Write your Post here...");
        postTextArea.setWrapText(true); // Allow text wrapping
        postTextArea.setPrefHeight(50); // Set fixed height
        postTextArea.setPrefWidth(400); // Set fixed width
        postTextArea.setScrollTop(0); // Ensure the content is scrollable

        textLabel = new Label("Post Text:");
        imageLabel = new Label("Add an Image:");
        uploadButton = new Button("Choose Image");
        createPostButton = new Button("Add Post");

        // Action for the upload button to open FileChooser and choose an image
        uploadButton.setOnAction(e -> openImageChooser(stage));
        createPostButton.setOnAction(e -> {
            handleAddPost(stage,userID);
        });

        // Layout for the page
        VBox layout = new VBox(10);
        layout.getStyleClass().add(".add-box");
        layout.getChildren().addAll(textLabel, postTextArea, imageLabel, uploadButton, createPostButton);

        // Scene setup
        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("AddWindow.css").toExternalForm());
        stage.setTitle("Add a Post");
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void handleAddPost(Stage stage ,String userID) {
        String postText = postTextArea.getText();

        // Set imagePath to an empty string if no image is selected
        String imagePath = selectedImage != null ? "/Images/" + selectedImage.getName() : "";

        if (postText == null || postText.trim().isEmpty()) {
            AlertUtils.showErrorMessage("Empty text", "Please enter text for your post.");
        } else {
            // Create the post content using the content factory
            ContentFactory contentFactory = ContentFactory.getInstance();
            contentFactory.createContent("Post", userID, postText, imagePath);
            AlertUtils.showInformationMessage("Post created", "Post Created Successfully!");
            stage.close();
        }
    }

    private void openImageChooser(Stage primaryStage) {
        // Create a FileChooser to filter image files
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif"));

        // Show the FileChooser dialog
        selectedImage = fileChooser.showOpenDialog(primaryStage);

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

                // Update UI immediately after copying the image
                Platform.runLater(() -> {
                    // Update label to show the image file name
                    imageLabel.setText("Image Added: " + selectedImage.getName());

                    // Load the new image and display it (if you have an ImageView to show it)
                    File copiedImageFile = new File(DESTINATION_FOLDER, selectedImage.getName());
                    System.out.println("File copied successfully to: " + destinationPath.toAbsolutePath());
                });

            } catch (IOException ex) {
                // Show an error message if file copying fails
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to upload image");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        } else {
            // Handle the case when no image is selected
            imageLabel.setText("No image selected.");
        }
    }
}

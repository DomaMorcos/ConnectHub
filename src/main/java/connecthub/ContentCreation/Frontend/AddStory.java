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

public class AddStory {

    // Declare attributes
    private TextArea storyTextArea;
    private Button uploadButton, createStoryButton;
    private Label imageLabel;
    private Label textLabel;
    private File selectedImage;

    // Folder path where the images will be saved (inside your project resources)
    private static final String DESTINATION_FOLDER = "src/main/resources/Images/";

    // Constructor to initialize the UI
    public void start(String userID) {
        // Initialize components
        Stage stage = new Stage();
        storyTextArea = new TextArea();
        storyTextArea.setPromptText("Write your story here...");
        storyTextArea.setPrefHeight(150);

        textLabel = new Label("Story Text:");
        imageLabel = new Label("Add an Image:");
        uploadButton = new Button("Choose Image");
        createStoryButton = new Button("Add Story");

        // Action for the upload button to open FileChooser and choose an image
        uploadButton.setOnAction(e -> openImageChooser(stage));
        createStoryButton.setOnAction(e -> {
            handleAddStory(stage,userID);
            stage.close();
        });

        // Layout for the page
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        layout.getChildren().addAll(textLabel, storyTextArea, imageLabel, uploadButton, createStoryButton);

        // Scene setup
        Scene scene = new Scene(layout, 400, 400);
        stage.setTitle("Add a Story");
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void handleAddStory(Stage stage ,String userID) {
        String storyText = storyTextArea.getText();

        // Set imagePath to an empty string if no image is selected
        String imagePath = selectedImage != null ? "/Images/" + selectedImage.getName() : "";

        if (storyText == null || storyText.trim().isEmpty()) {
            AlertUtils.showErrorMessage("Empty text", "Please enter text for your story.");
        } else {
            // Create the story content using the content factory
            ContentFactory contentFactory = ContentFactory.getInstance();
            contentFactory.createContent("Story", userID, storyText, imagePath);
            AlertUtils.showInformationMessage("Story created", "Story Created Successfully!");
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

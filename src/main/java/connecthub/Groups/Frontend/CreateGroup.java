package connecthub.Groups.Frontend;

import connecthub.AlertUtils;
import connecthub.Groups.Backend.Group;
import connecthub.Groups.Backend.GroupDatabase;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CreateGroup {
    GroupDatabase groupDatabase = GroupDatabase.getInstance();
    private Label groupNameLabel,groupDescriptionLabel,imageLabel;
    private TextField   groupNameTextField,groupDescriptionTextField;
    private Button uploadButton,createGroupButton;
    // Folder path inside the `resources` directory where images will be saved
    private static final String DESTINATION_FOLDER = "src/main/resources/Images/";
    private File selectedImage;
    public void start(String userID){
        Stage stage = new Stage();
        GridPane grid = new GridPane();

        groupNameLabel = new Label("Enter Group Name");
        grid.add(groupNameLabel,0,0);
        groupNameTextField = new TextField();
        grid.add(groupNameTextField,1,0);

        groupDescriptionLabel = new Label("Enter Group Description");
        grid.add(groupDescriptionLabel,0,1);
        groupDescriptionTextField = new TextField();
        grid.add(groupDescriptionTextField,1,1);

        imageLabel = new Label("Add an Image");
        grid.add(imageLabel,0,2);
        uploadButton = new Button("Add Image");
        uploadButton.setOnAction(e -> openImageChooser(stage));
        grid.add(uploadButton,1,2);

        createGroupButton = new Button("Create Group");
        createGroupButton.setOnAction(e -> handleAddGroup(stage,userID));


    }



    public void handleAddGroup(Stage stage,String userID){
        String groupName = groupNameTextField.getText();
        String groupDescription = groupDescriptionTextField.getText();
        String imagePath = selectedImage != null ? "/Images/" + selectedImage.getName() : "/Images/DefaultCoverPhoto.png";
        if (groupName.isEmpty() || groupDescription.isEmpty()) {
            AlertUtils.showErrorMessage("Empty text", "Please fill all the required fields");
        } else {
            Group group = new Group(groupName,groupDescription,imagePath,userID);
            groupDatabase.addGroup(group);
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

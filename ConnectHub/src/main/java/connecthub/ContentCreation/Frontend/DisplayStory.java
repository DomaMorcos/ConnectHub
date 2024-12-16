package connecthub.ContentCreation.Frontend;

import connecthub.ContentCreation.Backend.Story;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class DisplayStory {
    private TextArea storyText;
    private ImageView storyImage;

    public void start(Story story){
        Stage stage = new Stage();
        VBox contentBox = new VBox();

        storyText = new TextArea(story.getContent());
        storyText.setWrapText(true); // Allow text wrapping
        storyText.setPrefHeight(100); // Set fixed height
        storyText.setPrefWidth(300); // Set fixed width
        storyText.setScrollTop(0); // Ensure the content is scrollable
        storyText.setEditable(false);

        // Load the image from the file system using the File API
        File imageFile = new File("src/main/resources" + story.getImagePath());
        Image image = new Image(imageFile.toURI().toString());
        storyImage = new ImageView(image);

// Check the actual width of the image
        if (image.getWidth() > 600) {
            storyImage.setFitWidth(600);
            storyImage.setPreserveRatio(true); // To maintain the aspect ratio
        }

        contentBox.getChildren().addAll(storyText, storyImage);
        Scene scene = new Scene(contentBox, 800, 600);
        scene.getStylesheets().add(getClass().getResource("DisplayStory.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Story");
        stage.showAndWait();
    }
}

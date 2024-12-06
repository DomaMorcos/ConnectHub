package connecthub.ContentCreation.Frontend;

import connecthub.ContentCreation.Backend.Story;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayStory {
    private TextArea storyText;
    private ImageView storyImage;

    public void start(Story story){
        Stage stage = new Stage();
        VBox contentBox = new VBox();

        storyText = new TextArea(story.getContent());
        storyText.setEditable(false);
        storyImage = new ImageView(new Image(getClass().getResource(story.getImagePath()).toExternalForm()));
        if(storyImage.getFitWidth()>600){
            storyImage.setFitWidth(600);
        }
        contentBox.getChildren().addAll(storyText , storyImage);
        Scene scene = new Scene(contentBox,800,600);
        stage.setScene(scene);
        stage.setTitle("Story");
        stage.showAndWait();
    }

}

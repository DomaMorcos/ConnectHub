package connecthub.ProfileManagement.Frontend;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ProfilePage {
    private VBox posts;
    private ImageView profilePhoto, coverPhoto;
    private Label profileName, bio;

    public void start() throws Exception {
        coverPhoto = new ImageView(new Image("DefaultProfilePhoto.jpg"));
        coverPhoto.setFitHeight(200);
        coverPhoto.setFitWidth(800);

        profilePhoto = new ImageView(new Image("DefaultProfilePhoto.jpg"));
        profilePhoto.setFitHeight(150);
        profilePhoto.setFitWidth(150);

    }
}

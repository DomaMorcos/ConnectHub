package connecthub.ProfileManagement.Frontend;

import connecthub.ContentCreation.Backend.Post;
import connecthub.UserAccountManagement.Backend.User;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class PostComponent {
    private Post post;

    public PostComponent(Post post) {
        this.post = post;
    }

    // Generate a single post component
    public Pane getPostComponent(User user) {
        Label username = new Label(user.getUsername());
        Label time = new Label(post.getTimestamp());
        Label contentString = new Label(post.getContent());

        VBox postLayout = new VBox();
        postLayout.setSpacing(5);

        if (!post.getImagePath().isEmpty()) {
            ImageView contentImage = new ImageView(new Image(post.getImagePath()));
            contentImage.setFitWidth(400); // Adjust as needed
            contentImage.setPreserveRatio(true);
            postLayout.getChildren().addAll(username, time, contentString, contentImage);
        } else {
            postLayout.getChildren().addAll(username, time, contentString);
        }

        Pane postPane = new Pane(postLayout);
        return postPane;
    }

    // Static method to generate components for all posts
    public static List<Pane> getAllPostComponents(User user, List<Post> posts) {
        List<Pane> postComponents = new ArrayList<>();
        for (Post post : posts) {
            PostComponent postComponent = new PostComponent(post);
            postComponents.add(postComponent.getPostComponent(user));
        }
        return postComponents;
    }
}

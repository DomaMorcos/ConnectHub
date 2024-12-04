package connecthub.ContentCreation.Backend;

import java.time.LocalDateTime;

import static connecthub.ContentCreation.Backend.ContentDatabase.*;

public class ContentFactory {
    public static Content createContent(String type, String authorId, String content, String imagePath) {
        //make unique id for the content
        int contentId = generateId();
        if (type.equals("Post")) {
            //make a post
            Post post = new Post(contentId, authorId, content, imagePath, LocalDateTime.now().toString());
            //add it th the contents and save
            getContents().add(post);
            saveContents();
            return post;
        } else if (type.equals("Story")) {
            //make a story
            Story story = new Story(contentId, authorId, content, imagePath, LocalDateTime.now().toString());
            //add it th the contents and save
            getContents().add(story);
            saveContents();
            return story;
        }
        return null;
    }
}
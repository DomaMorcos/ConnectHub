package connecthub.ContentCreation.Backend;

import java.time.LocalDateTime;

import static connecthub.ContentCreation.Backend.ContentDatabase.*;

public class ContentFactory {
    public static Content createContent(String type, String authorId, String content, String imagePath) {
        //make unique id for the content
        GetContent getContent = new GetContent();
        String time = LocalDateTime.now().toString();
        String contentId = generateId(authorId);
        if (type.equals("Post")) {
            //make a post
            contentId = "P" + contentId;
            Post post = new Post(contentId, authorId, content, imagePath, time);
            //add it th the contents and save
            getContent.getAllContents().add(post);
            saveContents();
            return post;
        } else if (type.equals("Story")) {
            //make a story
            contentId = "S" + contentId;
            Story story = new Story(contentId, authorId, content, imagePath, time);
            //add it th the contents and save
            getContent.getAllContents().add(story);
            saveContents();
            return story;
        }
        return null;
    }
}
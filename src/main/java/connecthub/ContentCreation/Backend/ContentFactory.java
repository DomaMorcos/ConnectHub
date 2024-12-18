package connecthub.ContentCreation.Backend;

import java.time.LocalDateTime;

import static connecthub.ContentCreation.Backend.ContentDatabase.*;

public class ContentFactory {
    private static ContentFactory contentFactory = null;

    private ContentFactory() {

    }

    public static ContentFactory getInstance() {
        //only one instance
        if (contentFactory == null) {
            contentFactory = new ContentFactory();
        }
        return contentFactory;
    }

    public Content createContent(String type, String authorId, String content, String imagePath) {
        //make unique id for the content
        GetContent getContent = GetContent.getInstance();
        ContentDatabase contentDatabase = ContentDatabase.getInstance();
        String time = LocalDateTime.now().toString();
        String contentId = generateId(authorId);
        if (type.equals("Post")) {
            //make a post
            contentId = "P" + contentId;
            Post post = new Post(contentId, authorId, content, imagePath, time);
            //add it in the contents and save
            contentDatabase.getContents().add(post);
            contentDatabase.saveContents();
            return  post;
        } else if (type.equals("Story")) {
            //make a story
            contentId = "S" + contentId;
            Story story = new Story(contentId, authorId, content, imagePath, time);
            //add it in the contents and save
            contentDatabase.getContents().add(story);
            contentDatabase.saveContents();
            return story;
        } else if(type.equals("Comment")){
            contentId = "C" + contentId;
            Post comment = new Post(contentId, authorId, content, imagePath, time);
            return comment;
        }
        return null;
    }
//    public Post createComment(String authorId, String content, String imagePath){
//        String time = LocalDateTime.now().toString();
//        String contentId = generateId(authorId);
//        contentId = "C" + contentId;
//        Post comment = new Post(contentId, authorId, content, imagePath, time);
//        return comment;
//    }
}
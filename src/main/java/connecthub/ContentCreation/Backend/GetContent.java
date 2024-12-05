package connecthub.ContentCreation.Backend;

import connecthub.UserAccountManagement.Backend.User;

import java.util.ArrayList;
import java.util.Objects;

import static connecthub.ContentCreation.Backend.ContentDatabase.loadContents;

public class GetContent {
    private static GetContent getContent = null;

    private GetContent() {

    }

    public static GetContent getInstance() {
        //only one instance
        if (getContent == null) {
            getContent = new GetContent();
        }
        return getContent;
    }
    public ArrayList<Post> getAllPosts() {
        //load
        ArrayList<Content> c = loadContents();
        //array list empty
        ArrayList<Post> posts = new ArrayList<>();
        assert c != null;
        for (Content content : c) {
            //if the content is post cast and add
            if (content instanceof Post) {
                posts.add((Post) content);
            }
        }
        return posts;
    }

    public ArrayList<Story> getAllStories() {
        //load
        ArrayList<Content> contents = loadContents();
        //array list empty
        ArrayList<Story> stories = new ArrayList<>();
        assert contents != null;
        for (Content content : contents) {
            //if the content is story cast and add
            if (content instanceof Story) {
                stories.add((Story) content);
            }
        }
        return stories;
    }
    public ArrayList<Content> getAllContents() {
        //load
        ArrayList<Content> contents = loadContents();
        //array list empty
        ArrayList<Content> allContents = new ArrayList<>();
        assert contents != null;
        for (Content content : contents) {
            allContents.add(content);
        }
        return allContents;
    }

    public ArrayList<Post> getAllPostsForUser(User user) {
        //load
        ArrayList<Content> contents = loadContents();
        //array list empty
        ArrayList<Post> posts = new ArrayList<>();
        assert contents != null;
        for (Content content : contents) {
            //if the content is post
            if (content instanceof Post) {
                //if the id = id cast and add
                if (Objects.equals(((Post) content).getAuthorId(), user.getUserId()))
                    posts.add((Post) content);
            }
        }
        return posts;
    }

    public ArrayList<Story> getAllStoriesForUser(User user) {
        //load
        ArrayList<Content> contents = loadContents();
        //array list empty
        ArrayList<Story> stories = new ArrayList<>();
        assert contents != null;
        for (Content content : contents) {
            //if the content is story
            if (content instanceof Story) {
                //if the id = id cast and add
                if (Objects.equals(((Story) content).getAuthorId(), user.getUserId()))
                    stories.add((Story) content);
            }
        }
        return stories;
    }
    public ArrayList<Content> getAllContentForUser(User user) {
        //load all contents
        ArrayList<Content> contents = loadContents();
        //array list empty
        ArrayList<Content> contentsForUser = new ArrayList<>();
        assert contents != null;
        //loop on contents
        for (Content content : contents) {
            //if the id = id add

            if (content instanceof Story && Objects.equals(((Story) content).getAuthorId(), user.getUserId())) {
                contentsForUser.add(content);
            } else if (content instanceof Post && Objects.equals(((Post) content).getAuthorId(), user.getUserId())) {

                contentsForUser.add(content);
            }
        }
        return contentsForUser;
    }
}
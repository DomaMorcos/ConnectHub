package connecthub.ContentCreation.Backend;

import connecthub.UserAccountManagement.Backend.User;

import java.util.ArrayList;
import java.util.Objects;

import static connecthub.ContentCreation.Backend.ContentDatabase.loadContents;

public class GetContent {
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
        ArrayList<Content> c = loadContents();
        //array list empty
        ArrayList<Story> stories = new ArrayList<>();
        assert c != null;
        for (Content content : c) {
            //if the content is story cast and add
            if (content instanceof Story) {
                stories.add((Story) content);
            }
        }
        return stories;
    }

    public ArrayList<Post> getAllPostsForUser(User user) {
        //load
        ArrayList<Content> c = loadContents();
        //array list empty
        ArrayList<Post> posts = new ArrayList<>();
        assert c != null;
        for (Content content : c) {
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
        ArrayList<Content> c = loadContents();
        //array list empty
        ArrayList<Story> stories = new ArrayList<>();
        assert c != null;
        for (Content content : c) {
            //if the content is story
            if (content instanceof Story) {
                //if the id = id cast and add
                if (Objects.equals(((Story) content).getAuthorId(), user.getUserId()))
                    stories.add((Story) content);
            }
        }
        return stories;
    }
}

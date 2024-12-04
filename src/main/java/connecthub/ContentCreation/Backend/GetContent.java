package connecthub.ContentCreation.Backend;

import connecthub.UserAccountManagement.Backend.User;

import java.util.ArrayList;
import java.util.Objects;

import static connecthub.ContentCreation.Backend.ContentDatabase.loadContents;

public class GetContent {
    public ArrayList<Post> getAllPosts() {
        ArrayList<Content> c = loadContents();
        ArrayList<Post> posts = new ArrayList<>();
        assert c != null;
        for (Content content : c) {
            if (content instanceof Post) {
                posts.add((Post) content);
            }
        }
        return posts;
    }

    public ArrayList<Story> getAllStories() {
        ArrayList<Content> c = loadContents();
        ArrayList<Story> stories = new ArrayList<>();
        assert c != null;
        for (Content content : c) {
            if (content instanceof Story) {
                stories.add((Story) content);
            }
        }
        return stories;
    }

    public ArrayList<Post> getAllPostsForUser(User user) {
        ArrayList<Content> c = loadContents();
        ArrayList<Post> posts = new ArrayList<>();
        assert c != null;
        for (Content content : c) {
            if (content instanceof Post) {
                if (Objects.equals(((Post) content).getAuthorId(), user.getUserId()))
                    posts.add((Post) content);
            }
        }
        return posts;
    }

    public ArrayList<Story> getAllStoriesForUser(User user) {
        ArrayList<Content> c = loadContents();
        ArrayList<Story> stories = new ArrayList<>();
        assert c != null;
        for (Content content : c) {
            if (content instanceof Story) {
                if (Objects.equals(((Story) content).getAuthorId(), user.getUserId()))
                    stories.add((Story) content);
            }
        }
        return stories;
    }
}

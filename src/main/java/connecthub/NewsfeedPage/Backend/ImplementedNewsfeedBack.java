package connecthub.NewsfeedPage.Backend;

import connecthub.ContentCreation.Backend.Content;
import connecthub.ContentCreation.Backend.GetContent;
import connecthub.ContentCreation.Backend.Post;
import connecthub.ContentCreation.Backend.Story;
import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.UserAccountManagement.Backend.User;

import java.util.ArrayList;
import java.util.List;

public class ImplementedNewsfeedBack implements NewsfeedBack {

    private final GetContent getContent = GetContent.getInstance();
    private final FriendManager friendManager = FriendManager.getInstance();

    public ArrayList<Post> getFriendsPosts(String userId) {
        //empty array to return
        ArrayList<Post> friendsPosts = new ArrayList<>();
        try {
            //get list of friends
            ArrayList<User> friends = friendManager.getFriendsList(userId);
            if (friends == null) {
                return friendsPosts;
            }
            for (User friend : friends) {
                //get all posts for every friend
                ArrayList<Post> posts = getContent.getAllPostsForUser(friend);
                if (posts != null)
                    friendsPosts.addAll(posts);
            }
            return friendsPosts;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return friendsPosts;
    }

    public ArrayList<Story> getFriendsStories(String userId) {
        //empty array to return
        ArrayList<Story> friendsStories = new ArrayList<>();
        try {
            //get list of friends
            ArrayList<User> friends = friendManager.getFriendsList(userId);
            if (friends == null) {
                return friendsStories;
            }
            for (User friend : friends) {
                //get all stories for every friend
                ArrayList<Story> stories = getContent.getAllStoriesForUser(friend);
                if (stories != null)
                    friendsStories.addAll(stories);
            }
            return friendsStories;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return friendsStories;
    }

    @Override
    public ArrayList<Content> getFriendsContents(String userId) {
        //empty array to return
        ArrayList<Content> friendsContents = new ArrayList<>();
        try {
            //get list of friends
            List<User> friends = friendManager.getFriendsList(userId);
            if (friends == null) {
                return friendsContents;
            }
            for (User friend : friends) {
                //get all stories and posts for every friend
                ArrayList<? extends Content> posts = getContent.getAllPostsForUser(friend);
                ArrayList<? extends Content> stories = getContent.getAllStoriesForUser(friend);
                if (posts != null)
                    friendsContents.addAll(posts);
                if (stories != null)
                    friendsContents.addAll(stories);
            }
            return friendsContents;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return friendsContents;
    }

    @Override
    public ArrayList<User> getFriendsList(String userId) {
        return friendManager.getFriendsList(userId);
    }

    @Override
    public ArrayList<User> getFriendSuggestions(String userId) {
        return friendManager.getFriendSuggestions(userId);
    }
}
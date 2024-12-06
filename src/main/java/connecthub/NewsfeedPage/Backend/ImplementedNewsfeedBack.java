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
    private final FriendManager friendManager = connecthub.FriendManagement.Backend.FriendManager.getInstance();

    public ArrayList<Post> getFriendsPosts(String userId) {
        //empty list to fill
        ArrayList<Post> friendsPosts = new ArrayList<>();
        try {
            // get list of friends
            List<User> friends = friendManager.getFriendsList(userId);
            //check if empty
            if (friends.isEmpty()) {
                return friendsPosts;
            }
            //loop on every friend
            for (User friend : friends) {
                //get all posts for this friend
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
        //empty list to fill
        ArrayList<Story> friendsStories = new ArrayList<>();
        try {
            // get list of friends
            List<User> friends = friendManager.getFriendsList(userId);
            //check if empty
            if (friends.isEmpty()) {
                return friendsStories;
            }
            //loop on every friend
            for (User friend : friends) {
                //get all stories for this friend
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
        //empty list to fill
        ArrayList<Content> friendsContents = new ArrayList<>();
        try {
            // get list of friends
            List<User> friends = friendManager.getFriendsList(userId);
            //check if empty
            if (friends.isEmpty()) {
                return friendsContents;
            }
            //loop on every friend
            for (User friend : friends) {
                //get all contents for this friend
                List<Content> contents = getContent.getAllContentForUser(friend);
                if (contents != null)
                    friendsContents.addAll(contents);
            }
            return friendsContents;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return friendsContents;
    }

    @Override
    public List<User> getFriendsList(String userId) {
        return friendManager.getFriendsList(userId);
    }

    @Override
    public List<User> getFriendSuggestions(String userId) {
        return friendManager.suggestFriends(userId);
    }
}
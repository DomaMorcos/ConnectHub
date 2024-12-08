package connecthub.NewsfeedPage.Backend;

import connecthub.ContentCreation.Backend.Content;
import connecthub.ContentCreation.Backend.Post;
import connecthub.ContentCreation.Backend.Story;
import connecthub.UserAccountManagement.Backend.User;

import java.util.ArrayList;
import java.util.List;

public interface NewsfeedBack {
    ArrayList<Content> getFriendsContents(String userId);
    ArrayList<Post> getFriendsPosts(String userId);
    ArrayList<Story> getFriendsStories(String userId);
    List<User> getFriendsList(String userId);
    List<User> getFriendSuggestions(String userId);
}
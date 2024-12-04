package connecthub.NewsfeedPage.Backend;

import connecthub.ContentCreation.Backend.Content;
import connecthub.UserAccountManagement.Backend.User;

import java.util.ArrayList;
import java.util.List;

public interface NewsfeedBack {
    ArrayList<Content> getFriendsContents(String userId);
    ArrayList<User> getFriendsList(String userId);
    ArrayList<User> getFriendSuggestions(String userId);
}
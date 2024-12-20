package connecthub.FriendManagement.Backend;

import connecthub.NotificationSystem.backend.NotificationManager;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Backend.UserProfile;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import org.json.JSONArray;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FriendManager {
    private static FriendManager instance;
    private static  List<FriendRequest> friendRequests;
    private static final String REQUESTS_FILE = "FriendRequests.JSON";
    private static FriendManager friendManager  = null;
    private UserDatabase userDatabase = UserDatabase.getInstance();
    private ProfileDatabase profileDatabase = ProfileDatabase.getInstance();

    private FriendManager() {

        friendRequests = loadFriendRequests();
    }

    public static FriendManager getInstance() {
        if (friendManager == null) {
            friendManager= new FriendManager();
            friendManager.loadFriendRequests();
        }
        return friendManager;
    }

    // Send friend request
    public boolean sendFriendRequest(String senderId, String receiverId) {
        FriendRequest request = new FriendRequest(senderId, receiverId, "Pending");
        friendRequests.add(request);
        NotificationManager notificationManager = NotificationManager.getInstance();
        notificationManager.sendFriendRequestNotification(receiverId,senderId);
        saveFriendRequests();
        return true;
    }

    // Handle friend request response
    public boolean handleFriendRequest(String receiverId, String senderId, boolean accept) {
        Iterator<FriendRequest> iterator = friendRequests.iterator();
        while (iterator.hasNext()) {
            FriendRequest request = iterator.next();
            if (request.getSenderId().equals(senderId) && request.getReceiverId().equals(receiverId)) {
                if (!"Pending".equals(request.getStatus())) {
                    return false; // Prevent redundant actions
                }
                if (accept) {
                    request.setStatus("Accepted");
                    addFriend(senderId, receiverId);
                } else {
                    request.setStatus("Declined");
                }
                iterator.remove();
                saveFriendRequests();
                return true;
            }
        }
        return false;
    }

    // Add friend to both users
    public void addFriend(String userId, String friendId) {
        UserProfile userProfile = profileDatabase.getProfile(userId);
        UserProfile friendProfile = profileDatabase.getProfile(friendId);
        userProfile.addFriend(friendId);
        friendProfile.addFriend(userId);

        // Update Profile Database
        ProfileDatabase profileDb = ProfileDatabase.getInstance();
        profileDb.updateProfile(profileDb.getProfile(userId));
        profileDb.updateProfile(profileDb.getProfile(friendId));
    }

    // Remove friend
    public boolean removeFriend(String userId, String friendId) {
        UserProfile userProfile = profileDatabase.getProfile(userId);
        UserProfile friendProfile = profileDatabase.getProfile(friendId);
        userProfile.deleteFriend(friendId);
        friendProfile.deleteFriend(userId);
        return true;
    }

    // Block friend
    public boolean blockFriend(String userId, String blockedId) {
        UserProfile userProfile = profileDatabase.getProfile(userId);
        UserProfile friendProfile = profileDatabase.getProfile(blockedId);
        userProfile.deleteFriend(blockedId);
        friendProfile.deleteFriend(userId);
        userProfile.blockFriend(blockedId);
        friendProfile.blockFriend(userId);
        // Update Profile Database
        ProfileDatabase profileDb = ProfileDatabase.getInstance();
        profileDb.updateProfile(profileDb.getProfile(userId));
        profileDb.updateProfile(profileDb.getProfile(blockedId));
        return true;
    }

    public boolean blockUser(String userId, String blockedId) {
        UserProfile userProfile = profileDatabase.getProfile(userId);
        UserProfile friendProfile = profileDatabase.getProfile(blockedId);
        userProfile.blockFriend(blockedId);
        friendProfile.blockFriend(userId);
        // Update Profile Database
        ProfileDatabase profileDb = ProfileDatabase.getInstance();
        profileDb.updateProfile(profileDb.getProfile(userId));
        profileDb.updateProfile(profileDb.getProfile(blockedId));
        return true;
    }

    // Get friends list
    public List<User> getFriendsList(String userId) {
        UserProfile userProfile = profileDatabase.getProfile(userId);
        if (userProfile == null) return Collections.emptyList();

        List<String> friendIds = userProfile.getFriends();
        List<User> friends = new ArrayList<>();

        for (String friendId : friendIds) {
            User friend = userDatabase.getUserById(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        // Return the list of friends
        return friends;
    }

    public List<User> getBlockedUsers(String userId) {
        UserProfile userProfile = profileDatabase.getProfile(userId);
        if (userProfile == null) return Collections.emptyList();

        List<String> blockedUsersIds = userProfile.getBlockedUsers();
        List<User> blockedUsers = new ArrayList<>();

        for (String blockedUserId : blockedUsersIds) {
            User blockedUser = userDatabase.getUserById(blockedUserId);
            if (blockedUser != null) {
                blockedUsers.add(blockedUser);
            }
        }
        // Return the list of blocked users
        return blockedUsers;
    }


    // Get pending requests for a user
    public static List<FriendRequest> getPendingRequests(String userId) {
        List<FriendRequest> pendingRequests = new ArrayList<>();
        for (FriendRequest request : friendRequests) {
            if (request.getReceiverId().equals(userId) && "Pending".equals(request.getStatus())) {
                pendingRequests.add(request);
            }
        }
        return pendingRequests;
    }
    // Get all users with the same name
    public List<User> getUsersByName(String name,String userID) {
        UserDatabase userDb = UserDatabase.getInstance();
        User me = userDb.getUserById(userID);
        List<User> allUsers = userDb.users;
        List<User> filteredArray = new ArrayList<>();
        for (User user: allUsers) {
            if (!getBlockedUsers(userID).contains(user) && !user.equals(me) ){
                filteredArray.add(user);
            }
        }
        List<User> matchedUsers = new ArrayList<>();
        String lowerCaseName = name.toLowerCase(); // Normalize the search term to lowercase
        for (User user : filteredArray) {
            if (user.getUsername().toLowerCase().contains(lowerCaseName)) { // Normalize usernames and check
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }


    // Suggest friends
    public List<User> suggestFriends(String userId) {
        UserDatabase userDb = UserDatabase.getInstance();
        UserProfile userProfile = profileDatabase.getProfile(userId);

        List<String> existingFriends = userProfile.getFriends();
        List<User> allUsers = new ArrayList<>(userDb.users);
        allUsers.removeIf(user -> user.getUserId().equals(userId) ||
                existingFriends.contains(user.getUserId()) ||
                getBlockedUsers(userId).stream().anyMatch(receiver -> receiver.getUserId().equals(user.getUserId())) ||
                getAllPendingSenders(userId).stream().anyMatch(receiver -> receiver.getUserId().equals(user.getUserId())) ||
                getAllReceivers().stream().anyMatch(receiver -> receiver.getUserId().equals(user.getUserId())));
        return allUsers;
    }

    public List<User> getAllReceivers(){
        List<User> receivers = new ArrayList<User>();
        for(FriendRequest friendRequest : friendRequests) {
            User receiver = userDatabase.getUserById(friendRequest.getReceiverId()) ;
            receivers.add(receiver);
        }
        return receivers;
    }

    public List<User> getAllPendingSenders(String userId){
        List<User> pendingSenders = new ArrayList<User>();
        for(FriendRequest friendRequest : friendRequests) {
            if (friendRequest.getReceiverId().equals(userId)) {
                User sender = userDatabase.getUserById(friendRequest.getSenderId());
                pendingSenders.add(sender);
            }
        }
        return pendingSenders;
        }

    // Save friend requests to JSON
    private static void saveFriendRequests() {
        JSONArray jsonArray = new JSONArray();
        for (FriendRequest request : friendRequests) {
            if ("Pending".equals(request.getStatus())) {
                jsonArray.put(request.toJson());
            }
        }

        try (FileWriter file = new FileWriter(REQUESTS_FILE)) {
            file.write(jsonArray.toString(4));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load friend requests from JSON
    private List<FriendRequest> loadFriendRequests() {
        List<FriendRequest> requests = new ArrayList<>();
        try {
            String json = new String(Files.readAllBytes(Paths.get(REQUESTS_FILE)));
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                requests.add(FriendRequest.fromJson(jsonArray.getJSONObject(i)));
            }
        } catch (IOException e) {
            System.out.println("FriendRequests.json not found, starting with empty list.");
        }
        return requests;
    }
}
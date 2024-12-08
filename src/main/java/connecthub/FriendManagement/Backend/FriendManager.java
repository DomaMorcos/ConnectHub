package connecthub.FriendManagement.Backend;

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
    private  Map<String, List<String>> friendsMap; // userId -> list of friends
    private static  List<FriendRequest> friendRequests;
    private static final String REQUESTS_FILE = "FriendRequests.JSON";
    private static FriendManager friendManager  = null;
    private UserDatabase userDatabase = UserDatabase.getInstance();

    private FriendManager() {
        friendsMap = new HashMap<>();
        friendRequests = loadFriendRequests();
    }

    public static FriendManager getInstance() {
        if (friendManager == null) {
            friendManager= new FriendManager();
            friendManager.loadFriendRequests();
        }
        return friendManager;
    }
    // Initialize friends for a new user
    public void initializeFriends(String userId, List<String> friends) {
        friendsMap.put(userId, friends != null ? new ArrayList<>(friends) : new ArrayList<>());
    }

    // Send friend request
    public static boolean sendFriendRequest(String senderId, String receiverId) {
        FriendRequest request = new FriendRequest(senderId, receiverId, "Pending");
        friendRequests.add(request);
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
                return accept;
            }
        }
        return false;
    }

    // Add friend to both users
    public void addFriend(String userId, String friendId) {
        friendsMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(friendId);
        friendsMap.computeIfAbsent(friendId, k -> new ArrayList<>()).add(userId);

        // Update Profile Database
        ProfileDatabase profileDb = ProfileDatabase.getInstance();
        profileDb.updateProfile(profileDb.getProfile(userId));
        profileDb.updateProfile(profileDb.getProfile(friendId));
    }
//    public void addFriend(String userId, String friendId) {
//        ProfileDatabase profileDb = ProfileDatabase.getInstance();
//
//        // Get profiles of both users
//        UserProfile userProfile = profileDb.getProfile(userId);
//        UserProfile friendProfile = profileDb.getProfile(friendId);
//
//        // Add each other to the friends list
//        if (userProfile != null && friendProfile != null) {
//            userProfile.addFriend(friendId); // Add friend to user
//            friendProfile.addFriend(userId); // Add user to friend's list
//
//            // Save the updated profiles to the JSON file
//            profileDb.updateProfile(userProfile);
//            profileDb.updateProfile(friendProfile);
//        } else {
//            System.out.println("Profiles not found for either user.");
//        }
//    }



    // Remove friend
    public boolean removeFriend(String userId, String friendId) {
        friendsMap.getOrDefault(userId, new ArrayList<>()).remove(friendId);
        friendsMap.getOrDefault(friendId, new ArrayList<>()).remove(userId);
        return true;
    }

    // Block friend
    public boolean blockFriend(String userId, String blockedId) {
        removeFriend(userId, blockedId); // Remove friendship
        return true;
    }

    // Get friends list
    public List<User> getFriendsList(String userId) {
        UserDatabase userDb = UserDatabase.getInstance();
        List<String> friendIds = friendsMap.getOrDefault(userId, new ArrayList<>());
        List<User> friends = new ArrayList<>();
        for (String friendId : friendIds) {
            User user = userDb.getUserById(friendId);
            if (user != null) {
                friends.add(user);
            }
        }
        return friends;
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
    public List<User> getUsersByName(String name) {
        UserDatabase userDb = UserDatabase.getInstance();
        List<User> matchedUsers = new ArrayList<>();
        for (User user : userDb.users) {
            if (user.getUsername().equalsIgnoreCase(name)) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }

    // Suggest friends
    public List<User> suggestFriends(String userId) {
        UserDatabase userDb = UserDatabase.getInstance();
        List<String> existingFriends = friendsMap.getOrDefault(userId, new ArrayList<>());
        List<User> allUsers = new ArrayList<>(userDb.users);
        allUsers.removeIf(user -> user.getUserId().equals(userId) ||
                existingFriends.contains(user.getUserId()) ||
                getAllReceivers().stream().anyMatch(receiver -> receiver.getUserId().equals(user.getUserId())));

        // Sort by mutual friends count
        allUsers.sort((user1, user2) -> {
            int mutual1 = countMutualFriends(userId, user1.getUserId());
            int mutual2 = countMutualFriends(userId, user2.getUserId());
            return Integer.compare(mutual2, mutual1); // Descending order
        });

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

    public int countMutualFriends(String userId1, String userId2) {
        List<String> friends1 = friendsMap.getOrDefault(userId1, new ArrayList<>());
        List<String> friends2 = friendsMap.getOrDefault(userId2, new ArrayList<>());
        friends1.retainAll(friends2); // Get intersection of both friend lists
        return friends1.size();
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
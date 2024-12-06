package connecthub.FriendManagement.Backend;

import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Backend.UserProfile;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;

import java.util.*;
import java.util.stream.Collectors;

public class FriendManager {
    private static FriendManager instance;
    private final Map<String, List<String>> friendsMap = new HashMap<>();
    private final Map<String, List<FriendRequest>> friendRequestsMap = new HashMap<>(); // Track pending requests
    private final Map<String, List<String>> blockedMap = new HashMap<>(); // Blocked users map
    private static FriendManager friendManager = null;
    private UserDatabase userDatabase = UserDatabase.getInstance();
    private FriendManager() {

    }

    public static FriendManager getInstance() {
        if (friendManager == null) {
            friendManager = new FriendManager();
        }
        return friendManager;
    }

    public void initializeFriends(String userId, List<String> friends) {
        if (!friendsMap.containsKey(userId)) {
            friendsMap.put(userId, friends != null ? new ArrayList<>(friends) : new ArrayList<>());
        }
    }

    public List<User> getFriendsList(String userId) {
        UserDatabase userDatabase = UserDatabase.getInstance();

        // Ensure friendsMap is initialized for the given user
        if (!friendsMap.containsKey(userId)) {
            friendsMap.put(userId, new ArrayList<>());
        }

        // Retrieve the friends list, excluding blocked users
        List<String> friendsList = friendsMap.get(userId);
        List<User> filteredFriends = new ArrayList<>();

        for (String friendId : friendsList) {
            if (!isBlocked(userId, friendId)) {
                User friend = userDatabase.getUserById(friendId);
                if (friend != null) {
                    filteredFriends.add(friend);
                }
            }
        }
        return filteredFriends;
    }

    public List<User> suggestFriends(String userId) {
        UserDatabase userDatabase = UserDatabase.getInstance();

        // Initialize friendsMap for the given user if necessary
        if (!friendsMap.containsKey(userId)) {
            friendsMap.put(userId, new ArrayList<>());
        }

        List<User> suggestions = new ArrayList<>();
        List<User> userFriends = getFriendsList(userId);
        Set<String> userFriendIds = userFriends.stream().map(User::getUserId).collect(Collectors.toSet());

        for (String potentialFriendId : friendsMap.keySet()) {
            if (!userFriendIds.contains(potentialFriendId) &&
                    !potentialFriendId.equals(userId) &&
                    !isBlocked(userId, potentialFriendId)) {

                User potentialFriend = userDatabase.getUserById(potentialFriendId);
                if (potentialFriend != null) {
                    suggestions.add(potentialFriend);
                }
            }
        }
        return suggestions;
    }
    private boolean isFriend(String userId, String friendId) {
        return friendsMap.getOrDefault(userId, new ArrayList<>()).contains(friendId);
    }

//
//    // Add a friend for a user (mutual friendship)
//    public boolean addFriend(String userId, String friendId) {
//        // Check if the user is trying to add themselves as a friend
//        if (userId.equals(friendId)) {
//            return false;
//        }
//
//        // Check if the user is blocked by the other user
//        if (isBlocked(userId, friendId)) {
//            return false;
//        }
//
//        // Check if they are already friends
//        if (isFriend(userId, friendId)) {
//           return false;
//        }
//
//        // Add the friend to both users' friend lists (mutual friendship)
//        friendsMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(friendId);
//        friendsMap.computeIfAbsent(friendId, k -> new ArrayList<>()).add(userId);
//
//        // Save the updated state (optional)
//        FriendRequest.saveRequestsToJson();
//        return true;
//    }
public boolean addFriend(String userId, String friendId) {
    System.out.println("Adding friend: " + userId + " and " + friendId);

    // Add to FriendManager
    friendsMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(friendId);
    friendsMap.computeIfAbsent(friendId, k -> new ArrayList<>()).add(userId);

    // Update ProfileDatabase
    ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
    UserProfile userProfile = profileDatabase.getProfile(userId);
    UserProfile friendProfile = profileDatabase.getProfile(friendId);

    if (userProfile != null && friendProfile != null) {
        userProfile.getFriends().add(userDatabase.getUser(friendId));
        friendProfile.getFriends().add( userDatabase.getUser(userId));

        System.out.println("Updating profile for user: " + userId + " and " + friendId);
        profileDatabase.updateProfile(userProfile);
        profileDatabase.updateProfile(friendProfile);
        profileDatabase.saveProfilesToJsonFile(); // Persist the changes
    } else {
        System.out.println("Profile not found for one of the users!");
        return false;
    }

    return true;
}
    //Remove a friend from the user's friend list
    public boolean removeFriend(String userId, String friendId) {
        // Ensure both users are friends before removing them
        if (!friendsMap.containsKey(userId) || !friendsMap.containsKey(friendId)) {
            return false;
        }
        // Check if users are friends before attempting to remove
        List<String> userFriends = friendsMap.get(userId);
        List<String> friendFriends = friendsMap.get(friendId);

        if (!userFriends.contains(friendId)) {
            return false;
        }

        // Remove the friendship in both directions
        userFriends.remove(friendId);
        friendFriends.remove(userId);

        FriendRequest.saveRequestsToJson(); // Save the updated state
        return true;
    }


    // Send a friend request (mark as Pending)
    public boolean sendFriendRequest(String senderId, String receiverId) {
        if (getFriendsList(senderId).contains(receiverId)) {
            return false;
        }
        FriendRequest request = new FriendRequest(senderId, receiverId, "Pending");
        friendRequestsMap.computeIfAbsent(receiverId, k -> new ArrayList<>()).add(request);
        return true;
    }

    // Accept or decline friend requests
    public void respondToRequest(String receiverId, String senderId, boolean accept) {
        List<FriendRequest> requests = friendRequestsMap.getOrDefault(receiverId, new ArrayList<>());
        for (FriendRequest request : requests) {
            if (request.getSenderId().equals(senderId) && request.getStatus().equals("Pending")) {
                request.setStatus(accept ? "Accepted" : "Declined");
                if (accept) {
                    addFriend(receiverId, senderId);
                }
                break;
            }
        }
    }

    // Block a user
    public boolean blockUser(String userId, String blockId) {
        // Prevent blocking oneself
        if (userId.equals(blockId)) {
           return false;
        }

        // Block the user
        if (!blockedMap.containsKey(userId)) {
            blockedMap.put(userId, new ArrayList<>());
        }
        List<String> blockedUsers = blockedMap.get(userId);

        if (blockedUsers.contains(blockId)) {
            return false;
        }

        blockedUsers.add(blockId);

        // Remove the blocked user from the friends list
        List<String> userFriends = friendsMap.getOrDefault(userId, new ArrayList<>());
        userFriends.remove(blockId);

        List<String> friendFriends = friendsMap.getOrDefault(blockId, new ArrayList<>());
        friendFriends.remove(userId);

        FriendRequest.saveRequestsToJson(); // Save the updated data
        return true;
    }


    // Check if a user is blocked by another user
    public boolean isBlocked(String userId, String friendId) {
        return blockedMap.getOrDefault(userId, new ArrayList<>()).contains(friendId);
    }

    // Remove a user from the blocked list
    public boolean unblockUser(String userId, String unblockId) {
        if (blockedMap.containsKey(userId)) {
            blockedMap.get(userId).remove(unblockId);
            return true;
        }
        return false;
    }
//    // Display the online/offline status of friends
    public List<FriendStatus> getFriendStatuses(String userId) {
        List<FriendStatus> friendStatuses = new ArrayList<>();
        List<String> userFriends = friendsMap.getOrDefault(userId, new ArrayList<>());

        if (userFriends.isEmpty()) {
            return friendStatuses; // Return an empty list if no friends
        }

        for (String friendId : userFriends) {
            User friend = UserDatabase.getInstance().getUserById(friendId);
            if (friend != null) {
                String status = friend.getStatus(); // Retrieve the status of the friend
                friendStatuses.add(new FriendStatus(friend, status));
            }
        }
        return friendStatuses;
    }

}

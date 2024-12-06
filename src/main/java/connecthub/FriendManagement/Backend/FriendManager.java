package connecthub.FriendManagement.Backend;

import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Backend.UserProfile;
import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;

import java.util.*;
import java.util.stream.Collectors;

import static connecthub.FriendManagement.Backend.FriendRequest.saveRequestsToJson;

public class FriendManager {
    private static FriendManager instance;
    private final Map<String, List<String>> friendsMap = new HashMap<>();
    private static final Map<String, List<FriendRequest>> friendRequestsMap = new HashMap<>(); // Track pending requests
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

    // Add a friend for a user (mutual friendship)
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
    // Add a friend for a user (mutual friendship)
    public boolean addFriend(String userId, String friendId) {
        // Validate inputs
        if (userId == null || friendId == null || userId.isEmpty() || friendId.isEmpty()) {
            System.err.println("Invalid user IDs provided.");
            return false;
        }

        // Check if the user is trying to add themselves as a friend
        if (userId.equals(friendId)) {
            System.err.println("A user cannot add themselves as a friend.");
            return false;
        }

        // Check if the user is blocked by the other user
        if (isBlocked(userId, friendId)) {
            System.err.println("Cannot add friend. User " + userId + " is blocked by " + friendId);
            return false;
        }

        // Check if they are already friends
        if (isFriend(userId, friendId)) {
            System.err.println("Users " + userId + " and " + friendId + " are already friends.");
            return false;
        }

        // Add the friend to both users' friend lists (mutual friendship)
        friendsMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(friendId);
        friendsMap.computeIfAbsent(friendId, k -> new ArrayList<>()).add(userId);

        // Log the action
        System.out.println("Friendship established between User " + userId + " and User " + friendId);

        // Save the updated state
        saveRequestsToJson();
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

        saveRequestsToJson(); // Save the updated state
        return true;
    }


    // Send a friend request (mark as Pending)
    public static boolean sendFriendRequest(String senderId, String receiverId) {
        // Prevent sending requests to oneself or to an existing friend
        if (senderId.equals(receiverId) || FriendManager.getInstance().getFriendsList(senderId).contains(receiverId)) {
            return false;
        }

        // Check if a pending request already exists
        if (friendRequestsMap.getOrDefault(receiverId, Collections.emptyList()).stream()
                .anyMatch(req -> req.getSenderId().equals(senderId) && req.getStatus().equals("Pending"))) {
            return false;
        }

        // Create a new pending friend request
        FriendRequest request = new FriendRequest(senderId, receiverId, "Pending");
        friendRequestsMap.computeIfAbsent(receiverId, k -> new ArrayList<>()).add(request);

        // Save the updated requests map to the JSON file
        saveRequestsToJson();

        return true;
    }

    // Accept or decline friend requests
    public boolean respondToRequest(String receiverId, String senderId, boolean accept) {
        List<FriendRequest> requests = friendRequestsMap.getOrDefault(receiverId, new ArrayList<>());
        for (FriendRequest request : requests) {
            if (request.getSenderId().equals(senderId) && request.getStatus().equals("Pending")) {
                request.setStatus(accept ? "Accepted" : "Declined");
                if (accept) {
                    addFriend(receiverId, senderId);
                    return true;
                }

            }
        }
        return false;
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

        saveRequestsToJson(); // Save the updated data
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
    public List<User> searchUsersByName(String name) {
        if (name == null || name.isEmpty()) {
            System.err.println("Invalid name provided.");
            return new ArrayList<>();
        }

        // Get the UserDatabase instance
        UserDatabase userDatabase = UserDatabase.getInstance();
        List<User> matchingUsers = new ArrayList<>();

        // Perform a case-insensitive search through all users
        for (User user : userDatabase.users) {
            if (user.getUsername().toLowerCase().contains(name.toLowerCase())) {
                matchingUsers.add(user);
            }
        }

        // Log the result
        if (matchingUsers.isEmpty()) {
            System.out.println("No users found matching the name: " + name);
        } else {
            System.out.println("Users matching the name '" + name + "':");
            for (User user : matchingUsers) {
                System.out.println(user);
            }
        }

        return matchingUsers;
    }

}
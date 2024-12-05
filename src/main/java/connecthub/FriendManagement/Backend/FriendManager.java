package connecthub.FriendManagement.Backend;

import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;

import java.util.*;
import java.util.stream.Collectors;

public class FriendManager {
    private static FriendManager instance;
    private final Map<String, List<String>> friendsMap = new HashMap<>();
    private final Map<String, List<FriendRequest>> friendRequestsMap = new HashMap<>(); // Track pending requests
    private final Map<String, List<String>> blockedMap = new HashMap<>(); // Blocked users map

    private FriendManager() {}

    public static synchronized FriendManager getInstance() {
        if (instance == null) {
            instance = new FriendManager();
        }
        return instance;
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



    // Add a friend for a user (mutual friendship)
    public void addFriend(String userId, String friendId) {
        if (isBlocked(userId, friendId)) {
            System.out.println("Cannot add friend, user is blocked.");
            return;
        }
        friendsMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(friendId);
        friendsMap.computeIfAbsent(friendId, k -> new ArrayList<>()).add(userId); // Mutual friendship
    }

    // Remove a friend from the user's friend list
    public void removeFriend(String userId, String friendId) {
        List<String> userFriends = friendsMap.get(userId);
        List<String> friendFriends = friendsMap.get(friendId);

        // Ensure that both user and friend have each other in their lists
        if (userFriends != null) {
            userFriends.remove(friendId);
        }

        if (friendFriends != null) {
            friendFriends.remove(userId);
        }
    }

    // Send a friend request (mark as Pending)
    public void sendFriendRequest(String senderId, String receiverId) {
        if (getFriendsList(senderId).contains(receiverId)) {
            System.out.println("You are already friends.");
            return;
        }
        FriendRequest request = new FriendRequest(senderId, receiverId, "Pending");
        friendRequestsMap.computeIfAbsent(receiverId, k -> new ArrayList<>()).add(request);
        System.out.println("Friend request sent from " + senderId + " to " + receiverId);
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
    public void blockUser(String userId, String blockId) {
        if (!blockedMap.containsKey(userId)) {
            blockedMap.put(userId, new ArrayList<>());
        }
        blockedMap.get(userId).add(blockId);
        System.out.println("User " + blockId + " is now blocked by " + userId);
    }

    // Check if a user is blocked by another user
    public boolean isBlocked(String userId, String friendId) {
        return blockedMap.getOrDefault(userId, new ArrayList<>()).contains(friendId);
    }

    // Remove a user from the blocked list
    public void unblockUser(String userId, String unblockId) {
        if (blockedMap.containsKey(userId)) {
            blockedMap.get(userId).remove(unblockId);
            System.out.println("User " + unblockId + " is unblocked by " + userId);
        }
    }

    // Display the online/offline status of friends
    public List<FriendStatus> getFriendStatuses(String userId) {
        List<FriendStatus> friendStatuses = new ArrayList<>();
        for (User friendId : getFriendsList(userId)) {
            friendStatuses.add(new FriendStatus(friendId, "online")); // Example status, should come from user status management
        }
        return friendStatuses;
    }
}

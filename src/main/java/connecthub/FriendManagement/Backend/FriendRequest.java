package connecthub.FriendManagement.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class FriendRequest {
    private static final String FRIEND_REQUESTS_FILE = "FriendRequests.JSON";
    private static Map<String, List<FriendRequest>> friendRequestsMap = new HashMap<>();

    private final String senderId;
    private final String receiverId;
    private String status; // "Pending", "Accepted", "Declined"

    public FriendRequest(String senderId, String receiverId, String status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }

    // Getters and Setters
    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // JSON Serialization
    public JSONObject toJson() {
        return new JSONObject()
                .put("senderId", senderId)
                .put("receiverId", receiverId)
                .put("status", status);
    }

    public static FriendRequest fromJson(JSONObject jsonObject) {
        return new FriendRequest(
                jsonObject.getString("senderId"),
                jsonObject.getString("receiverId"),
                jsonObject.getString("status")
        );
    }

    // Send a friend request
    public static boolean sendFriendRequest(String senderId, String receiverId) {
        // Prevent a user from sending a friend request to themselves
        if (senderId.equals(receiverId)) {
        return false;
        }

        // Check if there is already a pending request from sender to receiver
        if (friendRequestsMap.getOrDefault(receiverId, Collections.emptyList()).stream()
                .anyMatch(req -> req.getSenderId().equals(senderId) && req.getStatus().equals("Pending"))) {
            return false;
        }

        // Create and add the request to the receiver's list
        FriendRequest request = new FriendRequest(senderId, receiverId, "Pending");
        friendRequestsMap.computeIfAbsent(receiverId, k -> new ArrayList<>()).add(request);
        saveRequestsToJson();  // Save the state after adding the request
        return true;
    }

    // Responding to a friend request
    public static boolean respondToRequest(String receiverId, String senderId, boolean accept) {
        List<FriendRequest> requests = friendRequestsMap.getOrDefault(receiverId, Collections.emptyList());
        Optional<FriendRequest> optionalRequest = requests.stream()
                .filter(req -> req.getSenderId().equals(senderId) && req.getStatus().equals("Pending"))
                .findFirst();

        if (optionalRequest.isEmpty()) {
            return false;
        }

        FriendRequest request = optionalRequest.get();
        request.setStatus(accept ? "Accepted" : "Declined");

        if (!accept) {
            requests.remove(request); // Remove declined request
        }

        saveRequestsToJson();
        return accept;
    }

    // Retrieve pending friend requests for a user
    public static List<FriendRequest> getPendingRequests(String userId) {
        return friendRequestsMap.getOrDefault(userId, Collections.emptyList())
                .stream()
                .filter(req -> req.getStatus().equals("Pending"))
                .collect(Collectors.toList());
    }

    // Save only pending friend requests to the JSON file
    public static void saveRequestsToJson() {
        JSONObject data = new JSONObject();

        // Save only pending requests to friendRequestsMap
        Map<String, List<FriendRequest>> pendingRequests = new HashMap<>();
        for (Map.Entry<String, List<FriendRequest>> entry : friendRequestsMap.entrySet()) {
            List<FriendRequest> pendingList = entry.getValue().stream()
                    .filter(request -> request.getStatus().equals("Pending"))
                    .collect(Collectors.toList());

            if (!pendingList.isEmpty()) {
                pendingRequests.put(entry.getKey(), pendingList);
            }
        }
        data.put("friendRequestsMap", requestsToJson(pendingRequests));

        try (FileWriter file = new FileWriter(FRIEND_REQUESTS_FILE)) {
            file.write(data.toString(4));
        } catch (IOException e) {
            System.err.println("Error saving friend requests: " + e.getMessage());
        }
    }

    // Load only pending requests from the JSON file
    public static void loadRequestsFromJson() {
        File file = new File(FRIEND_REQUESTS_FILE);
        if (!file.exists()) {
            System.out.println("No previous data found. Creating new data.");
            saveRequestsToJson(); // Create a new file if it doesn't exist
            return;
        }

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            JSONObject data = new JSONObject(content);

            // Clear previous data
            friendRequestsMap.clear();
            jsonToRequests(data.getJSONObject("friendRequestsMap"), friendRequestsMap);

        } catch (IOException e) {
            System.err.println("Error loading friend requests: " + e.getMessage());
        }
    }


    // Helper: Convert Map<String, List<FriendRequest>> to JSON
    private static JSONObject requestsToJson(Map<String, List<FriendRequest>> map) {
        JSONObject json = new JSONObject();
        map.forEach((key, list) -> {
            JSONArray array = new JSONArray();
            list.forEach(req -> array.put(req.toJson()));
            json.put(key, array);
        });
        return json;
    }

    // Helper: Populate Map<String, List<FriendRequest>> from JSON
    private static void jsonToRequests(JSONObject jsonObject, Map<String, List<FriendRequest>> map) {
        jsonObject.keySet().forEach(key -> {
            JSONArray array = jsonObject.getJSONArray(key);
            List<FriendRequest> list = array.toList().stream()
                    .map(obj -> FriendRequest.fromJson(new JSONObject((Map<?, ?>) obj)))
                    .collect(Collectors.toList());
            map.put(key, list);
        });
    }
    @Override
    public String toString() {
        return "FriendRequest{senderId='" + senderId + "', receiverId='" + receiverId + "', status='" + status + "'}";
    }

}
package connecthub.FriendManagement.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FriendRequest {
    private String senderId;
    private String receiverId;
    private String status; // Possible values: "Pending", "Accepted", "Declined"
    private static final String FRIEND_FILEPATH = "Friend.JSON";
    // In-memory storage for requests and blocked users
    private static Map<String, List<FriendRequest>> friendRequestsMap = new HashMap<>();
    private static Map<String, List<String>> blockedMap = new HashMap<>();
    private static FriendManager friendManager = FriendManager.getInstance();

    public FriendRequest(String senderId, String receiverId, String status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }

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

    @Override
    public String toString() {
        return "FriendRequest{senderId='%s', receiverId='%s', status='%s'}"
                .formatted(senderId, receiverId, status);
    }

    // Static methods for saving and loading JSON data
    public static void saveDataToJson() {
        JSONObject data = new JSONObject();
        data.put("friendRequestsMap", requestsToJson(friendRequestsMap));
        data.put("blockedMap", mapToJson(blockedMap));

        try (FileWriter file = new FileWriter(FRIEND_FILEPATH)) {
            file.write(data.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadDataFromJson() {
        File file = new File(FRIEND_FILEPATH);
        if (!file.exists()) {
            saveDataToJson();
            return;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(FRIEND_FILEPATH)));
            JSONObject data = new JSONObject(content);

            friendRequestsMap.clear();
            blockedMap.clear();

            jsonToRequests(data.getJSONObject("friendRequestsMap"), friendRequestsMap);
            jsonToMap(data.getJSONObject("blockedMap"), blockedMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send a friend request (mark as Pending)
    public void sendFriendRequest(String senderId, String receiverId) {
        if (friendManager.getFriendsList(senderId).contains(receiverId)) {
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
                    friendManager.addFriend(receiverId, senderId);
                }
                break;
            }
        }
    }

    // JSON conversion helpers
    private static JSONObject requestsToJson(Map<String, List<FriendRequest>> map) {
        JSONObject jsonObject = new JSONObject();
        map.forEach((key, requests) -> {
            JSONArray array = new JSONArray();
            for (FriendRequest req : requests) {
                JSONObject reqObj = new JSONObject();
                reqObj.put("senderId", req.getSenderId());
                reqObj.put("receiverId", req.getReceiverId());
                reqObj.put("status", req.getStatus());
                array.put(reqObj);
            }
            jsonObject.put(key, array);
        });
        return jsonObject;
    }

    private static void jsonToRequests(JSONObject jsonObject, Map<String, List<FriendRequest>> map) {
        jsonObject.keySet().forEach(key -> {
            JSONArray array = jsonObject.getJSONArray(key);
            List<FriendRequest> requests = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject reqObj = array.getJSONObject(i);
                requests.add(new FriendRequest(
                        reqObj.getString("senderId"),
                        reqObj.getString("receiverId"),
                        reqObj.getString("status")
                ));
            }
            map.put(key, requests);
        });
    }

    private static JSONObject mapToJson(Map<String, List<String>> map) {
        JSONObject jsonObject = new JSONObject();
        map.forEach(jsonObject::put);
        return jsonObject;
    }

    private static void jsonToMap(JSONObject jsonObject, Map<String, List<String>> map) {
        jsonObject.keySet().forEach(key -> {
            JSONArray array = jsonObject.getJSONArray(key);
            List<String> list = array.toList().stream().map(Object::toString).collect(Collectors.toList());
            map.put(key, list);
        });
    }
}

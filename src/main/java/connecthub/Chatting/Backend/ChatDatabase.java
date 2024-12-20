package connecthub.Chatting.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private static final String FILEPATH = "ChatMessages.JSON";
    private static ChatDatabase chatDatabase;
    private final List<ChatMessage> messages = new ArrayList<>();

    private ChatDatabase() {
        loadMessagesFromFile(); // Load messages when the instance is created
    }

    public static ChatDatabase getInstance() {
        if (chatDatabase == null) {
            chatDatabase = new ChatDatabase();
        }
        return chatDatabase;
    }

    public void saveMessagesToFile() {
        JSONArray messagesArray = new JSONArray();
        for (ChatMessage message : messages) {
            JSONObject obj = new JSONObject();
            obj.put("senderId", message.getSenderId());
            obj.put("receiverId", message.getReceiverId());
            obj.put("message", message.getMessage());
            obj.put("timestamp", message.getTimestamp().toString());
            messagesArray.put(obj);
        }
        try (FileWriter file = new FileWriter(FILEPATH)) {
            file.write(messagesArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMessagesFromFile() {
        messages.clear(); // Clear existing messages before loading
        try {
            String json = new String(Files.readAllBytes(Paths.get(FILEPATH)));
            JSONArray messagesArray = new JSONArray(json);
            for (int i = 0; i < messagesArray.length(); i++) {
                JSONObject obj = messagesArray.getJSONObject(i);
                ChatMessage message = new ChatMessage(
                        obj.getString("senderId"),
                        obj.getString("receiverId"),
                        obj.getString("message"),
                        LocalDateTime.parse(obj.getString("timestamp"))
                );
                messages.add(message);
            }
        } catch (IOException e) {
            System.out.println("ChatMessages.JSON not found. Starting fresh.");
        }
    }

    // New method to reload messages from the file
    public void reloadMessages() {
        loadMessagesFromFile();
    }

    public void sendMessage(String senderId, String receiverId, String messageContent) {
        ChatMessage message = new ChatMessage(senderId, receiverId, messageContent, LocalDateTime.now());
        messages.add(message);
        saveMessagesToFile();
    }


    public boolean deleteMessage(String senderId, LocalDateTime timestamp) {
        for (ChatMessage message : messages) {
            if (message.getSenderId().equals(senderId) && message.getTimestamp().equals(timestamp)) {
                if (isInTimeLimit(message.getTimestamp())) {
                    message.setMessage("#* This message has been deleted *#");
                    saveMessagesToFile();
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public List<ChatMessage> getChatHistory(String userId1, String userId2) {
        List<ChatMessage> userMessages = new ArrayList<>();
        for (ChatMessage message : messages) {
            // Check if the message is between the two users
            if ((message.getSenderId().equals(userId1) && message.getReceiverId().equals(userId2)) ||
                    (message.getSenderId().equals(userId2) && message.getReceiverId().equals(userId1))) {
                userMessages.add(message);
            }
        }
        return userMessages;
    }

    private boolean isInTimeLimit(LocalDateTime timestamp) {
        Duration duration = Duration.between(timestamp, LocalDateTime.now());
        return duration.toMinutes() <= 15;
    }
}
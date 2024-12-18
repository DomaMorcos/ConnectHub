package connecthub.Chatting.Backend;

import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;

import java.time.LocalDateTime;

public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String message;
    private LocalDateTime timestamp;
    UserDatabase userDatabase = UserDatabase.getInstance();

    public ChatMessage(String senderId, String receiverId, String message, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderUsername () {
        User user = userDatabase.getUserById(senderId);
        return user.getUsername();
    }


    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ChatMessage{" + "senderId='" + senderId + '\'' + ", receiverId='" + receiverId + '\'' + ", message='" + message + '\'' + ", timestamp=" + timestamp + '}';
    }
}
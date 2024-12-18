package connecthub.Chatting.Frontend;

import connecthub.Chatting.Backend.ChatDatabase;
import connecthub.Chatting.Backend.ChatMessage;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ChattingPage {

    private final ChatDatabase chatDatabase = ChatDatabase.getInstance();
    private final TextArea chatArea = new TextArea();
    private final TextField messageField = new TextField();

    public void start(String userID, String friendID) throws Exception {

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Chatting Page");

        BorderPane root = new BorderPane();
        VBox chatBox = new VBox();
        chatBox.setSpacing(10);
        chatBox.setPadding(new Insets(10));

        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        refreshChatHistory(userID, friendID);

        HBox messageBox = new HBox();
        messageBox.setSpacing(10);



        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage(userID, friendID));

        Button editButton = new Button("Edit Last Message");
        editButton.setOnAction(e -> editLastMessage(userID, friendID));

        Button deleteButton = new Button("Delete Last Message");
        deleteButton.setOnAction(e -> deleteLastMessage(userID, friendID));


        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshChatHistory(userID, friendID));

        messageBox.getChildren().addAll(messageField, sendButton, editButton, deleteButton, refreshButton);
        messageBox.setPadding(new Insets(10));

        chatBox.getChildren().addAll(chatArea, messageBox);
        root.setCenter(chatBox);

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    private void refreshChatHistory(String userID, String friendID) {
        chatDatabase.reloadMessages();
        List<ChatMessage> messages = chatDatabase.getChatHistory(userID, friendID);
        chatArea.clear();
        for (ChatMessage message : messages) {
            chatArea.appendText(formatMessage(message) + "\n");
        }
    }

    private void sendMessage(String userID, String friendID) {
        String messageContent = messageField.getText().trim();
        if (!messageContent.isEmpty()) {
            chatDatabase.sendMessage(userID, friendID, messageContent);
            messageField.clear();
            refreshChatHistory(userID, friendID);
        }
        else {
            showAlert("Empty Message", "You must send a message that contains letters.");
        }
    }

    private void editLastMessage(String userID, String friendID) {
        List<ChatMessage> messages = chatDatabase.getChatHistory(userID, friendID);
        if (!messages.isEmpty()) {
            ChatMessage lastMessage = messages.get(messages.size() - 1);
            if (lastMessage.getSenderId().equals(userID)) {
                boolean success = chatDatabase.editMessage(userID, lastMessage.getTimestamp(), "Edited: " + messageField.getText().trim());
                if (success) {
                    refreshChatHistory(userID, friendID);
                } else {
                    showAlert("Edit Error", "Cannot edit the message as the time limit has passed.");
                }
            } else {
                showAlert("Edit Error", "You can only edit your own messages.");
            }
        }
    }

    private void deleteLastMessage(String userID, String friendID) {
        List<ChatMessage> messages = chatDatabase.getChatHistory(userID, friendID);
        if (!messages.isEmpty()) {
            ChatMessage lastMessage = messages.get(messages.size() - 1);
            if (lastMessage.getSenderId().equals(userID)) {
                boolean success = chatDatabase.deleteMessage(userID, lastMessage.getTimestamp());
                if (success) {
                    refreshChatHistory(userID, friendID);
                } else {
                    showAlert("Delete Error", "Cannot delete the message as the time limit has passed.");
                }
            } else {
                showAlert("Delete Error", "You can only delete your own messages.");
            }
        }
    }


    private String formatMessage(ChatMessage message) {
        return String.format("[%s] %s: %s",
                message.getTimestamp().toString(),
                message.getSenderUsername(),
                message.getMessage());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
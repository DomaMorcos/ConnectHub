package connecthub.Chatting.Frontend;

import connecthub.Chatting.Backend.ChatDatabase;
import connecthub.Chatting.Backend.ChatMessage;
import connecthub.TimestampFormatter;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ChattingPage {

    private final ChatDatabase chatDatabase = ChatDatabase.getInstance();
    UserDatabase userDatabase = UserDatabase.getInstance();
    private final VBox messagesContainer = new VBox();
    private final TextField messageField = new TextField();

    public void start(String userID, String friendID) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Chat with " + userDatabase.getUserById(friendID).getUsername());

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F0F2F5;");

        // Header
        HBox header = createHeader(friendID);
        root.setTop(header);

        // Chat Area
        ScrollPane chatScrollPane = createChatArea();
        root.setCenter(chatScrollPane);

        // Message Input
        HBox messageBox = createMessageBox(userID, friendID);
        root.setBottom(messageBox);

        // Initial Chat Load
        refreshChatHistory(userID, friendID);

        primaryStage.setScene(new Scene(root, 600, 800));
        primaryStage.show();
    }

    private HBox createHeader(String friendID) {
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #3B5998; -fx-alignment: center-left;");

        // Friend Name
        Label friendName = new Label( userDatabase.getUserById(friendID).getUsername());
        friendName.setStyle("-fx-font-size: 18; -fx-text-fill: white; -fx-padding: 0 10;");

        header.getChildren().add(friendName);
        return header;
    }

    private ScrollPane createChatArea() {
        messagesContainer.setSpacing(10);
        messagesContainer.setPadding(new Insets(10));
        messagesContainer.setStyle("-fx-background-color: white; -fx-padding: 10;");

        ScrollPane scrollPane = new ScrollPane(messagesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");
        return scrollPane;
    }

    private HBox createMessageBox(String userID, String friendID) {
        HBox messageBox = new HBox();
        messageBox.setPadding(new Insets(10));
        messageBox.setSpacing(10);
        messageBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DADDE1;");

        // Message Field
        messageField.setPromptText("Type a message...");
        messageField.setStyle("-fx-background-color: #F0F2F5; -fx-border-radius: 15; -fx-background-radius: 15;");
        messageField.setPrefWidth(250);

        // Send Button
        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #4267B2; -fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15;");
        sendButton.setOnAction(e -> sendMessage(userID, friendID));

        // Delete Button
        Button deleteButton = new Button("Delete Last");
        deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15;");
        deleteButton.setOnAction(e -> deleteLastMessage(userID, friendID));

        // Refresh Button
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15;");
        refreshButton.setOnAction(e -> refreshChatHistory(userID, friendID));

        messageBox.getChildren().addAll(messageField, sendButton, deleteButton, refreshButton);
        return messageBox;
    }

    private void refreshChatHistory(String userID, String friendID) {
        messagesContainer.getChildren().clear();
        chatDatabase.reloadMessages();
        List<ChatMessage> messages = chatDatabase.getChatHistory(userID, friendID);

        for (ChatMessage message : messages) {
            messagesContainer.getChildren().add(formatMessage(message, userID));
        }
    }

    private HBox formatMessage(ChatMessage message, String userID) {
        HBox messageBox = new HBox();
        messageBox.setSpacing(10);
        messageBox.setPadding(new Insets(5));

        boolean isOwnMessage = message.getSenderId().equals(userID);
        messageBox.setStyle(isOwnMessage
                ? "-fx-alignment: center-right; -fx-background-color: #D1E7DD; -fx-border-radius: 10; -fx-background-radius: 10;"
                : "-fx-alignment: center-left; -fx-background-color: #F8D7DA; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label messageLabel = new Label(message.getMessage());
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(5));
        messageLabel.setStyle("-fx-font-size: 14; -fx-text-fill: black;");

        Label timestampLabel = new Label(TimestampFormatter.formatTimeAgo(message.getTimestamp().toString()));
        timestampLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");

        VBox messageDetails = new VBox(messageLabel, timestampLabel);
        messageBox.getChildren().add(messageDetails);

        return messageBox;
    }

    private void sendMessage(String userID, String friendID) {
        String messageContent = messageField.getText().trim();
        if (!messageContent.isEmpty()) {
            chatDatabase.sendMessage(userID, friendID, messageContent);
            messageField.clear();
            refreshChatHistory(userID, friendID);
        } else {
            showAlert("Empty Message", "You must send a message that contains letters.");
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

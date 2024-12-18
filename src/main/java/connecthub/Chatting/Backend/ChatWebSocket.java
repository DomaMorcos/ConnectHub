package connecthub.Chatting.Backend;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/chat")
public class ChatWebSocket {
    private Session session;
    private final URI serverUri;
    private final String userID;

    public ChatWebSocketClient(URI serverUri, String userID) {
        this.serverUri = serverUri;
        this.userID = userID;
    }

    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, serverUri); // Use the provided URI
        } catch (Exception e) {
            System.err.println("WebSocket connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to WebSocket as " + userID);
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("WebSocket message received: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("WebSocket connection closed.");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    public void sendMessage(String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
            } else {
                System.err.println("WebSocket session is not open.");
            }
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
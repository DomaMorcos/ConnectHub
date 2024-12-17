module connecthub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.json;
    requires org.json;
    requires java.desktop;
    requires javax.websocket.api;
//    requires jakarta.websocket.api;
    requires tyrus.server;


    opens connecthub to javafx.fxml;
    exports connecthub;
    exports connecthub.UserAccountManagement.Frontend;
    exports connecthub.NewsfeedPage.Frontend;
}
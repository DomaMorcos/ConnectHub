module connecthub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.json;
    requires org.json;
    requires java.desktop;


    opens connecthub to javafx.fxml;
    exports connecthub;
    exports connecthub.UserAccountManagement.Frontend;
}
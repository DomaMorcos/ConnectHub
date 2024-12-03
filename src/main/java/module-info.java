module connecthub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.json;


    opens connecthub to javafx.fxml;
    exports connecthub;
}
module connecthub {
    requires javafx.controls;
    requires javafx.fxml;


    opens connecthub to javafx.fxml;
    exports connecthub;
}
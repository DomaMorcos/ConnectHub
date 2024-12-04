module connecthub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.json;
    requires org.json;


    opens connecthub to javafx.fxml;
    exports connecthub;
}
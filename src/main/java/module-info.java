module com.example.essprjtjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.essprjtjava to javafx.fxml;
    opens com.essprjtjava.controller to javafx.fxml;
    exports com.essprjtjava;
}
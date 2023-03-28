module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;

    opens org.example to javafx.fxml;
    opens org.example.controller to javafx.fxml;

    exports org.example;
    exports org.example.model;
    exports org.example.controller;
}
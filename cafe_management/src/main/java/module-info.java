module com.example.cafe_management {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.cafe_management to javafx.fxml;
    exports com.example.cafe_management;
}
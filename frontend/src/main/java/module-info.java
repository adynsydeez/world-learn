module com.worldlearn.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.worldlearn.frontend to javafx.fxml;
    exports com.worldlearn.frontend;
}
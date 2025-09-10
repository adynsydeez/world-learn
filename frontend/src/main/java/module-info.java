module com.worldlearn.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires com.worldlearn.backend;

    opens com.worldlearn.frontend to javafx.fxml;
    exports com.worldlearn.frontend;
    exports com.worldlearn.frontend.services;
    opens com.worldlearn.frontend.services to javafx.fxml;
}
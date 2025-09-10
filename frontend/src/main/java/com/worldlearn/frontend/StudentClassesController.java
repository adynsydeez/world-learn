package com.worldlearn.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Parent; // optional, but handy
import java.io.IOException;

public class StudentClassesController {
    @FXML private Button homeButton;
    @FXML private Button lessonButton;

    @FXML
    protected void onHomeButtonClick() throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();

        // Use absolute classpath path; put FXMLs under src/main/resources/com/worldlearn/frontend/
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/worldlearn/frontend/student-dashboard-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onLessonButtonClick() throws IOException {
        Stage stage = (Stage) lessonButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/worldlearn/frontend/student-lesson-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}

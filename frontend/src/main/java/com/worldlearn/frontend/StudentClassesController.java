package com.worldlearn.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import java.io.IOException;

public class StudentClassesController {
    @FXML
    private Button homeButton;
    @FXML
    private Button lessonButton;

    @FXML
    protected void onHomeButtonClick() throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
<<<<<<<< HEAD:frontend/src/main/java/com/worldlearn/frontend/ClassesView.java
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/resources/hello-view.fxml"));
========
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
>>>>>>>> 4c50013e154809080ad1efc11407a8fbd292f921:frontend/src/main/java/com/worldlearn/frontend/StudentClassesController.java
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
    }
    @FXML
    protected void onLessonButtonClick() throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
    }
}

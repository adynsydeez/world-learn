package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import java.io.IOException;

public class StudentLessonController {

    @FXML
    private Button homeButtonLessonPage;
    @FXML
    private Button profileButtonLessonPage;

    @FXML
    protected void onHomeButtonClickLessonPage() throws IOException {
        Stage stage = (Stage) homeButtonLessonPage.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
    }
    @FXML
    protected void onProfileButtonClickLessonPage() throws IOException {
        Stage stage = (Stage) profileButtonLessonPage.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
    }

}

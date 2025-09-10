package com.worldlearn.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import java.io.IOException;

public class StudentDashboardController {

    private User user;
    private Stage stage;

    //pass user, stage to controller
    public void init(User user, Stage stage) {
        this.user = user;
        this.stage = stage;
        welcomeLabel.setText("Welcome, " + user.getEmail() + "!");
    }

    @FXML private Label welcomeLabel;
    @FXML private Button classesButton;
    @FXML private Button profileButton;
    @FXML private Button teacherButton;

    @FXML
    protected void onClassesButtonClick() throws IOException {
        Stage stage = (Stage) classesButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("student-classes-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
    }
    @FXML
    protected void setProfileButtonClick() throws IOException {
        Stage stage = (Stage) profileButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
    }
    @FXML
    protected void onTeacherButtonClick() throws IOException {   // NEW method
        Stage stage = (Stage) teacherButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("teacher-dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700); // dashboard is bigger
        stage.setScene(scene);
    }


}
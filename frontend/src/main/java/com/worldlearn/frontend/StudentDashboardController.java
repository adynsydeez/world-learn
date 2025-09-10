package com.worldlearn.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.worldlearn.frontend.services.AuthenticationService;

import javafx.scene.control.Button;
import java.io.IOException;

public class StudentDashboardController {

    private User user;
    private Stage stage;
    private AuthenticationService auth;

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
    @FXML private Button logoutButton;
    @FXML
    protected void onLogoutButtonClick() throws Exception {
        // clear any local session state if you want
        this.user = null;

        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("Auth-view.fxml"));
        Scene scene = new Scene(fxml.load(), 900, 650);

        // re-init the AuthController with the SAME auth service + stage
        AuthController authController = fxml.getController();
        authController.init((auth != null ? auth : new AuthenticationService()), stage);

        stage.setScene(scene);
        stage.show();
    }



}
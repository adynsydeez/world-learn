package com.worldlearn.frontend;

import com.worldlearn.frontend.services.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class StudentDashboardController {
    private User user;
    private Stage stage;
    private AuthenticationService auth;


    public void init(User user, Stage stage) { init(user, stage, null); }

    public void init(User user, Stage stage, AuthenticationService auth) {
        this.user = user;
        this.stage = stage;
        this.auth  = auth;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + user.getEmail() + "!");
        }
    }

    @FXML private Label welcomeLabel;
    @FXML private Button classesButton;
    @FXML private Button profileButton;
    @FXML private Button teacherButton;
    @FXML private Button logoutButton;

    @FXML
    protected void onClassesButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-classes-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);

        // pass context into the next controller
        StudentClassesController c = fxml.getController();
        c.init(user, stage, auth);

        stage.setScene(scene);
    }

    @FXML
    protected void setProfileButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);

        ProfileController controller = fxml.getController();
        controller.init(user, stage, auth);

        stage.setScene(scene);
    }

    @FXML
    protected void onLogoutButtonClick() throws Exception {
        user = null;
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("Auth-view.fxml"));
        Scene scene = new Scene(fxml.load(), 900, 650);
        AuthController authController = fxml.getController();
        authController.init((auth != null ? auth : new AuthenticationService()), stage);
        stage.setScene(scene);
        stage.show();
    }
}

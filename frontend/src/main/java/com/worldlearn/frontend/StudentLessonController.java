package com.worldlearn.frontend;

import com.worldlearn.frontend.services.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class StudentLessonController {
    private User user;
    private Stage stage;
    private AuthenticationService auth;

    public void init(User user, Stage stage, AuthenticationService auth) {
        this.user = user;
        this.stage = stage;
        this.auth  = auth;

    }

    @FXML private Button homeButtonLessonPage;
    @FXML private Button profileButtonLessonPage;

    @FXML
    protected void onHomeButtonClickLessonPage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);

        StudentDashboardController c = fxml.getController();
        c.init(user, stage, auth);

        stage.setScene(scene);
    }

    @FXML
    protected void onProfileButtonClickLessonPage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);

        ProfileController c = fxml.getController();
        c.init(user, stage, auth);

        stage.setScene(scene);
    }
}
package com.worldlearn.frontend;

import com.worldlearn.backend.database.AuthenticationService;
import com.worldlearn.backend.database.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class StudentClassesController {
    private User user;
    private Stage stage;
    private AuthenticationService auth;

    public void init(User user, Stage stage, AuthenticationService auth) {
        this.user = user;
        this.stage = stage;
        this.auth  = auth;
    }

    @FXML private Button homeButton;
    @FXML private Button lessonButton;

    @FXML
    protected void onHomeButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);

        StudentDashboardController c = fxml.getController();
        c.init(user, stage, auth);

        stage.setScene(scene);
    }

    @FXML
    protected void onLessonButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);

        StudentLessonController c = fxml.getController();
        c.init(user, stage, auth);

        stage.setScene(scene);
    }
}

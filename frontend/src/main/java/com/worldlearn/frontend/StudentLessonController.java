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
    @FXML private Button classesView; // ok to keep, even though name matches a method

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

    @FXML
    protected void quizSelected(javafx.scene.input.MouseEvent e) throws Exception {

        javafx.scene.Node n = (javafx.scene.Node) e.getTarget();
        while (n != null && !(n instanceof javafx.scene.layout.HBox)) n = n.getParent();
        if (n == null) return;
        javafx.scene.layout.HBox row = (javafx.scene.layout.HBox) n;

       //find what quiz was selected
        String quizTitle = null;
        for (javafx.scene.Node child : row.getChildren()) {
            if (child instanceof javafx.scene.control.Label) {
                javafx.scene.control.Label lbl = (javafx.scene.control.Label) child;
                quizTitle = lbl.getText();
                break;
            }
        }

        if (quizTitle == null) {
            int idx = ((javafx.scene.layout.VBox) row.getParent()).getChildren().indexOf(row) + 1;
            quizTitle = "Quiz " + idx;
        }


        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("student-question-view.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        StudentQuestionViewController c = loader.getController();
        c.init(user, stage, auth);
        c.setLessonTitle(quizTitle);

        stage.setScene(scene);
    }

    @FXML
    protected void classesView() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-classes-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);
        StudentClassesController c = fxml.getController();
        c.init(user, stage, auth);

        stage.setScene(scene);
    }
}

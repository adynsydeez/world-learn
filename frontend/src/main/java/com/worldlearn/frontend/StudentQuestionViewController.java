package com.worldlearn.frontend;

import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class StudentQuestionViewController {
    private User user;
    private Stage stage;
    private AuthClientService auth;

    public void init(User user, Stage stage, AuthClientService auth) {
        this.user = user;
        this.stage = stage;
        this.auth  = auth;

    }

    @FXML private Button homeButtonLessonPage;
    @FXML private Button profileButtonLessonPage;
    @FXML private Button quizView;

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
    protected void onQuestionSelected(javafx.scene.input.MouseEvent e) throws Exception {
        // Figure out which question row was clicked so we can easily change question title header
        javafx.scene.Node node = (javafx.scene.Node) e.getTarget();
        while (node != null && !(node instanceof javafx.scene.layout.HBox)) {
            node = node.getParent();
        }
        if (node == null) return;
        javafx.scene.layout.HBox clickedRow = (javafx.scene.layout.HBox) node;
        javafx.scene.layout.VBox list = (javafx.scene.layout.VBox) clickedRow.getParent();
        int questionNumber = list.getChildren().indexOf(clickedRow) + 1;

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("multiple-choice-question-view.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        MultipleChoiceQuestionController c = loader.getController();

        String region  = null; //idk if we want to add regions in yet but if we do heres this
        String qText   = "Why did the chicken cross the road?";
        var choices    = java.util.List.of("To get to the other side","To get to the other side","To get to the other side","To get to the other side");
        String correct = "Answer B";
        String map     = null;

        c.init(user, stage, auth, questionNumber, region, qText, choices, correct, map);
        stage.setScene(scene);
    }
    @FXML
    protected void quizView() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);

        StudentLessonController c = fxml.getController();
        c.init(user, stage, auth);

        stage.setScene(scene);
    }
    @FXML private javafx.scene.control.Label lessonTitleLabel;

    public void setLessonTitle(String title) {
        lessonTitleLabel.setText(title);
    }



}



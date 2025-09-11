package com.worldlearn.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class TeacherDashboardController {

    private User user;
    private Stage stage;

    // pass user,stage to controller
    public void init(User user, Stage stage) {
        this.user = user;
        this.stage = stage;
    }

    @FXML private Label lblClasses;
    @FXML private Label lblLessons;
    @FXML private Label lblQuizzes;
    @FXML private Label lblQuestions;

    @FXML private Button createLessonBtn;
    @FXML private Button createClassBtn;
    @FXML private Button createQuizBtn;
    @FXML private Button createQuestionBtn;

    @FXML
    private void initialize() {
        setupHover(lblClasses);
        setupHover(lblLessons);
        setupHover(lblQuizzes);
        setupHover(lblQuestions);
    }

    private void setupHover(Label label) {
        label.setOnMouseEntered(e -> {
            label.setTextFill(Color.YELLOW);
            label.setStyle("-fx-underline: true;");
        });
        label.setOnMouseExited(e -> {
            label.setTextFill(Color.WHITE);
            label.setStyle("-fx-underline: false;");
        });
    }

    @FXML
    protected void onCreateQuestionClick() throws IOException {
        Stage stage = (Stage) createQuestionBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("question-creation-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
    }

}


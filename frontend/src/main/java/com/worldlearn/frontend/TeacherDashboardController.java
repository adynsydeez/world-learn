package com.worldlearn.frontend;

import com.worldlearn.backend.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TeacherDashboardController {

    private User user;
    private Stage stage;

    // pass user,stage to controller
    public void init(Stage stage) {
        User user = Session.getCurrentUser();
        if(user == null){
            System.err.println("No user logged in.");
        }
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
    private void openPopup(String fxmlPath, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Stage parentStage = (Stage) createQuestionBtn.getScene().getWindow();
            popupStage.initOwner(parentStage);

            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onCreateQuestionClick() {
        openPopup("question-creation-view.fxml", "Create Question");
    }

    @FXML
    protected void onCreateClassClick() {
        openPopup("class-creation-view.fxml", "Create Class");
    }

    @FXML
    protected void onCreateLessonClick() {
        openPopup("lesson-creation-view.fxml", "Create Lesson");
    }

    @FXML
    protected void onCreateQuizClick() {
        openPopup("quiz-creation-view.fxml", "Create Quiz");
    }


}


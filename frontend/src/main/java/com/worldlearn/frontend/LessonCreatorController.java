package com.worldlearn.frontend;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import com.worldlearn.backend.dto.CreateLessonRequest;
import com.worldlearn.backend.dto.CreateQuizRequest;
import com.worldlearn.backend.models.Quiz;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.worldlearn.frontend.services.ApiService;
import com.worldlearn.backend.models.Question.Visibility;

public class LessonCreatorController {

    String lessonName = "Lesson name";

    //Dummy Data
    List<Integer> quizIds = List.of(21);
    private final ApiService apiService = new ApiService();

    @FXML private TextField lessonTitleField;
    @FXML private TextArea lessonDescriptionField;
    @FXML private Button createLessonBtn;

    private List<Quiz> quizzes;

    @FXML
    private void goHome(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("teacher-dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    @FXML
    private void handleAddContent(ActionEvent event) {
        System.out.println("Add Content clicked for lesson: " + lessonTitleField.getText());
    }

    @FXML
    private void handleAddQuiz(ActionEvent event) {
        System.out.println("Add Quiz clicked for lesson: " + lessonTitleField.getText());
    }


    @FXML
    private void handleCreateLesson() {
        System.out.println("Lesson created: " + lessonTitleField.getText() +
                " | Description: " + lessonDescriptionField.getText());

        CreateLessonRequest lessonRequest = new CreateLessonRequest(
                lessonName,
                Visibility.PRIVATE,
                quizIds
        );

        apiService.getQuizQuestionsAsync(22)
                .thenAccept(questions -> Platform.runLater(() -> {
                    System.out.println("Fetched " + questions.size() + " question(s)");
                    //System.out.println(questions.getFirst().getQuestionName());
                }))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });


    }
}
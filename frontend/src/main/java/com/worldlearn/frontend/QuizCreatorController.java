package com.worldlearn.frontend;

import com.worldlearn.frontend.services.ApiService;
import com.worldlearn.backend.models.Question;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


public class QuizCreatorController {
    @FXML private VBox privateContainer;
    @FXML private Button addToQuizBtn;
    @FXML private Button getBtn;
    @FXML private Button searchBtn;

    private List<Question> quizQuestions = new ArrayList<>();

    private final List<Question> selectedQuestions = new ArrayList<>();

    int teacherId = Session.getCurrentUser().getId();
    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        getBtn.setOnAction(e -> {
            getTeacherQuestions();
        });
        searchBtn.setOnAction(e ->{
            // get public Questions
        });
        addToQuizBtn.setOnAction(e -> {
            addToQuiz(selectedQuestions);
        });

    }

    @FXML
    private void getTeacherQuestions() {
        apiService.getAllTeacherQuestionsAsync(teacherId)
                .thenAccept(questions -> {
                    javafx.application.Platform.runLater(() -> {
                        System.out.println("Fetched " + questions.size() + " questions:");
                        privateContainer.getChildren().clear();
                        for (Question q : questions) {
                            CheckBox cb = new CheckBox(q.getPrompt());
                            cb.setUserData(q);
                            privateContainer.getChildren().add(cb);
                        }
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void addToQuiz(List<Question> questions) {
        for (Question q : questions) {
            if (!quizQuestions.contains(q)) {
                quizQuestions.add(q);
            }
        }
    }
}

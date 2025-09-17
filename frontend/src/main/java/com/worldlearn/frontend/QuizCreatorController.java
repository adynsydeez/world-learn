package com.worldlearn.frontend;

import com.worldlearn.frontend.services.ApiService;
import com.worldlearn.backend.models.Question;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class QuizCreatorController {
    @FXML private ListView<Question> teacherQuestionsList;
    @FXML private ListView<Question> searchQuestionsList;
    @FXML private ListView<Question> quizQuestionsList;
    @FXML private Button addToQuizBtn;
    @FXML private Button searchBtn;
    @FXML private Button removeBtn;
    @FXML private Button saveBtn;

    private ObservableList<Question> teacherQuestions = FXCollections.observableArrayList();
    private ObservableList<Question> searchQuestions = FXCollections.observableArrayList();
    private ObservableList<Question> quizQuestions = FXCollections.observableArrayList();

    int teacherId = Session.getCurrentUser().getId();
    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        // hook up observable lists to UI
        teacherQuestionsList.setItems(teacherQuestions);
        searchQuestionsList.setItems(searchQuestions);
        quizQuestionsList.setItems(quizQuestions);

        teacherQuestionsList.setCellFactory(lv -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestionName());
            }
        });

        searchQuestionsList.setCellFactory(lv -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestionName());
            }
        });

        quizQuestionsList.setCellFactory(lv -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestionName());
            }
        });

        // allow multi-selection in teacher list
        teacherQuestionsList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        searchQuestionsList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        quizQuestionsList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        searchBtn.setOnAction(e -> {
            searchQuestions();
        });

        addToQuizBtn.setOnAction(e -> {
            List<Question> privateSelected = new ArrayList<>(teacherQuestionsList.getSelectionModel().getSelectedItems());
            List<Question> publicSelected = new ArrayList<>(searchQuestionsList.getSelectionModel().getSelectedItems());
            addToQuiz(privateSelected);
            addToQuiz(publicSelected);
        });

        removeBtn.setOnAction(e -> {
            List<Question> selected = new ArrayList<>(quizQuestionsList.getSelectionModel().getSelectedItems());
            removeFromQuiz(selected);
        });
        getTeacherQuestions();

        saveBtn.setOnAction(e -> {
            saveQuiz(quizQuestions);
        });
    }

    private void getTeacherQuestions() {
        apiService.getAllTeacherQuestionsAsync(teacherId)
                .thenAccept(questions -> Platform.runLater(() -> {
                    System.out.println("Fetched " + questions.size() + " questions");
                    teacherQuestions.setAll(questions);
                }))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void searchQuestions() {
        apiService.getPublicQuestionsAsync()
                .thenAccept(questions -> Platform.runLater(() -> {
                    System.out.println("Fetched " + questions.size() + " questions");
                    searchQuestions.setAll(questions);
                }))
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

    private void removeFromQuiz(List<Question> questions) {
        for (Question q : questions) {
            quizQuestions.remove(q);
        }
    }

    private void clearQuiz() {
        quizQuestions.clear();
    }

    private void saveQuiz(List<Question> questions) {
        System.out.println("Need Save Quiz Implementation!");
    }
}

package com.worldlearn.frontend;

import com.worldlearn.frontend.services.ApiService;
import com.worldlearn.backend.models.Question;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class QuizCreatorController {
    @FXML private ListView<Question> teacherQuestionsList;
    @FXML private ListView<Question> searchQuestionsList;
    @FXML private ListView<Question> quizQuestionsList;
    //@FXML private ComboBox<Visibility> visibilityComboBox;
    @FXML private Button addToQuizBtn;
    @FXML private Button removeBtn;
    @FXML private Button saveBtn;
    @FXML private Button clearBtn;
    @FXML private TextField searchField;
    @FXML private TextField nameField;

    private ObservableList<Question> teacherQuestions = FXCollections.observableArrayList();
    private ObservableList<Question> searchQuestions = FXCollections.observableArrayList();
    private ObservableList<Question> quizQuestions = FXCollections.observableArrayList();

    private FilteredList<Question> filteredSearch;

    int teacherId = Session.getCurrentUser().getId();
    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        // hook up observable lists to UI
        teacherQuestionsList.setItems(teacherQuestions);
        searchQuestionsList.setItems(searchQuestions);
        quizQuestionsList.setItems(quizQuestions);

        searchQuestions();

        // wrap your observable list in a FilteredList
        filteredSearch = new FilteredList<>(searchQuestions, q -> true);

// bind filtered list to ListView
        searchQuestionsList.setItems(filteredSearch);

// add listener on text field
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase();
            filteredSearch.setPredicate(q -> {
                if (filter == null || filter.isEmpty()) {
                    return true; // show all
                }
                return q.getQuestionName().toLowerCase().contains(filter);
            });
        });


        //visibilityComboBox.getItems().setAll(Quiz.Visibility.values());
        //visibilityComboBox.setValue(Quiz.Visibility.PRIVATE);

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

        clearBtn.setOnAction(e -> {
            clearQuiz();
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
            boolean alreadyAdded = quizQuestions.stream()
                    .anyMatch(existing -> existing.getQuestionId() == q.getQuestionId());
            if (!alreadyAdded) {
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

    private boolean checkInvalidName() {
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            return true;
        } else return nameField.getText().trim().length() < 2;
    }

    private boolean checkInvalidQuiz() {
        return quizQuestions.isEmpty();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveQuiz(List<Question> questions) {
        if (checkInvalidName()) {
            showAlert("Please enter a valid quiz name");
        } else if (checkInvalidQuiz()) {
            showAlert("ERROR: Quiz Must Contain Questions");
        }
        System.out.println("Need Save Quiz Implementation!");
    }
}

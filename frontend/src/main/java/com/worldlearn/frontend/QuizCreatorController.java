package com.worldlearn.frontend;

import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.models.Teacher;
import com.worldlearn.backend.models.User;
import com.worldlearn.backend.dto.CreateQuizRequest;
import com.worldlearn.backend.models.Question.Visibility;
import com.worldlearn.frontend.services.ApiService;
import com.worldlearn.backend.models.Question;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class QuizCreatorController {
    @FXML private ListView<Question> teacherQuestionsList;
    @FXML private ListView<Question> searchQuestionsList;
    @FXML private ListView<Question> quizQuestionsList;
    @FXML private ComboBox<Question.Visibility> visibilityComboBox;
    @FXML private Button addToQuizBtn;
    @FXML private Button removeBtn;
    @FXML private Button saveBtn;
    @FXML private Button clearBtn;
    @FXML private Button loadBtn;
    @FXML private TextField searchField;
    @FXML private TextField nameField;
    private String quizName;

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

        // wrap your observable list in a FilteredList
        filteredSearch = new FilteredList<>(searchQuestions, q -> true);

// bind filtered list to ListView
        searchQuestionsList.setItems(filteredSearch);

        visibilityComboBox.getItems().setAll(Visibility.values());
        visibilityComboBox.setValue(Visibility.PRIVATE);

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


        saveBtn.setOnAction(e -> {
            saveQuiz(quizQuestions);
        });

        clearBtn.setOnAction(e -> {
            clearQuiz();
        });

//        loadBtn.setOnAction(e -> {
//            getTeacherQuestions();
//            searchQuestions();
//        });

        getTeacherQuestions();
        getTeacherQuestions();
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setContentText(message);
        alert.showAndWait();
        // Close dialog after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                javafx.application.Platform.runLater(() -> closeDialog());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Visibility getVisibility() {
        Visibility selected = visibilityComboBox.getValue();
        if (selected == null) {
            throw new IllegalArgumentException("You must select a visibility.");
        }
        return selected;
    }

    private void saveQuiz(List<Question> questions) {
        System.out.println("Attempting Save Quiz.");
        if (checkInvalidName()) {
            showError("Please enter a valid quiz name");
        } else if (checkInvalidQuiz()) {
            showError("ERROR: Quiz Must Contain Questions");
        }
        quizName = nameField.getText().trim();
        try {
            List<Integer> questionIds = questions.stream()
                    .map(Question::getQuestionId)
                    .toList();

            CreateQuizRequest quizRequest = new CreateQuizRequest(
                    quizName,
                    getVisibility(),
                    questionIds
            );

            apiService.createQuizAsync(quizRequest)
                    .thenAccept(q -> System.out.println("Quiz saved:" + quizName))
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    })
                    .join();
            StringBuilder message = new StringBuilder("Quiz Created! \n \nName: " + quizRequest.getQuizName() + "\nvisibility: " + quizRequest.getVisibility().toString() + "\n" + "Questions:\n");
            for(Question question : questions) {
                message.append(question.getQuestionName()).append("\n");
            }
            showAlert(message.toString());

        } catch (IllegalArgumentException ex){
            showError(ex.getMessage());
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}

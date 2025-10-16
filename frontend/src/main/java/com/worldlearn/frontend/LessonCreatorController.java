package com.worldlearn.frontend;

import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.dto.CreateLessonRequest;
import com.worldlearn.frontend.services.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LessonCreatorController {
    @FXML private ListView<Quiz> teacherQuizzesList;
    @FXML private ListView<Quiz> lessonQuizzesList;
    @FXML private ComboBox<Question.Visibility> visibilityComboBox;
    @FXML private Button addToLessonBtn;
    @FXML private Button removeBtn;
    @FXML private Button saveBtn;
    @FXML private Button clearBtn;
    @FXML private Button loadBtn;
    @FXML private TextField nameField;
    @FXML private Label creatorTitle;

    private String lessonName;

    private ObservableList<Quiz> teacherQuizzes = FXCollections.observableArrayList();
    private ObservableList<Quiz> lessonQuizzes = FXCollections.observableArrayList();

    private final ApiService apiService = new ApiService();

    private Lesson lesson;

    @FXML
    public void initialize() {
        // Hook up lists to UI
        teacherQuizzesList.setItems(teacherQuizzes);
        lessonQuizzesList.setItems(lessonQuizzes);

        visibilityComboBox.getItems().setAll(Question.Visibility.values());
        visibilityComboBox.setValue(Question.Visibility.PRIVATE);

        // Cell factories
        teacherQuizzesList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuizName());
            }
        });

        lessonQuizzesList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuizName());
            }
        });

        // Allow multi-selection
        teacherQuizzesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lessonQuizzesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Button actions
        addToLessonBtn.setOnAction(e -> {
            List<Quiz> selected = new ArrayList<>(teacherQuizzesList.getSelectionModel().getSelectedItems());
            addToLesson(selected);
        });

        removeBtn.setOnAction(e -> {
            List<Quiz> selected = new ArrayList<>(lessonQuizzesList.getSelectionModel().getSelectedItems());
            removeFromLesson(selected);
        });

        clearBtn.setOnAction(e -> clearLesson());

        //loadBtn.setOnAction(e -> getTeacherQuizzes());

        getTeacherQuizzes();

        if(this.lesson != null) {
            getLessonQuizzes();
            nameField.setText(this.lesson.getLessonName());
            visibilityComboBox.setValue(this.lesson.getVisibility());
            saveBtn.setOnAction(e -> editLesson(lessonQuizzes));
            creatorTitle.setText("Edit Lesson");
        }
        else {
            saveBtn.setOnAction(e -> saveLesson(lessonQuizzes));
        }
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    private void getTeacherQuizzes() {
        apiService.getAllQuizzesAsync()
                .thenAccept(quizzes -> Platform.runLater(() -> {
                    System.out.println("Fetched " + quizzes.size() + " quizzes");
                    teacherQuizzes.setAll(quizzes);
                }))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void getLessonQuizzes() {
        apiService.getLessonQuizzes(this.lesson.getLessonId())
                .thenAccept(quizzes -> Platform.runLater(() -> {
                    System.out.println("Fetched " + quizzes.size() + " quizzes");
                    lessonQuizzes.setAll(quizzes);
                }))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void addToLesson(List<Quiz> quizzes) {
        for (Quiz q : quizzes) {
            boolean alreadyAdded = lessonQuizzes.stream()
                    .anyMatch(existing -> existing.getQuizID() == q.getQuizID());
            if (!alreadyAdded) {
                lessonQuizzes.add(q);
            }
        }
    }

    private void removeFromLesson(List<Quiz> quizzes) {
        lessonQuizzes.removeAll(quizzes);
    }

    private void clearLesson() {
        lessonQuizzes.clear();
    }

    private boolean checkInvalidName() {
        return nameField.getText() == null || nameField.getText().trim().length() < 2;
    }

    private boolean checkInvalidLesson() {
        return lessonQuizzes.isEmpty();
    }

    private Question.Visibility getVisibility() {
        Question.Visibility selected = visibilityComboBox.getValue();
        if (selected == null) {
            throw new IllegalArgumentException("You must select a visibility.");
        }
        return selected;
    }

    private void saveLesson(List<Quiz> quizzes) {
        System.out.println("Attempting Save Lesson.");

        if (checkInvalidName()) {
            showError("Please enter a valid lesson name");
            return;
        }
        if (checkInvalidLesson()) {
            showError("ERROR: Lesson Must Contain Quizzes");
            return;
        }

        lessonName = nameField.getText().trim();
        try {
            List<Integer> quizIds = quizzes.stream()
                    .map(Quiz::getQuizID)
                    .toList();

            CreateLessonRequest lessonRequest = new CreateLessonRequest(
                    lessonName,
                    getVisibility(),
                    quizIds
            );

            apiService.createLessonAsync(lessonRequest)
                    .thenAccept(l -> System.out.println("Lesson saved: " + lessonName))
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    })
                    .join();

            StringBuilder message = new StringBuilder("Lesson Created! \n \nName: " + lessonRequest.getLessonName() + "\nvisibility: " + lessonRequest.getVisibility().toString() + "\n" + "Quizzes:\n");
            for(Quiz quiz : quizzes) {
                message.append(quiz.getQuizName()).append("\n");
            }
            showAlert(message.toString());

        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void editLesson(List<Quiz> quizzes) {
        System.out.println("Attempting Edit Lesson.");

        if (checkInvalidName()) {
            showError("Please enter a valid lesson name");
            return;
        }
        if (checkInvalidLesson()) {
            showError("ERROR: Lesson Must Contain Quizzes");
            return;
        }

        lessonName = nameField.getText().trim();
        try {
            List<Integer> quizIds = quizzes.stream()
                    .map(Quiz::getQuizID)
                    .toList();

            CreateLessonRequest lessonRequest = new CreateLessonRequest(
                    this.lesson.getLessonId(),
                    lessonName,
                    getVisibility(),
                    quizIds
            );

            apiService.updateLessonAsync(lessonRequest)
                    .thenAccept(l -> {
                        System.out.println("Lesson saved: " + lessonName);

                        // Only show success alert if the update actually succeeded
                        Platform.runLater(() -> {
                            StringBuilder message = new StringBuilder("Lesson Updated! \n \nName: " + lessonRequest.getLessonName() + "\nvisibility: " + lessonRequest.getVisibility().toString() + "\n" + "Quizzes:\n");
                            for(Quiz quiz : quizzes) {
                                message.append(quiz.getQuizName()).append("\n");
                            }
                            showAlert(message.toString());
                        });
                    })
                    .exceptionally(e -> {
                        Platform.runLater(() -> {
                            showError("Failed to update lesson: " + e.getMessage());
                        });
                        e.printStackTrace();
                        return null;
                    });
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
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

    private void closeDialog() {
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}

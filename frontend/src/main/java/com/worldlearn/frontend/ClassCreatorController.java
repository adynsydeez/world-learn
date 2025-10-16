package com.worldlearn.frontend;

import com.worldlearn.backend.dto.CreateClassRequest;
import com.worldlearn.backend.models.*;
import com.worldlearn.frontend.services.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class ClassCreatorController {
    @FXML private ListView<Lesson> teacherLessonsList;
    @FXML private ListView<Lesson> searchLessonsList;
    @FXML private ListView<Lesson> classLessonsList;
    @FXML private Button addToClassBtn;
    @FXML private Button removeBtn;
    @FXML private Button saveBtn;
    @FXML private Button clearBtn;
    @FXML private Button loadBtn;
    @FXML private TextField searchField;
    @FXML private TextField nameField;
    private String className;

    private ObservableList<Lesson> teacherLessons = FXCollections.observableArrayList();
    private ObservableList<Lesson> searchLessons = FXCollections.observableArrayList();
    private ObservableList<Lesson> classLessons = FXCollections.observableArrayList();

    private FilteredList<Lesson> filteredSearch;

    int teacherId = Session.getCurrentUser().getId();
    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {


        // hook up observable lists to UI
        teacherLessonsList.setItems(teacherLessons);
        searchLessonsList.setItems(searchLessons);
        classLessonsList.setItems(classLessons);

        // wrap your observable list in a FilteredList
        filteredSearch = new FilteredList<>(searchLessons, q -> true);

// bind filtered list to ListView
        searchLessonsList.setItems(filteredSearch);

// add listener on text field
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase();
            filteredSearch.setPredicate(l -> {
                if (filter == null || filter.isEmpty()) {
                    return true; // show all
                }
                return l.getLessonName().toLowerCase().contains(filter);
            });
        });

        teacherLessonsList.setCellFactory(lv -> new ListCell<Lesson>() {
            @Override
            protected void updateItem(Lesson item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLessonName());
            }
        });

        searchLessonsList.setCellFactory(lv -> new ListCell<Lesson>() {
            @Override
            protected void updateItem(Lesson item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLessonName());
            }
        });

        classLessonsList.setCellFactory(lv -> new ListCell<Lesson>() {
            @Override
            protected void updateItem(Lesson item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLessonName());
            }
        });

        // allow multi-selection in teacher list
        teacherLessonsList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        searchLessonsList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        classLessonsList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);


        addToClassBtn.setOnAction(e -> {
            List<Lesson> privateSelected = new ArrayList<>(teacherLessonsList.getSelectionModel().getSelectedItems());
            List<Lesson> publicSelected = new ArrayList<>(searchLessonsList.getSelectionModel().getSelectedItems());
            addToClass(privateSelected);
            addToClass(publicSelected);
        });

        removeBtn.setOnAction(e -> {
            List<Lesson> selected = new ArrayList<>(classLessonsList.getSelectionModel().getSelectedItems());
            removeFromClass(selected);
        });


        saveBtn.setOnAction(e -> {
            saveClass(classLessons);
        });

        clearBtn.setOnAction(e -> {
            clearClass();
        });

        loadBtn.setOnAction(e -> {
            getLessons();
        });
    }

    private void getLessons() {
        apiService.getAllTeacherLessonsAsync(teacherId)
                .thenAccept(teacherLs -> {
                    Platform.runLater(() -> {
                        teacherLessons.setAll(teacherLs);
                    });

                    apiService.getPublicLessonsAsync()
                            .thenAccept(publicLessons -> {
                                List<Lesson> filtered = publicLessons.stream()
                                        .filter(l -> teacherLs.stream()
                                                .noneMatch(tl -> tl.getLessonId() == l.getLessonId()))
                                        .toList();

                                Platform.runLater(() -> {
                                    searchLessons.setAll(filtered);
                                    System.out.println("Fetched " + filtered.size() + " public lesson(s).");
                                });
                            })
                            .exceptionally(e -> {
                                e.printStackTrace();
                                return null;
                            });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void addToClass(List<Lesson> lessons) {
        for (Lesson l : lessons) {
            boolean alreadyAdded = classLessons.stream()
                    .anyMatch(existing -> existing.getLessonId() == l.getLessonId());
            if (!alreadyAdded) {
                classLessons.add(l);
            }
        }
    }

    private void removeFromClass(List<Lesson> lessons) {
        for (Lesson l : lessons) {
            classLessons.remove(l);
        }
    }

    private void clearClass() {
        nameField.clear();
        classLessons.clear();
    }

    private boolean checkInvalidName() {
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            return true;
        } else return nameField.getText().trim().length() < 2;
    }

    private boolean checkInvalidClass() {
        return classLessons.isEmpty();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveClass(List<Lesson> lessons) {
        System.out.println("Attempting Save Class.");
        if (checkInvalidName()) {
            showAlert("Please enter a valid class name");
            return;
        } else if (checkInvalidClass()) {
            showAlert("ERROR: Class Must Contain Lessons");
            return;
        }
        className = nameField.getText().trim();
        try {
            List<Integer> lessonIds = lessons.stream()
                    .map(Lesson::getLessonId)
                    .toList();

            CreateClassRequest classRequest = new CreateClassRequest(
                    className,
                    lessonIds
            );

            apiService.createClassAsync(classRequest)
                    .thenAccept(q -> System.out.println("Class saved:" + className))
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    })
                    .join();
            clearClass();

        } catch (IllegalArgumentException ex){
            showAlert(ex.getMessage());
        }
    }
}

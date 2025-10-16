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
import javafx.stage.Stage;

import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.List;

public class ClassCreatorController {
    private WlClass wlClass;

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
    @FXML private Label creatorTitle;


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
        teacherLessonsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        searchLessonsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        classLessonsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


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

        getLessons();

        if(this.wlClass != null) {
            getLessonQuizzes();
            nameField.setText(this.wlClass.getClassName());
            saveBtn.setOnAction(e -> editClass(classLessons));
            creatorTitle.setText("Edit Class");
        }
        else {
            saveBtn.setOnAction(e -> saveClass(classLessons));
        }
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

    private void getLessonQuizzes() {
        apiService.getClassLessons(this.wlClass.getId())
                .thenAccept(lessons -> Platform.runLater(() -> {
                    System.out.println("Fetched " + lessons.size() + " lessons");
                    classLessons.setAll(lessons);
                }))
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

    private void saveClass(List<Lesson> lessons) {
        System.out.println("Attempting Save Class.");
        if (checkInvalidName()) {
            showError("Please enter a valid class name");
            return;
        } else if (checkInvalidClass()) {
            showError("ERROR: Class Must Contain Lessons");
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
                    .thenAccept(q -> {
                        Platform.runLater(() -> {
                            StringBuilder message = new StringBuilder("Class Created! \n \nName: "
                                    + q.getClassName()
                                    + "\njoin code: " + q.getJoinCode()
                                    + "\n" + "Lessons:\n");
                            for(Lesson lesson : lessons) {
                                message.append(lesson.getLessonName()).append("\n");
                            }
                            showAlert(message.toString());
                            clearClass();
                        });
                    })
                    .exceptionally(e -> {
                        Platform.runLater(() -> {
                            e.printStackTrace();
                            showError("Failed to create class: " + e.getMessage());
                        });
                        return null;
                    });

        } catch (IllegalArgumentException ex){
            showError(ex.getMessage());
        }
    }

    private void editClass(List<Lesson> lessons) {
        System.out.println("Attempting Edit Class.");
        if (checkInvalidName()) {
            showError("Please enter a valid class name");
            return;
        } else if (checkInvalidClass()) {
            showError("ERROR: Class Must Contain Lessons");
            return;
        }
        className = nameField.getText().trim();
        try {
            List<Integer> lessonIds = lessons.stream()
                    .map(Lesson::getLessonId)
                    .toList();

            CreateClassRequest classRequest = new CreateClassRequest(
                    this.wlClass.getId(),
                    className,
                    lessonIds
            );

            apiService.updateClassAsync(classRequest)
                    .thenAccept(q -> {
                        Platform.runLater(() -> {
                            StringBuilder message = new StringBuilder("Class Updated! \n \nName: "
                                    + q.getClassName()
                                    + "\njoin code: " + q.getJoinCode()
                                    + "\n" + "Lessons:\n");
                            System.out.println("Number of lessons: " + lessons.size());
                            for(Lesson lesson : lessons) {
                                message.append(lesson.getLessonName()).append("\n");
                            }
                            showAlert(message.toString());
                            clearClass();
                        });
                    })
                    .exceptionally(e -> {
                        Platform.runLater(() -> {
                            e.printStackTrace();
                            showError("Failed to update class: " + e.getMessage());
                        });
                        return null;
                    });
        } catch (IllegalArgumentException ex){
            showError(ex.getMessage());
        }
    }

    public void setClass(WlClass wlClass) {
        this.wlClass = wlClass;
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
                Platform.runLater(() -> closeDialog());
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

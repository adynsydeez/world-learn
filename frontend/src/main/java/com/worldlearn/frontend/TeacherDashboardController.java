package com.worldlearn.frontend;

import com.worldlearn.backend.models.*;
import com.worldlearn.frontend.services.ApiService;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;

public class TeacherDashboardController {

    private static final Logger log = LoggerFactory.getLogger(TeacherDashboardController.class);
    private User user;
    private Stage stage;
    private AuthClientService auth;
    private final ApiService api = new ApiService();

    @FXML private Button logoutBtn;
    @FXML private Button createLessonBtn;
    @FXML private Button createClassBtn;
    @FXML private Button createQuizBtn;
    @FXML private Button createQuestionBtn;
    @FXML private TilePane classList;
    @FXML private VBox lessonList;
    @FXML private VBox quizList;
    @FXML private VBox questionList;

    // === INITIALIZATION ===
    public void init(Stage stage, AuthClientService auth) {
        this.user = Session.getCurrentUser();
        this.stage = stage;
        this.auth = auth;

        if (user == null) {
            System.err.println("No user logged in.");
            return;
        }

        loadClasses(user);
        loadLessons(user.getId());
        loadQuizzes(user.getId());
        loadQuestions(user.getId());
    }

    @FXML
    private void initialize() {
        // You no longer need the hover label setup since labels are gone from the layout
    }

    // === LOAD CLASSES ===
    private void loadClasses(User user) {
    api.getAllClassesForUser(user)
            .thenAccept(classes -> Platform.runLater(() -> {
                classList.getChildren().clear();

                // Define a set of stable pastel colors
                String[] pastelColors = {
                        "#E8EAF6", "#E3F2FD", "#E8F5E9", "#FFF3E0", "#F3E5F5", "#FBE9E7"
                };

                for (WlClass wlClass : classes) {
                    // Stable color index based on class ID or name hash
                    int colorIndex;
                    if (wlClass.getId() != 0) {
                        colorIndex = wlClass.getId() % pastelColors.length;
                    } else {
                        colorIndex = Math.abs(wlClass.getClassName().hashCode()) % pastelColors.length;
                    }

                    String color = pastelColors[colorIndex];

                    VBox tile = new VBox();
                    tile.setSpacing(8);
                    tile.setPrefSize(250, 150);
                    tile.setPadding(new Insets(14));
                    tile.setStyle(
                            "-fx-background-color:" + color + ";" +
                            "-fx-background-radius:16;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0.2, 0, 2);"
                    );
                    tile.getStyleClass().add("tile"); // enables hover animation

                    Label name = new Label(wlClass.getClassName());
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #2a2a2a;");

                    Label code = new Label("Code: " + wlClass.getJoinCode());
                    code.setStyle("-fx-font-size: 13; -fx-text-fill: #555;");
                    VBox.setMargin(code, new Insets(20, 0, 0, 0));

                    tile.getChildren().addAll(name, code);

                    tile.setOnMouseClicked(e -> editClass(wlClass));

                    classList.getChildren().add(tile);
                }
            }))
            .exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
    }

    // === LOAD LESSONS ===
    private void loadLessons(int teacherId) {
        api.getAllTeacherLessonsAsync(teacherId)
                .thenAccept(lessons -> Platform.runLater(() -> {
                    lessonList.getChildren().clear();
                    for (Lesson lesson : lessons) {
                        Button btn = new Button(lesson.getLessonName());
                        btn.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ccc;");
                        btn.setOnAction(e -> editLesson(lesson));
                        lessonList.getChildren().add(btn);
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    // === LOAD QUIZZES ===
    private void loadQuizzes(int teacherId) {
        api.getAllTeacherQuizzesAsync(teacherId)
                .thenAccept(quizzes -> Platform.runLater(() -> {
                    quizList.getChildren().clear();
                    for (Quiz quiz : quizzes) {
                        Button btn = new Button(quiz.getQuizName());
                        btn.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ccc;");
                        btn.setOnAction(e -> editQuiz(quiz));
                        quizList.getChildren().add(btn);
                    }
                }))
                .exceptionally(e -> { e.printStackTrace(); return null; });
    }

    // === LOAD QUESTIONS ===
    private void loadQuestions(int teacherId) {
        api.getAllTeacherQuestionsAsync(teacherId)
                .thenAccept(questions -> Platform.runLater(() -> {
                    questionList.getChildren().clear();
                    for (Question question : questions) {
                        Button btn = new Button(question.getQuestionName());
                        btn.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ccc;");
                        btn.setOnAction(e -> editQuestion(question));
                        questionList.getChildren().add(btn);
                    }
                }))
                .exceptionally(e -> { e.printStackTrace(); return null; });
    }

    // === EDIT POPUPS ===
    private void editClass(WlClass wlClass) { openPopup("class-creation-view.fxml", "Edit Class", wlClass); }
    private void editLesson(Lesson lesson) { openPopup("lesson-creation-view.fxml", "Edit Lesson", lesson); }
    private void editQuiz(Quiz quiz) { openPopup("quiz-creation-view.fxml", "Edit Quiz", quiz); }
    private void editQuestion(Question question) { openPopup("question-creation-view.fxml", "Edit Question", question); }

    // === POPUP LOGIC ===
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

            popupStage.setOnHidden(e -> {
                loadClasses(this.user);
                loadLessons(this.user.getId());
                loadQuizzes(this.user.getId());
                loadQuestions(this.user.getId());
            });

            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openPopup(String fxmlPath, String title, WlClass wlClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
            fxmlLoader.setControllerFactory(param -> {
                ClassCreatorController controller = new ClassCreatorController();
                controller.setClass(wlClass);
                return controller;
            });

            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Stage parentStage = (Stage) createClassBtn.getScene().getWindow();
            popupStage.initOwner(parentStage);

            popupStage.setOnHidden(e -> loadClasses(this.user));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openPopup(String fxmlPath, String title, Lesson lesson) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
            fxmlLoader.setControllerFactory(param -> {
                LessonCreatorController controller = new LessonCreatorController();
                controller.setLesson(lesson);
                return controller;
            });

            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Stage parentStage = (Stage) createLessonBtn.getScene().getWindow();
            popupStage.initOwner(parentStage);

            popupStage.setOnHidden(e -> loadLessons(this.user.getId()));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openPopup(String fxmlPath, String title, Quiz quiz) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
            fxmlLoader.setControllerFactory(param -> {
                QuizCreatorController controller = new QuizCreatorController();
                controller.setQuiz(quiz);
                return controller;
            });

            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Stage parentStage = (Stage) createQuizBtn.getScene().getWindow();
            popupStage.initOwner(parentStage);

            popupStage.setOnHidden(e -> loadQuizzes(this.user.getId()));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openPopup(String fxmlPath, String title, Question question) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
            fxmlLoader.setControllerFactory(param -> {
                QuestionCreatorController controller = new QuestionCreatorController();
                controller.setQuestion(question);
                return controller;
            });

            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Stage parentStage = (Stage) createQuestionBtn.getScene().getWindow();
            popupStage.initOwner(parentStage);

            popupStage.setOnHidden(e -> loadQuestions(this.user.getId()));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // === LOGOUT HANDLER ===
    @FXML
    private void onLogoutButtonClick() {
        try {
            Session.clearSession();
            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();

            FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("auth-view.fxml"));
            Scene scene = new Scene(fxml.load(), 1280, 720);

            AuthController authController = fxml.getController();
            authController.init((auth != null ? auth : new AuthClientService()), currentStage);

            currentStage.setScene(scene);
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to log out: " + e.getMessage()).showAndWait();
        }
    }

    // === CREATE BUTTON HANDLERS ===
    @FXML protected void onCreateQuestionClick() { openPopup("question-creation-view.fxml", "Create Question"); }
    @FXML protected void onCreateClassClick() { openPopup("class-creation-view.fxml", "Create Class"); }
    @FXML protected void onCreateLessonClick() { openPopup("lesson-creation-view.fxml", "Create Lesson"); }
    @FXML protected void onCreateQuizClick() { openPopup("quiz-creation-view.fxml", "Create Quiz"); }
}


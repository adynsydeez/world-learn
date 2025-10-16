package com.worldlearn.frontend;
import com.worldlearn.backend.models.*;
import com.worldlearn.frontend.services.ApiService;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class TeacherDashboardController {
    private static final Logger log = LoggerFactory.getLogger(TeacherDashboardController.class);
    private User user;
    private Stage stage;
    private AuthClientService auth;
    private ApiService api = new ApiService();

    // pass user,stage to controller
    public void init(Stage stage, AuthClientService auth) {
        User user = Session.getCurrentUser();
        this.user = user;
        if(user == null){
            System.err.println("No user logged in.");
        }

        loadClasses(this.user);
        loadLessons(this.user.getId());
        loadQuizzes(this.user.getId());
        loadQuestions(this.user.getId());
    }

    @FXML private Label lblClasses;
    @FXML private Label lblLessons;
    @FXML private Label lblQuizzes;
    @FXML private Label lblQuestions;
    @FXML private Button logoutBtn;
    @FXML private Button createLessonBtn;
    @FXML private Button createClassBtn;
    @FXML private Button createQuizBtn;
    @FXML private Button createQuestionBtn;
    @FXML private VBox classList;
    @FXML private VBox lessonList;
    @FXML private VBox quizList;
    @FXML private VBox questionList;

    @FXML
    private void initialize() {
        setupHover(lblClasses);
        setupHover(lblLessons);
        setupHover(lblQuizzes);
        setupHover(lblQuestions);
    }

    private void loadClasses(User user) {
        api.getAllClassesForUser(user)
                .thenAccept(classes -> Platform.runLater(() -> {
                    classList.getChildren().clear();
                    for (WlClass wlClass : classes) {
                        Button btn = new Button(wlClass.getClassName() + " - Code: " + wlClass.getJoinCode());
                        btn.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ccc;");
                        btn.setOnAction(e -> editClass(wlClass));
                        classList.getChildren().add(btn);
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

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
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

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
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void editClass(WlClass wlClass) {
        openPopup("class-creation-view.fxml", "Edit Class", wlClass);
    }
    private void editLesson(Lesson lesson) {
        openPopup("lesson-creation-view.fxml", "Edit Lesson", lesson);
    }
    private void editQuiz(Quiz quiz) {
        openPopup("quiz-creation-view.fxml", "Edit Quiz", quiz);
    }
    private void editQuestion(Question question) {
        openPopup("question-creation-view.fxml", "Edit Question", question);
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
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            ClassCreatorController controller = fxmlLoader.getController();
            controller.setClass(wlClass);

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Stage parentStage = (Stage) createClassBtn.getScene().getWindow();
            popupStage.initOwner(parentStage);

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

    @FXML
    private void onLogoutButtonClick() {
        try {
            // 1) Clear session (adjust for your Session API)
            Session.clearSession();

            // 2) Get the current window from the button (NOT the null 'stage' field)
            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();

            // 3) Load auth/login view (check the file name/casing!)
            FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("auth-view.fxml"));
            Scene scene = new Scene(fxml.load(), 1280, 720);

            // 4) Init the auth controller with a real service + the same Stage
            AuthController authController = fxml.getController();
            authController.init((auth != null ? auth : new AuthClientService()), currentStage);

            // 5) Swap scenes
            currentStage.setScene(scene);
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Failed to log out: " + e.getMessage()
            ).showAndWait();
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


package com.worldlearn.frontend;

import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.worldlearn.frontend.services.ApiService;
public class StudentLessonController {
    private User user;
    private Stage stage;
    private AuthClientService auth;
    private Integer lessonId;    // <-- NEW
    private String lessonName;

    @FXML private Button homeButtonLessonPage;
    @FXML private Button profileButtonLessonPage;
    @FXML private Button classesView;
    @FXML private VBox quizListContainer;
    private final com.worldlearn.frontend.services.ApiService api = new com.worldlearn.frontend.services.ApiService();
    public void init(User user, Stage stage, AuthClientService auth) {
        this.user = user;
        this.stage = stage;
        this.auth  = auth;

    }

    private void loadQuizzesFromApi() {
        quizListContainer.getChildren().clear();
        api.getAllQuizzesAsync()
                .thenAccept(quizzes -> javafx.application.Platform.runLater(() -> {
                    quizListContainer.getChildren().clear();
                    for (Quiz q : quizzes) {
                        Button b = new Button(q.getQuizName());
                        b.setMaxWidth(Double.MAX_VALUE);
                        // inline styles to mimic your old “pill” rows
                        b.setStyle("-fx-background-color:#dbdbdb; -fx-background-radius:20; -fx-padding:20; -fx-cursor:hand; -fx-font-size:18; -fx-font-weight:bold;");
                        b.setOnAction(e -> openQuiz(q));
                        quizListContainer.getChildren().add(b);
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private void openQuiz(Quiz q){
        try{
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("student-question-view.fxml"));
            Scene scene = new Scene(loader.load(),800,600);
            StudentQuestionViewController c = loader.getController();
            c.init(user,stage,auth);
            c.setQuiz(q.getQuizID(), q.getQuizName()); // next screen loads questions for this quiz
            stage.setScene(scene);
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    @FXML
    protected void onHomeButtonClickLessonPage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(),800,600);
        StudentDashboardController c = fxml.getController();
        c.init(user,stage,auth);
        stage.setScene(scene);
    }

    @FXML
    protected void onProfileButtonClickLessonPage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(),800,600);
        ProfileController c = fxml.getController();
        c.init(user,stage,auth);
        stage.setScene(scene);
    }

    @FXML
    protected void classesView() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-classes-view.fxml"));
        Scene scene = new Scene(fxml.load(),800,600);
        StudentClassesController c = fxml.getController();
        c.init(user,stage,auth);
        stage.setScene(scene);
    }
    @FXML
    protected void quizView() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);
        StudentLessonController c = fxml.getController();
        c.init(user, stage, auth);
        stage.setScene(scene);
    }
    public void setLesson(int lessonId, String lessonName) {
        this.lessonId = lessonId;
        this.lessonName = lessonName;
        loadQuizzesForLesson();
    }
    private void loadQuizzesForLesson() {
        quizListContainer.getChildren().clear();
        api.getLessonQuizzes(lessonId)   // uses existing ApiService method
                .thenAccept(quizzes -> javafx.application.Platform.runLater(() -> {
                    quizListContainer.getChildren().clear();
                    for (Quiz q : quizzes) {
                        Button b = new Button(q.getQuizName());
                        b.setMaxWidth(Double.MAX_VALUE);
                        b.setStyle("-fx-background-color:#dbdbdb; -fx-background-radius:20; -fx-padding:20; -fx-cursor:hand; -fx-font-size:18; -fx-font-weight:bold;");
                        b.setOnAction(e -> openQuiz(q));
                        quizListContainer.getChildren().add(b);
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }
}

package com.worldlearn.frontend;

import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.frontend.services.ApiService;
import javafx.util.Duration;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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


        if (this.lessonId == null) {
            Lesson saved = Session.instance.getCurrentLesson();
            if (saved != null) {
                setLesson(saved.getLessonId(), saved.getLessonName());
            }
        }
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
                        b.setOnAction(e -> {
                            try {
                                openQuiz(q);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        quizListContainer.getChildren().add(b);
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }


    private void openQuiz(Quiz q) throws IOException {
        Session.instance.setCurrentQuiz(q);  // remember quiz
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("student-question-view.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 720);
        StudentQuestionViewController c = loader.getController();
        c.init(user, stage, auth);
        c.setQuiz(q.getQuizID(), q.getQuizName());
        stage.setScene(scene);
    }

    @FXML
    protected void setProfileButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        ProfileController controller = fxml.getController();
        controller.init(user, stage, auth);

        stage.setScene(scene);
    }
    @FXML
    protected void onHomeButtonClickLessonPage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(),1280,720);
        StudentDashboardController c = fxml.getController();
        c.init(user,stage,auth);
        stage.setScene(scene);
    }

    @FXML
    protected void onProfileButtonClickLessonPage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(),1280,720);
        ProfileController c = fxml.getController();
        c.init(user,stage,auth);
        stage.setScene(scene);
    }

    @FXML
    protected void classesView() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(),1280,720);
        StudentDashboardController c = fxml.getController();
        c.init(Session.instance.getCurrentUser(), stage, auth);
        stage.setScene(scene);
    }
    @FXML
    protected void quizView() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);
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
        api.getLessonQuizzes(lessonId)
                .thenAccept(quizzes -> javafx.application.Platform.runLater(() -> {
                    quizListContainer.getChildren().clear();

                    for (Quiz q : quizzes) {
                        // Row container
                        HBox pill = new HBox(12);
                        pill.getStyleClass().setAll("quiz-pill", "quiz-idle"); // default = idle/blue
                        pill.setMaxWidth(Double.MAX_VALUE);

                        // Left: title
                        Label title = new Label(q.getQuizName());
                        title.getStyleClass().add("quiz-pill-title");

                        // Spacer
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                        // Right: score
                        Label score = new Label("--/--");
                        score.getStyleClass().add("quiz-pill-score");

                        pill.getChildren().addAll(title, spacer, score);

                        // Click → open quiz (your existing behavior)
                        pill.setOnMouseClicked(e -> {
                            try {
                                openQuiz(q);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });

                        quizListContainer.getChildren().add(pill);

                        // Compute & paint the pill based on progress
                        computeQuizProgress(q.getQuizID(), pill, score);
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    /** Fetch questions + answers, compute earned/total, colorize pill and set score. */
    private void computeQuizProgress(int quizId, HBox pill, Label scoreLabel) {
        api.getQuizQuestionsAsync(quizId).thenCompose(questions -> {
            // For each question, ask if the student answered
            java.util.List<java.util.concurrent.CompletableFuture<com.worldlearn.backend.dto.AnswerResponse>> calls =
                    new java.util.ArrayList<>();

            int totalPossible = 0;
            for (com.worldlearn.backend.models.Question q : questions) {
                totalPossible += q.getPointsWorth();
                calls.add(api.getStudentAnswer(q.getQuestionId(), user.getId()));
            }

            int finalTotalPossible = totalPossible;
            return java.util.concurrent.CompletableFuture
                    .allOf(calls.toArray(new java.util.concurrent.CompletableFuture[0]))
                    .thenApply(v -> {
                        int earned = 0;
                        boolean attempted = false;
                        for (var f : calls) {
                            var ans = f.join(); // safe after allOf
                            if (ans != null) {
                                attempted = true;
                                earned += Math.max(0, ans.getPointsEarned());
                            }
                        }
                        return new int[]{earned, finalTotalPossible, attempted ? 1 : 0};
                    });
        }).thenAccept(arr -> javafx.application.Platform.runLater(() -> {
            int earned = arr[0];
            int total = Math.max(arr[1], 0);
            boolean attempted = arr[2] == 1;

            // Score text
            if (!attempted) {
                scoreLabel.setText("--/" + (total == 0 ? "--" : total));
            } else {
                scoreLabel.setText(earned + "/" + total);
            }

            // Color state
            pill.getStyleClass().removeAll("quiz-pass", "quiz-fail", "quiz-idle");
            if (!attempted) {
                pill.getStyleClass().add("quiz-idle");
            } else {
                double pct = (total == 0) ? 0 : (earned * 1.0 / total);
                pill.getStyleClass().add(pct >= 0.5 ? "quiz-pass" : "quiz-fail");
            }
        })).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}

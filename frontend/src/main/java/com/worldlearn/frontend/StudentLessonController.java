package com.worldlearn.frontend;

import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.models.User;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.frontend.services.ApiService;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentLessonController {

    // tracking what’s loaded / active
    private User user;
    private Stage stage;
    private AuthClientService auth;
    private Integer lessonId;
    private String lessonName;

    // fx refs
    @FXML private Button homeButtonLessonPage;
    @FXML private Button profileButtonLessonPage;
    @FXML private Button classesView;
    @FXML private VBox quizListContainer;

    // main api client
    private final ApiService api = new ApiService();

    // runs when we come into this scene
    public void init(Stage stage, AuthClientService auth) {
        this.user = Session.getCurrentUser();
        this.stage = stage;
        this.auth  = auth;

        // if no lesson set yet, grab from session (so it remembers)
        if (this.lessonId == null) {
            Lesson saved = Session.instance.getCurrentLesson();
            if (saved != null) {
                setLesson(saved.getLessonId(), saved.getLessonName());
            }
        }
    }

    // fallback version that pulls all quizzes (used earlier)
    private void loadQuizzesFromApi() {
        quizListContainer.getChildren().clear();
        api.getAllQuizzesAsync()
                .thenAccept(quizzes -> Platform.runLater(() -> {
                    quizListContainer.getChildren().clear();
                    for (Quiz q : quizzes) {
                        Button b = new Button(q.getQuizName());
                        b.setMaxWidth(Double.MAX_VALUE);
                        b.setStyle("-fx-background-color:#dbdbdb; -fx-background-radius:20; -fx-padding:20; "
                                + "-fx-cursor:hand; -fx-font-size:18; -fx-font-weight:bold;");
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

    // jumps to quiz page for whatever quiz you clicked
    public void openQuiz(Quiz q) throws IOException {
        Session.instance.setCurrentQuiz(q); // remember quiz for later
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("student-question-view.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 720);
        StudentQuestionViewController c = loader.getController();
        c.init(stage, auth);
        c.setQuiz(q.getQuizID(), q.getQuizName());
        stage.setScene(scene);
    }

    // quick nav → profile
    @FXML
    protected void setProfileButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        ProfileController controller = fxml.getController();
        controller.init(stage, auth);

        stage.setScene(scene);
    }

    // go back home (dashboard)
    @FXML
    protected void onHomeButtonClickLessonPage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(),1280,720);
        StudentDashboardController c = fxml.getController();
        c.init(stage,auth);
        stage.setScene(scene);
    }

    // nav → profile (duplicate action, just used in sidebar)
    @FXML
    protected void onProfileButtonClickLessonPage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(),1280,720);
        ProfileController c = fxml.getController();
        c.init(stage,auth);
        stage.setScene(scene);
    }

    // back to dashboard showing classes
    @FXML
    protected void classesView() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(),1280,720);
        StudentDashboardController c = fxml.getController();
        c.init(stage, auth);
        stage.setScene(scene);
    }

    // reload this same lesson view
    @FXML
    protected void quizView() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);
        StudentLessonController c = fxml.getController();
        c.init(stage, auth);
        stage.setScene(scene);
    }

    // sets lesson data then loads quizzes for it
    public void setLesson(int lessonId, String lessonName) {
        this.lessonId = lessonId;
        this.lessonName = lessonName;
        loadQuizzesForLesson();
    }

    // pulls lesson’s quizzes + builds each “pill”
    private void loadQuizzesForLesson() {
        quizListContainer.getChildren().clear();
        api.getLessonQuizzes(lessonId)
                .thenAccept(quizzes -> Platform.runLater(() -> {
                    quizListContainer.getChildren().clear();

                    for (Quiz q : quizzes) {
                        HBox pill = new HBox(12);
                        pill.getStyleClass().setAll("quiz-pill", "quiz-idle"); // start idle/blue
                        pill.setMaxWidth(Double.MAX_VALUE);

                        Label title = new Label(q.getQuizName());
                        title.getStyleClass().add("quiz-pill-title");

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                        Label score = new Label("--/--");
                        score.getStyleClass().add("quiz-pill-score");

                        pill.getChildren().addAll(title, spacer, score);

                        // click → open quiz
                        pill.setOnMouseClicked(e -> {
                            try {
                                openQuiz(q);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });

                        quizListContainer.getChildren().add(pill);

                        // update its score + color
                        computeQuizProgress(q.getQuizID(), pill, score);
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    // checks quiz progress + colors the pill (green/red/blue)
    private void computeQuizProgress(int quizId, HBox pill, Label scoreLabel) {
        api.getQuizQuestionsAsync(quizId).thenCompose(questions -> {
            var calls = new java.util.ArrayList<java.util.concurrent.CompletableFuture<com.worldlearn.backend.dto.AnswerResponse>>();
            int totalPossible = 0;

            // loop all questions to check answers
            for (com.worldlearn.backend.models.Question q : questions) {
                totalPossible += q.getPointsWorth();
                calls.add(api.getStudentAnswer(q.getQuestionId(), user.getId()));
            }

            int finalTotalPossible = totalPossible;

            // wait for all calls then sum results
            return java.util.concurrent.CompletableFuture
                    .allOf(calls.toArray(new java.util.concurrent.CompletableFuture[0]))
                    .thenApply(v -> {
                        int earned = 0;
                        boolean attempted = false;
                        for (var f : calls) {
                            var ans = f.join();
                            if (ans != null) {
                                attempted = true;
                                earned += Math.max(0, ans.getPointsEarned());
                            }
                        }
                        return new int[]{earned, finalTotalPossible, attempted ? 1 : 0};
                    });
        }).thenAccept(arr -> Platform.runLater(() -> {
            int earned = arr[0];
            int total = Math.max(arr[1], 0);
            boolean attempted = arr[2] == 1;

            // text inside pill
            if (!attempted) {
                scoreLabel.setText("--/" + (total == 0 ? "--" : total));
            } else {
                scoreLabel.setText(earned + "/" + total);
            }

            // set color style
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

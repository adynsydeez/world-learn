package com.worldlearn.frontend;
import com.worldlearn.backend.models.Question;

import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.services.ApiService;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StudentQuestionViewController {
    private User user;
    private Stage stage;
    private AuthClientService auth;

    private final ApiService api = new ApiService();
    private int quizId;
    private String quizName;
    private List<Question> loadedQuestions = List.of();

    // keep a reference to THIS scene so MC page can come back without reload
    private Scene myScene;

    @FXML private Label lessonTitleLabel;
    @FXML private VBox questionListBox;
    @FXML private Label pointsBadge;
    public void init(Stage stage, AuthClientService auth) {
        this.user = Session.getCurrentUser();
        this.stage = stage;
        this.auth  = auth;
    }

    /** Called by StudentLessonController after loading FXML */
    public void setQuiz(int quizId, String quizName) {
        this.quizId = quizId;
        this.quizName = quizName;
        if (lessonTitleLabel != null) lessonTitleLabel.setText(quizName);

        // remember my own scene for "Back"
        this.myScene = lessonTitleLabel.getScene();

        api.getQuizQuestionsAsync(quizId)
                .thenAccept(qs -> Platform.runLater(() -> renderQuestions(qs)))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private void renderQuestions(List<Question> questions) {
        this.loadedQuestions = questions;
        questionListBox.getChildren().clear();

        int i = 1;
        for (Question q : questions) {
            HBox row = new HBox(10);
            // use style classes instead of big inline style strings
            row.getStyleClass().add("q-row");

            Label lbl = new Label("Question " + i + ": " + q.getPrompt());
            row.getChildren().add(lbl);

            final int questionNumber = i;
            row.setOnMouseClicked(e -> openQuestion(q, questionNumber));
            questionListBox.getChildren().add(row);

            // <-- NEW: decorate row based on student's previous answer
            decorateRowWithStatus(row, q.getQuestionId());

            i++;
        }
        computeAndShowQuizProgress(questions);
    }

    /** Ask backend if student answered this question; color the row. */
    private void decorateRowWithStatus(HBox row, int questionId) {
        api.getStudentAnswer(questionId, this.user.getId())
                .thenAccept(answer -> Platform.runLater(() -> {
                    // First clear any old status classes (if list reloaded)
                    row.getStyleClass().removeAll("q-row--correct", "q-row--wrong");

                    if (answer == null) {
                        // unanswered → leave grey (q-row only)
                        return;
                    }
                    if (answer.isCorrect()) {
                        row.getStyleClass().add("q-row--correct");
                    } else {
                        row.getStyleClass().add("q-row--wrong");
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private void openQuestion(Question q, int questionNumber) {
        try {

            api.getStudentAnswer(q.getQuestionId(), this.user.getId())
                    .thenAccept(previousAnswer -> {
                        javafx.application.Platform.runLater(() -> {
                            if (previousAnswer != null) {
                                // Show their previous answer
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Already Answered");
                                alert.setHeaderText("You've already answered this question!");
                                alert.setContentText(
                                        "Your answer: " + previousAnswer.getGivenAnswer() + "\n" +
                                                "Points earned: " + previousAnswer.getPointsEarned() + "\n" +
                                                "Answered at: " + previousAnswer.getAnsweredAt() + "\n" +
                                                "Result: " + (previousAnswer.isCorrect() ? "Correct ✓" : "Incorrect ✗")
                                );
                                alert.showAndWait();
                            } else {
                                // save the selected question in session
                                Session.setCurrentQuestion(q);

                                FXMLLoader loader = new FXMLLoader(
                                        HelloApplication.class.getResource("multiple-choice-question-view.fxml")
                                );
                                Scene mcScene = null;
                                try {
                                    mcScene = new Scene(loader.load(), 1280, 720);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                MultipleChoiceQuestionController c = loader.getController();
                                List<String> choices = (q.getOptions() == null) ? List.of() : Arrays.asList(q.getOptions());
                                c.init(stage, auth,
                                        questionNumber,
                                        null,
                                        q.getPrompt(),
                                        choices,
                                        q.getAnswer(),
                                        q.getPointsWorth(),
                                        q.getQuestionId(),
                                        null);

                                stage.setScene(mcScene);
                            }
                        });
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    protected void onHomeButtonClickLessonPage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(),1280,720);
        StudentDashboardController c = fxml.getController();
        c.init(stage,auth);
        stage.setScene(scene);
    }

    @FXML
    protected void onProfileButtonClickLessonPage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(),1280,720);
        ProfileController c = fxml.getController();
        c.init(stage,auth);
        stage.setScene(scene);
    }
    @FXML
    protected void quizView() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);
        StudentLessonController c = fxml.getController();
        c.init(stage, auth);
        stage.setScene(scene);
    }


    private void computeAndShowQuizProgress(List<Question> questions) {
        int possible = questions.stream().mapToInt(Question::getPointsWorth).sum();

        var futures = questions.stream()
                .map(q -> api.getStudentAnswer(q.getQuestionId(), user.getId()))
                .toList();

        java.util.concurrent.CompletableFuture
                .allOf(futures.toArray(new java.util.concurrent.CompletableFuture[0]))
                .thenRun(() -> {
                    int[] earnedRef = {0};
                    int[] answeredRef = {0};

                    futures.forEach(f -> {
                        try {
                            var ans = f.get(); // already completed
                            if (ans != null) {
                                answeredRef[0]++;
                                earnedRef[0] += Math.max(0, ans.getPointsEarned());
                            }
                        } catch (Exception ignored) { }
                    });

                    int earned = earnedRef[0];
                    int answered = answeredRef[0];
                    double pct = possible > 0 ? (earned * 100.0 / possible) : 0.0;

                    javafx.application.Platform.runLater(() ->
                            updatePointsBadge(earned, possible, answered, pct));
                })
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private void updatePointsBadge(int earned, int possible, int answered, double pct) {
        String text = String.format("%d / %d (%.0f%%)", earned, possible, pct);
        pointsBadge.setText(text);

        // base style
        String base = "-fx-font-weight:700; -fx-padding:6 10; -fx-background-radius:12;";
        if (answered == 0) {
            // Not started -> grey
            pointsBadge.setStyle(base + "-fx-background-color:#e5e7eb; -fx-text-fill:#111827;");
        } else if (pct >= 50.0) {
            // Green
            pointsBadge.setStyle(base + "-fx-background-color:#c9f3cf; -fx-text-fill:#0b3d1c; "
                    + "-fx-border-color:#58b16b; -fx-border-radius:12; -fx-border-width:1;");
        } else {
            // Red
            pointsBadge.setStyle(base + "-fx-background-color:#ffd6d6; -fx-text-fill:#5b1111; "
                    + "-fx-border-color:#e36b6b; -fx-border-radius:12; -fx-border-width:1;");
        }
    }


}

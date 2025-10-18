package com.worldlearn.frontend;
import com.worldlearn.backend.models.Question;

import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Quiz;
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
import java.util.ArrayList;
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
    private int availableQuestion;

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

    public void init(Stage stage, AuthClientService auth, Quiz q) {
        this.user = Session.getCurrentUser();
        this.stage = stage;
        this.auth = auth;
        this.quizId = q.getQuizID();
        this.quizName = q.getQuizName();

        setQuiz(quizId, quizName);
    }
    /** Called by StudentLessonController after loading FXML */
    public void setQuiz(int quizId, String quizName) {
        this.quizId = quizId;
        this.quizName = quizName;
        if (lessonTitleLabel != null) lessonTitleLabel.setText(quizName);

        // remember my own scene for "Back"
        this.myScene = lessonTitleLabel.getScene();

        getQuestionsAsync(quizId);
    }

    private void getQuestionsAsync(int quizId) {
        api.getQuizQuestionsAsync(quizId)
                .thenAccept(qs -> Platform.runLater(() -> renderQuestions(qs)))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private VBox buildQuestionItem(Question q, int questionNumber) {
        VBox itemBox = new VBox(6);
        itemBox.getStyleClass().add("q-item");

        HBox row = new HBox(10);
        row.getStyleClass().add("q-row");

        Label lbl = new Label("Question " + questionNumber + ": " + q.getPrompt());
        lbl.getStyleClass().add("q-title");
        row.getChildren().add(lbl);

        // Hidden details panel (becomes visible on hover if answered)
        VBox details = new VBox(4);
        details.getStyleClass().add("q-details");
        details.setVisible(false);
        details.setManaged(false);

        itemBox.getChildren().addAll(row, details);

        // Default: clicking the row tries to open the question
        row.setOnMouseClicked(e -> openQuestion(q, questionNumber));

        // Ask backend whether student answered -> decorate + fill details + hover behavior
        api.getStudentAnswer(q.getQuestionId(), this.user.getId())
                .thenAccept(answer -> Platform.runLater(() -> {
                    row.getStyleClass().removeAll("q-row--correct", "q-row--wrong", "q-row--disabled");
                    row.setDisable(false);

                    if (answer == null) {
                        // lock-step availability logic you already had
                        this.availableQuestion = Math.min(
                                this.availableQuestion == 0 ? questionNumber : this.availableQuestion,
                                questionNumber
                        );
                        if (questionNumber != this.availableQuestion) {
                            row.getStyleClass().add("q-row--disabled");
                            row.setDisable(true);
                        }
                        // unanswered: keep click to open MC view
                    } else {
                        // answered: color the row, fill details, disable click
                        if (answer.isCorrect()) {
                            row.getStyleClass().add("q-row--correct");
                        } else {
                            row.getStyleClass().add("q-row--wrong");
                        }

                        // Fill details panel
                        details.getChildren().setAll(
                                new Label("Your answer: " + answer.getGivenAnswer()),
                                new Label("Points earned: " + Math.max(0, answer.getPointsEarned())),
                                new Label("Answered at: " + answer.getAnsweredAt()),
                                new Label("Result: " + (answer.isCorrect() ? "Correct ✓" : "Incorrect ✗"))
                        );

                        // Disable click (so it won’t open MC page)
                        row.setOnMouseClicked(null);

                        // Hover behavior: show details on enter, hide on exit
                        itemBox.setOnMouseEntered(ev -> showDetails(details));
                        itemBox.setOnMouseExited(ev -> hideDetails(details));
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });

        return itemBox;
    }

    // Simple fade helpers
    private void showDetails(VBox details) {
        if (!details.isVisible()) {
            details.setManaged(true);
            details.setVisible(true);
            var fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(120), details);
            fade.setFromValue(0.0); fade.setToValue(1.0); fade.play();
        }
    }

    private void hideDetails(VBox details) {
        if (details.isVisible()) {
            var fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(120), details);
            fade.setFromValue(1.0); fade.setToValue(0.0);
            fade.setOnFinished(e -> { details.setVisible(false); details.setManaged(false); });
            fade.play();
        }
    }

    private void renderQuestions(List<Question> questions) {
        this.loadedQuestions = questions;
        questionListBox.getChildren().clear();
        this.availableQuestion = 0;

        int i = 1;
        for (Question q : questions) {
            VBox item = buildQuestionItem(q, i);
            questionListBox.getChildren().add(item);
            i++;
        }
        computeAndShowQuizProgress(questions);
        Session.setQuestionList(new ArrayList<>(loadedQuestions));
    }

    // Optional: remove the popup path in openQuestion
    public void openQuestion(Question q, int questionNumber) {
        api.getStudentAnswer(q.getQuestionId(), this.user.getId())
                .thenAccept(previousAnswer -> Platform.runLater(() -> {
                    if (previousAnswer != null) {
                        // Already answered: do nothing (hover shows info)
                        return;
                    }
                    // Unanswered -> load MC view (same as your current code)
                    try {
                        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("multiple-choice-question-view.fxml"));
                        Scene mcScene = new Scene(loader.load(), 1280, 720);
                        MultipleChoiceQuestionController c = loader.getController();
                        List<String> choices = (q.getOptions() == null) ? List.of() : Arrays.asList(q.getOptions());
                        c.init(stage, auth, questionNumber, null, q.getPrompt(), choices, q.getAnswer(), q.getPointsWorth(), q.getQuestionId(), null);
                        stage.setScene(mcScene);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
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
        Session.clearQuestionsList();
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(),1280,720);
        ProfileController c = fxml.getController();
        c.init(stage,auth);
        stage.setScene(scene);
    }
    @FXML
    protected void quizView() throws Exception {
        Session.clearQuestionsList();
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

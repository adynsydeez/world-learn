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

    public void init(User user, Stage stage, AuthClientService auth) {
        this.user = user;
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
            row.setStyle("-fx-background-color:#dbdbdb; -fx-background-radius:20; -fx-padding:20; -fx-cursor:hand;");
            row.getChildren().add(new Label("Question " + i + ": " + q.getPrompt()));
            final int questionNumber = i;
            row.setOnMouseClicked(e -> openQuestion(q, questionNumber));
            questionListBox.getChildren().add(row);
            i++;
        }
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
                                    mcScene = new Scene(loader.load(), 800, 600);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                MultipleChoiceQuestionController c = loader.getController();
                                List<String> choices = (q.getOptions() == null) ? List.of() : Arrays.asList(q.getOptions());
                                c.init(user, stage, auth,
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
    protected void quizView() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);
        StudentLessonController c = fxml.getController();
        c.init(user, stage, auth);
        stage.setScene(scene);
    }
}

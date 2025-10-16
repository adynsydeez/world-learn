package com.worldlearn.frontend;

import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.worldlearn.backend.models.Quiz;
import com.worldlearn.frontend.services.ApiService;
import java.util.List;

public class MultipleChoiceQuestionController {
    private User user;
    private Stage stage;
    private AuthClientService auth;
    private ApiService apiService;

    private String correctAnswer;
    private int questionId;

    @FXML private Label headerLabel, questionLabel;
    @FXML private ImageView mapView;
    @FXML private ToggleGroup answersGroup;
    @FXML private ToggleButton aBtn, bBtn, cBtn, dBtn;
    @FXML private Button submitBtn;

    // Inline style snippets (no external CSS)
    private static final String SELECTED_HILITE = "; -fx-border-color:#64b5f6; -fx-border-width:2; -fx-background-insets:0;"
            + " -fx-effect: dropshadow(three-pass-box, rgba(100,181,246,0.5), 6, 0, 0, 0);";
    private static final String CORRECT_HILITE  = "; -fx-border-color:#2e7d32; -fx-border-width:2; -fx-background-color:#c8f7c5;";
    private static final String WRONG_HILITE    = "; -fx-border-color:#b71c1c; -fx-border-width:2; -fx-background-color:#ffd3d3;";

    public void init(Stage stage, AuthClientService auth,
                     int questionNumber, String region, String question,
                     List<String> choices, String correct, int pointsWorth, int questionId, String mapResource) {

        this.user = Session.getCurrentUser();
        this.stage = stage;
        this.auth  = auth;
        this.correctAnswer = correct;
        this.apiService = new ApiService(); // Initialize API service
        this.questionId = questionId;

        headerLabel.setText("Question " + questionNumber + (region != null ? " - " + region : ""));

        questionLabel.setText(question);
        aBtn.setText(choices.get(0));
        bBtn.setText(choices.get(1));
        cBtn.setText(choices.get(2));
        dBtn.setText(choices.get(3));

        // Save each button's BASE style so we can revert easily
        for (Toggle t : answersGroup.getToggles()) {
            ToggleButton b = (ToggleButton) t;
            b.getProperties().put("baseStyle", b.getStyle());
        }

        // Enable submit + show selected highlight
        answersGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            submitBtn.setDisable(newT == null);
            resetToBaseStyles();
            if (newT != null) {
                ToggleButton b = (ToggleButton) newT;
                String base = (String) b.getProperties().getOrDefault("baseStyle", b.getStyle());
                b.setStyle(base + SELECTED_HILITE);
            }
        });

        if (mapResource != null) {
            var is = HelloApplication.class.getResourceAsStream(mapResource);
            if (is != null) mapView.setImage(new Image(is));
        }
    }
    @FXML
    protected void setProfileButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        ProfileController controller = fxml.getController();
        controller.init(stage, auth);

        stage.setScene(scene);
    }
    @FXML
    private void onSubmit() {
        ToggleButton chosen = (ToggleButton) answersGroup.getSelectedToggle();
        if (chosen == null) return;

        String picked = chosen.getText();

        // Lock input immediately
        disableAllButtons();

        // Submit answer via API service
        apiService.submitAnswerAsync(this.questionId, user.getId(), picked)
                .thenAccept(response -> {
                    // Update UI on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        displayResult(chosen, response.isCorrect(), response.getPointsEarned());
                    });
                })
                .exceptionally(ex -> {
                    // Handle error on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        System.err.println("Error submitting answer: " + ex.getMessage());
                        showErrorAlert("Failed to submit answer. Please try again.");
                        enableAllButtons();
                    });
                    return null;
                });
    }

    private void disableAllButtons() {
        aBtn.setDisable(true);
        bBtn.setDisable(true);
        cBtn.setDisable(true);
        dBtn.setDisable(true);
        submitBtn.setDisable(true);
    }

    private void enableAllButtons() {
        aBtn.setDisable(false);
        bBtn.setDisable(false);
        cBtn.setDisable(false);
        dBtn.setDisable(false);
        submitBtn.setDisable(false);
    }

    private void displayResult(ToggleButton chosenButton, boolean isCorrect, int pointsEarned) {
        resetToBaseStyles();

        if (isCorrect) {
            apply(chosenButton, CORRECT_HILITE);
            showSuccessAlert("Correct! You earned " + pointsEarned + " points!");
        } else {
            apply(chosenButton, WRONG_HILITE);
            // Mark the correct option
            for (Toggle t : answersGroup.getToggles()) {
                ToggleButton b = (ToggleButton) t;
                if (b.getText().equalsIgnoreCase(correctAnswer)) {
                    apply(b, CORRECT_HILITE);
                }
            }
            showErrorAlert("Incorrect. You earned " + pointsEarned + " points.");
        }
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Result");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Result");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetToBaseStyles() {
        for (Toggle t : answersGroup.getToggles()) {
            ToggleButton b = (ToggleButton) t;
            String base = (String) b.getProperties().getOrDefault("baseStyle", b.getStyle());
            b.setStyle(base);
        }
    }

    private void apply(ToggleButton b, String extra) {
        String base = (String) b.getProperties().getOrDefault("baseStyle", b.getStyle());
        b.setStyle(base + extra);
    }

    @FXML
    private void onBack() throws Exception {
        Quiz current = Session.instance.getCurrentQuiz();

        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-question-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);
        StudentQuestionViewController c = fxml.getController();
        c.init(stage, auth);

        if (current != null) {
            c.setQuiz(current.getQuizID(), current.getQuizName());
        }

        stage.setScene(scene);
    }
}
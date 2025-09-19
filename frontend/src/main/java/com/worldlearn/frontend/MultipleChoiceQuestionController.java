package com.worldlearn.frontend;

import com.worldlearn.backend.models.User;
import com.worldlearn.backend.services.AuthenticationService;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.List;

public class MultipleChoiceQuestionController {
    private User user;
    private Stage stage;
    private AuthClientService auth;

    private String correctAnswer;

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

    public void init(User user, Stage stage, AuthClientService auth,
                     int questionNumber, String region, String question,
                     List<String> choices, String correct, String mapResource) {

        this.user = user;
        this.stage = stage;
        this.auth  = auth;
        this.correctAnswer = correct;

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
    private void onSubmit() {
        ToggleButton chosen = (ToggleButton) answersGroup.getSelectedToggle();
        if (chosen == null) return;

        String picked = chosen.getText();
        boolean isCorrect = picked.equalsIgnoreCase(correctAnswer);

        // Lock input
        aBtn.setDisable(true); bBtn.setDisable(true); cBtn.setDisable(true); dBtn.setDisable(true);
        submitBtn.setDisable(true);

        resetToBaseStyles();

        if (isCorrect) {
            apply(chosen, CORRECT_HILITE);
        } else {
            apply(chosen, WRONG_HILITE);
            // mark the correct option too
            for (Toggle t : answersGroup.getToggles()) {
                ToggleButton b = (ToggleButton) t;
                if (b.getText().equalsIgnoreCase(correctAnswer)) {
                    apply(b, CORRECT_HILITE);
                }
            }
        }

        // TODO: record attempt + navigate next if required
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
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-question-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);
        StudentQuestionViewController c = fxml.getController();
        c.init(user, stage, auth);
        stage.setScene(scene);
    }
}

package com.worldlearn.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class DashboardController {

    @FXML private Label lblClasses;
    @FXML private Label lblLessons;
    @FXML private Label lblQuizzes;
    @FXML private Label lblQuestions;

    @FXML
    private void initialize() {
        setupHover(lblClasses);
        setupHover(lblLessons);
        setupHover(lblQuizzes);
        setupHover(lblQuestions);
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
}

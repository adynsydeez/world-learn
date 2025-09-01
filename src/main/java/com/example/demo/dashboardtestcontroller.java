package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class dashboardtestcontroller {
    @FXML private Label welcomeLabel;

    private Stage stage;
    private User user;

    public void init(User user, Stage stage) {
        this.user = user;
        this.stage = stage;

        welcomeLabel.setText("Welcome, " + user.getEmail() + " (" + user.getRole() + ")");
    }

    @FXML
    private void onLogout() {
        stage.close(); // or navigate back to login scene
    }
}

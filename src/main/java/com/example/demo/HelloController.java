package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class HelloController {
    @FXML
    public TextField signupUsernameField;

    public PasswordField signupPasswordField;

    @FXML
    private ChoiceBox<String> signupRoleBox;

    @FXML
    public void initialize() {
        signupRoleBox.getItems().addAll("Student", "Teacher");
        signupRoleBox.setValue("Student"); // default selection
    }

    public Label signupErrorLabel;
    public TextField loginUsernameField;
    public PasswordField loginPasswordField;
    public Label loginErrorLabel;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    public void onSignup(ActionEvent actionEvent) {
    }
    @FXML
    public void onLogin(ActionEvent actionEvent) {

    }
}
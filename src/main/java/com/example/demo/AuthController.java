package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class AuthController {

    private AuthenticationService auth;
    private Stage stage;

    @FXML
    public void init(AuthenticationService auth, Stage stage) {
        this.auth = auth;
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        signupRoleBox.getItems().addAll("Student", "Teacher");
        signupRoleBox.setValue("Student"); // default selection
    }

    //sign up fields
    @FXML public PasswordField signupPasswordField;
    @FXML public TextField signupEmailField;
    @FXML public Label signupErrorLabel;

    @FXML private ChoiceBox<String> signupRoleBox;

    @FXML
    public void onSignup(ActionEvent actionEvent) {
        String email = signupEmailField.getText().trim();
        String password = signupPasswordField.getText();
        String roleString = signupRoleBox.getValue();

        try {
            // Convert string -> Role enum
            Role role = roleString.equalsIgnoreCase("Student") ? Role.STUDENT : Role.TEACHER;

            User newUser = auth.signUp(email, password, role);

            signupErrorLabel.setText("Signed up: " + newUser.getEmail() + " (" + newUser.getRole() + ")");
        } catch (IllegalArgumentException ex) {
            signupErrorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            signupErrorLabel.setText("Sign up failed.");
            ex.printStackTrace();
        }
    }

    //log in fields
    @FXML public TextField loginEmailField;
    @FXML public PasswordField loginPasswordField;
    @FXML public Label loginErrorLabel;

    @FXML
    public void onLogin(ActionEvent actionEvent) {
        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText().trim();

        try {
            User newUser = auth.logIn(email, password);
            loginErrorLabel.setText("Logged in: " + newUser.getEmail() + " (" + newUser.getRole() + ")");
        } catch (IllegalArgumentException ex) {
            loginErrorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            loginErrorLabel.setText("Log in Failed");
            ex.printStackTrace();
        }
    }







}
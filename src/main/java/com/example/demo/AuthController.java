package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class AuthController {

    private AuthenticationService auth;
    private Stage stage;

    //passes authentication service to controller
    @FXML
    public void init(AuthenticationService auth, Stage stage) {
        this.auth = auth;
        this.stage = stage;
    }
    //initialises and populates role choice box
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
            //signs up user
            User newUser = auth.signUp(email, password, role);
            //displays message for successful sign up
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

    //log ins user and switches stage to dashboard when successful
    @FXML
    public void onLogin(ActionEvent actionEvent) {
        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText().trim();

        try {
            User newUser = auth.logIn(email, password);
            loginErrorLabel.setText("Logged in: " + newUser.getEmail() + " (" + newUser.getRole() + ")");

            // Always load the same dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Dashboard-test.fxml"));
            Parent root = loader.load();

            // Pass data to dashboard controller
            dashboardtestcontroller controller = loader.getController();
            // Pass user and stage to new controller
            controller.init(newUser, stage);

            // Swap scene
            stage.setScene(new Scene(root, 640, 360));
            stage.show();


            //throws errors if credentials are incorrect or no input
        } catch (IllegalArgumentException ex) {
            loginErrorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            loginErrorLabel.setText("Log in Failed");
            ex.printStackTrace();
        }


    }







}
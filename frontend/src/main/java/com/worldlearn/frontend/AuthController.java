package com.worldlearn.frontend;


import com.worldlearn.backend.models.User;
import com.worldlearn.backend.services.AuthenticationService;
import com.worldlearn.frontend.services.AuthClientService;
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

import java.io.IOException;


public class AuthController {

    private AuthClientService auth;
    private Stage stage;

    //passes authentication service to controller
    @FXML
    public void init(AuthClientService auth, Stage stage) {
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
    @FXML public TextField signupFirstNameField;
    @FXML public TextField signupLastNameField;
    @FXML public PasswordField signupPasswordField;
    @FXML public TextField signupEmailField;
    @FXML public Label signupErrorLabel;
    @FXML private ChoiceBox<String> signupRoleBox;

    @FXML
    public void onSignup(ActionEvent actionEvent) {
        String firstName = signupFirstNameField.getText().trim();
        String lastName = signupLastNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        String password = signupPasswordField.getText();
        String role = signupRoleBox.getValue().toLowerCase();

        try {
            //signs up user - you'll need to update your AuthenticationService.signUp method to accept firstName and lastName
            User newUser = auth.signUp(email, password, role, firstName, lastName);
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

        if (email.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Email and password are required");
            return;
        }

        auth.logIn(email, password)
                .thenAccept(user -> javafx.application.Platform.runLater(() -> {
                    loginErrorLabel.setText("Logged in: " + user.getEmail() + " (" + user.getRole() + ")");
                    Session.setCurrentUser(user);

                    String fxmlFile;
                    switch (user.getRole().toLowerCase()) {
                        case "student" -> fxmlFile = "/com/worldlearn/frontend/student-dashboard-view.fxml";
                        case "teacher" -> fxmlFile = "/com/worldlearn/frontend/teacher-dashboard-view.fxml";
                        default -> {
                            loginErrorLabel.setText("Unknown role: " + user.getRole());
                            return;
                        }
                    }

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                        Parent root = loader.load();

                        Object controller = loader.getController();
                        if (controller instanceof StudentDashboardController studentController) {
                            studentController.init(user, stage, auth);
                        } else if (controller instanceof TeacherDashboardController teacherController) {
                            teacherController.init(stage, auth);
                        }

                        stage.setScene(new Scene(root, 1280, 720));
                        stage.show();
                    } catch (IOException e) {
                        loginErrorLabel.setText("Failed to load dashboard");
                        e.printStackTrace();
                    }
                }))
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() -> {
                        String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        loginErrorLabel.setText("Login failed: " + message);
                    });
                    ex.printStackTrace();
                    return null;
                });
    }

}
package com.worldlearn.frontend;

import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ResetPasswordDialogController {

    @FXML private PasswordField passwordField;
    @FXML private PasswordField retypePasswordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private User currentUser;
    private AuthClientService auth;

    public void initData(User user, AuthClientService authService) {
        this.currentUser = user;
        this.auth = authService;
    }

    @FXML
    public void onUpdatePassword() {
        String password = passwordField.getText();
        String retypePassword = retypePasswordField.getText();

        // Validation
        if (password.isEmpty() || retypePassword.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            successLabel.setText("");
            return;
        }

        if (!password.equals(retypePassword)) {
            errorLabel.setText("Passwords do not match");
            successLabel.setText("");
            return;
        }

        try {
            // Update password
            auth.updatePassword(currentUser.getId(), password);

            successLabel.setText("Password updated successfully!");
            errorLabel.setText("");

            auth.refreshCurrentUser()
                    .thenAccept(updatedUser -> {
                        System.out.println("Session refreshed for: " + updatedUser.getEmail());
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });

            // Close dialog after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> closeDialog());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
            successLabel.setText("");
        } catch (Exception ex) {
            errorLabel.setText("Password update failed.");
            successLabel.setText("");
            ex.printStackTrace();
        }
    }

    @FXML
    public void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.close();
    }
}
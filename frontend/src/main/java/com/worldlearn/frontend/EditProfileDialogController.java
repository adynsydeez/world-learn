package com.worldlearn.frontend;

import com.worldlearn.backend.models.User;
import com.worldlearn.backend.services.AuthenticationService;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditProfileDialogController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private User currentUser;
    private final AuthClientService auth = new AuthClientService();

    public void initData(User user) {
        this.currentUser = user;

        // Populate fields with current user data
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
    }

    @FXML
    public void onSaveChanges() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();

        try {
            // Update user profile (you'll need to implement this method in your service)
            auth.updateProfile(currentUser.getId(), email, currentUser.getPassword(), currentUser.getRole(), firstName, lastName);

            successLabel.setText("Profile updated successfully!");
            errorLabel.setText("");

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
            errorLabel.setText("Update failed.");
            successLabel.setText("");
            ex.printStackTrace();
        }
    }

    @FXML
    public void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
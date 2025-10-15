package com.worldlearn.frontend;

import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProfileController {
    public Button editProfileButton;
    public Button editPasswordButton;
    private User user;
    private Stage stage;
    private AuthClientService auth;

    public void init(User user, Stage stage, AuthClientService auth) {

        this.user = user;
        this.stage = stage;
        this.auth = auth;
        refreshUserProfile();


    }
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;

    @FXML private Button homeButtonProfilePage;

    @FXML
    protected void onHomeButtonClickProfilePage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        StudentDashboardController c = fxml.getController();
        c.init(user, stage, this.auth);   // pass context back

        stage.setScene(scene);
    }
    private void refreshUserProfile() {
        if (nameLabel != null && user != null) {
            String first = safe(user.getFirstName());
            String last  = safe(user.getLastName());
            nameLabel.setText((first + " " + last).trim());
        }
        if (emailLabel != null && user != null) {
            emailLabel.setText("Email: " + safe(user.getEmail()));
        }
    }
    private String safe(String s) { return (s == null) ? "" : s; }
    @FXML
    private void onBack() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        StudentDashboardController c = fxml.getController();
        c.init(user, stage, auth);

        stage.setScene(scene);
    }

    @FXML
    protected void onEditProfileButtonClick() throws Exception {
        // Load the FXML for the edit profile dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-profile-dialog.fxml"));
        Parent root = loader.load();

        // Get the controller and pass current user data
        com.worldlearn.frontend.EditProfileDialogController controller = loader.getController();
        controller.initData(this.user);

        // Create a new stage for the popup
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Profile");
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Makes it a blocking dialog
        dialogStage.initOwner(editProfileButton.getScene().getWindow());
        dialogStage.setScene(new Scene(root));
        dialogStage.setResizable(false);

        // Show and wait for the dialog to close
        dialogStage.showAndWait();

        this.user = this.auth.getCurrentUser();

        refreshUserProfile();
    }

    @FXML
    protected void onResetPasswordButtonClick() throws Exception {
        // Load the FXML for the reset password dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reset-password-dialog.fxml"));
        Parent root = loader.load();

        // Get the controller and pass current user data
        ResetPasswordDialogController controller = loader.getController();
        controller.initData(this.user, this.auth); // ← Are you passing this.auth here?

        // Create a new stage for the popup
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Reset Password");
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(editProfileButton.getScene().getWindow());
        dialogStage.setScene(new javafx.scene.Scene(root));
        dialogStage.setResizable(false);

        // Show and wait for the dialog to close
        dialogStage.showAndWait();
    }


}
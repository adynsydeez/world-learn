package com.worldlearn.frontend;

import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ProfileController {
    private User user;
    private Stage stage;
    private AuthClientService auth;

    public void init(User user, Stage stage, AuthClientService auth) {
        this.user = user;
        this.stage = stage;
        this.auth  = auth;

    }

    @FXML private Button homeButtonProfilePage;

    @FXML
    protected void onHomeButtonClickProfilePage() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);

        StudentDashboardController c = fxml.getController();
        c.init(user, stage, auth);   // pass context back

        stage.setScene(scene);
    }}
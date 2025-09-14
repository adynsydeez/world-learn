package com.worldlearn.frontend;

import com.worldlearn.backend.models.WlClass;
import com.worldlearn.backend.services.AuthenticationService;
import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.services.ApiService;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StudentClassesController {
    private User user;
    private Stage stage;
    private AuthClientService auth;
    private final ApiService apiService = new ApiService();

    @FXML private HBox classBox;

    @FXML private Button homeButton;
    @FXML private Button lessonButton;

    public void init(User user, Stage stage, AuthClientService auth) {
        this.user = user;
        this.stage = stage;
        this.auth  = auth;

        loadClasses();
    }

    private void loadClasses() {
        CompletableFuture<List<WlClass>> future = apiService.getAllClassesForUser(this.user);

        future.thenAccept(classes -> {
            Platform.runLater(() -> {
                // Clear any existing buttons before adding
                classBox.getChildren().clear();

                ToggleGroup group = new ToggleGroup();

                for (WlClass wlClass : classes) {
                    ToggleButton btn = new ToggleButton(wlClass.getClassName());
                    btn.setToggleGroup(group);

                    // Example: add event handler
                    btn.setOnAction(e -> {
                        System.out.println("Selected class: " + wlClass.getClassName());
                    });

                    classBox.getChildren().add(btn);
                }
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @FXML
    protected void onHomeButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);

        StudentDashboardController c = fxml.getController();
        c.init(user, stage, auth);

        stage.setScene(scene);
    }

    @FXML
    protected void onLessonButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);

        StudentLessonController c = fxml.getController();
        c.init(user, stage, auth);

        stage.setScene(scene);
    }
}

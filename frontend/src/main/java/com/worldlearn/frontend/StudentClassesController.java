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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
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
                // Clear existing class buttons but keep space fresh
                classBox.getChildren().clear();

                ToggleGroup group = new ToggleGroup();

                for (WlClass wlClass : classes) {
                    ToggleButton btn = new ToggleButton(wlClass.getClassName());
                    btn.setToggleGroup(group);

                    btn.setOnAction(e -> {
                        System.out.println("Selected class: " + wlClass.getClassName());
                    });

                    classBox.getChildren().add(btn);
                }

                // Add Join Class button at the end
                Button joinBtn = new Button("Join Class");
                joinBtn.setOnAction(e -> onJoinButtonClick());
                classBox.getChildren().add(joinBtn);
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }


    @FXML
    private void onJoinButtonClick() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Join Class");
        dialog.setHeaderText("Enter the join code for the class:");
        dialog.setContentText("Join code:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(code -> {
            try {
                int joinCode = Integer.parseInt(code.trim());
                joinClass(joinCode);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid join code.");
                alert.showAndWait();
            }
        });
    }

    private void joinClass(int joinCode) {
        System.out.println("Joining with userId=" + user.getId() + " joinCode=" + joinCode);
        CompletableFuture.runAsync(() -> {
            try {
                apiService.assignStudentToClass(this.user.getId(), joinCode);

                // Refresh the classes UI on JavaFX thread
                Platform.runLater(this::loadClasses);

            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to join class: " + e.getMessage());
                    alert.showAndWait();
                });
            }
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

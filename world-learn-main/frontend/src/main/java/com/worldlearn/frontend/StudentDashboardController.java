package com.worldlearn.frontend;

import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.WlClass;
import com.worldlearn.backend.services.AuthenticationService;
import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.services.AuthClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.worldlearn.frontend.Session;
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
import com.worldlearn.frontend.services.ApiService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import com.worldlearn.backend.models.Lesson;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class StudentDashboardController {
    private static final Logger log = LoggerFactory.getLogger(StudentDashboardController.class);
    private User user;
    private Stage stage;
    private AuthClientService auth;
    private final ApiService apiService = new ApiService();
    @FXML private HBox classBox;
    @FXML private HBox lessonsRow;
    @FXML private Button homeButton;
    @FXML private Button lessonButton;

    public void init(Stage stage) { init(stage, null); }

    public void init(Stage stage, AuthClientService auth) {
        this.user = Session.getCurrentUser();
        this.stage = stage;
        this.auth  = auth;
        loadClasses();
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + user.getEmail() + "!");
        }
    }

    @FXML private Label welcomeLabel;
    @FXML private Button classesButton;
    @FXML private Button profileButton;
    @FXML private Button teacherButton;
    @FXML private Button logoutButton;

    @FXML
    protected void onClassesButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-classes-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        // pass context into the next controller
        StudentClassesController c = fxml.getController();
        c.init(stage, auth);

        stage.setScene(scene);
    }

    @FXML
    protected void setProfileButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        ProfileController controller = fxml.getController();
        controller.init(stage, auth);

        stage.setScene(scene);
    }

    @FXML
    protected void onLogoutButtonClick() throws Exception {
        user = null;
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("Auth-view.fxml"));
        Scene scene = new Scene(fxml.load(),1280,720);
        AuthController authController = fxml.getController();
        authController.init((auth != null ? auth : new AuthClientService()), stage);
        stage.setScene(scene);
        stage.show();
    }
    private void loadClasses() {
        apiService.getAllClassesForUser(this.user)
                .thenAccept(classes -> Platform.runLater(() -> {
                    classBox.getChildren().clear();

                    ToggleGroup group = new ToggleGroup();
                    for (WlClass wlClass : classes) {
                        ToggleButton btn = new ToggleButton(wlClass.getClassName());
                        btn.setToggleGroup(group);


                        btn.setOnAction(e -> {
                            Integer classId =

                                    wlClass.getId();
                            loadLessonsForClass(classId);
                        });

                        classBox.getChildren().add(btn);
                    }

                    Button joinBtn = new Button("Join Class");
                    joinBtn.setOnAction(e -> onJoinButtonClick());
                    classBox.getChildren().add(joinBtn);
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
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
        Scene scene = new Scene(fxml.load(), 1280, 720);

        StudentDashboardController c = fxml.getController();
        c.init(stage, auth);

        stage.setScene(scene);
    }

    @FXML
    protected void onLessonButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        StudentLessonController c = fxml.getController();
        c.init(stage, auth);

        stage.setScene(scene);
    }
    private void loadLessonsForClass(int classId) {
        apiService.getClassLessons(classId)
                .thenAccept(lessons -> Platform.runLater(() -> {
                    lessonsRow.getChildren().clear();

                    for (Lesson l : lessons) {
                        VBox card = buildLessonCard(l);
                        lessonsRow.getChildren().add(card);
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private VBox buildLessonCard(Lesson l) {
        VBox card = new VBox(6);
        card.setStyle("-fx-padding:10; -fx-background-color:#f3edf9; -fx-background-radius:12; -fx-pref-width:180;");
        card.getChildren().addAll(
                new Label(String.valueOf(l.getLessonId())) {{
                    setStyle("-fx-font-size:60;");
                }},
                new Label(l.getLessonName()) {{
                    setStyle("-fx-font-weight:bold;");
                }},
                new Label("Updated recently") {{
                    setStyle("-fx-text-fill:#666; -fx-font-size:11;");
                }}
        );

        card.setOnMouseClicked(ev -> {
            try {
                openLesson(l);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return card;
    }


    private void openLesson(Lesson l) throws IOException {

        Session.instance.setCurrentLesson(l);  // remember lesson

        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);
        StudentLessonController c = fxml.getController();
        c.init(stage, auth);
        c.setLesson(l.getLessonId(), l.getLessonName());  // triggers load for this lesson
        stage.setScene(scene);
    }
}


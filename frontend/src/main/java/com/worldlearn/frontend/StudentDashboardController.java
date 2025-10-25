package com.worldlearn.frontend;

import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.User;
import com.worldlearn.backend.models.WlClass;
import com.worldlearn.frontend.Session;
import com.worldlearn.frontend.services.ApiService;
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

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class StudentDashboardController {

    private static final Logger log = LoggerFactory.getLogger(StudentDashboardController.class);

    // state we carry around
    private User user;
    private Stage stage;
    private AuthClientService auth;
    private final ApiService apiService = new ApiService();

    // ui refs
    @FXML private HBox classBox;     // chips/tabs for classes
    @FXML private HBox lessonsRow;   // lesson cards container
    @FXML private Button homeButton;
    @FXML private Button lessonButton;
    @FXML private Label welcomeLabel;
    @FXML private Button classesButton;
    @FXML private Button profileButton;
    @FXML private Button teacherButton;
    @FXML private Button logoutButton;

    // entry when we only know the stage
    public void init(Stage stage) { init(stage, null); }

    // entry when we have auth too
    public void init(Stage stage, AuthClientService auth) {
        this.user = Session.getCurrentUser();
        this.stage = stage;
        this.auth  = auth;

        // greet + pull classes
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + user.getEmail() + "!");
        }
        loadClasses();
    }

    // go to classes page (full list view)
    @FXML
    protected void onClassesButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-classes-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        StudentClassesController c = fxml.getController();
        c.init(stage, auth); // pass context

        stage.setScene(scene);
    }

    // go to profile
    @FXML
    protected void setProfileButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        ProfileController controller = fxml.getController();
        controller.init(stage, auth);

        stage.setScene(scene);
    }

    // logout back to auth screen
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

    // pull classes for this user and render the chips
    private void loadClasses() {
        apiService.getAllClassesForUser(this.user)
                .thenAccept(classes -> Platform.runLater(() -> {
                    classBox.getChildren().clear();

                    ToggleGroup group = new ToggleGroup();

                    // build a toggle for each class
                    for (WlClass wlClass : classes) {
                        ToggleButton btn = new ToggleButton(wlClass.getClassName());
                        btn.setToggleGroup(group);
                        btn.getStyleClass().addAll("chip-btn", "class-tab");
                        btn.setOnAction(e -> loadLessonsForClass(wlClass.getId()));
                        classBox.getChildren().add(btn);
                    }

                    // add "join class" action on the end
                    Button joinBtn = new Button("Join Class");
                    joinBtn.getStyleClass().addAll("pill", "success");
                    joinBtn.setOnAction(e -> onJoinButtonClick());
                    classBox.getChildren().add(joinBtn);

                    // auto-select first class so page isn’t empty
                    if (!classes.isEmpty() && !classBox.getChildren().isEmpty()) {
                        ToggleButton firstBtn = (ToggleButton) classBox.getChildren().get(0);
                        firstBtn.setSelected(true);
                        loadLessonsForClass(classes.get(0).getId());
                    }
                }))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    // prompt for a join code and try to join
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

    // actually calls backend to join the class, then refreshes ui
    private void joinClass(int joinCode) {
        log.info("joining class: userId={} joinCode={}", user.getId(), joinCode);

        CompletableFuture.runAsync(() -> {
            try {
                apiService.assignStudentToClass(this.user.getId(), joinCode);
                Platform.runLater(this::loadClasses); // refresh chips
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to join class: " + e.getMessage());
                    alert.showAndWait();
                });
            }
        });
    }

    // reload this dashboard (main/home)
    @FXML
    protected void onHomeButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-dashboard-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        StudentDashboardController c = fxml.getController();
        c.init(stage, auth);

        stage.setScene(scene);
    }

    // open the generic lessons page (without picking a specific lesson)
    @FXML
    protected void onLessonButtonClick() throws Exception {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        StudentLessonController c = fxml.getController();
        c.init(stage, auth);

        stage.setScene(scene);
    }

    // get lessons for the selected class and render cards
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

    // compact little lesson card with color + title
    private VBox buildLessonCard(Lesson l) {
        VBox card = new VBox(6);
        card.getStyleClass().addAll("lesson-card", pickColor(l));

        Label over = new Label("Lesson");
        over.getStyleClass().add("lesson-overline");

        Label title = new Label(l.getLessonName());
        title.getStyleClass().add("lesson-title");

        card.getChildren().addAll(over, title);
        card.setOnMouseClicked(ev -> {
            try {
                openLesson(l);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return card;
    }

    // pick a color class based on id (simple, but looks varied)
    private String pickColor(Lesson l) {
        int mod = Math.abs(l.getLessonId()) % 3;
        return switch (mod) {
            case 0 -> "card-purple";
            case 1 -> "card-amber";
            default -> "card-green";
        };
    }

    // push into lesson page with the chosen lesson pre-selected
    private void openLesson(Lesson l) throws IOException {
        Session.instance.setCurrentLesson(l); // remember which one

        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("student-lesson-view.fxml"));
        Scene scene = new Scene(fxml.load(), 1280, 720);

        StudentLessonController c = fxml.getController();
        c.init(stage, auth);
        c.setLesson(l.getLessonId(), l.getLessonName());

        stage.setScene(scene);
    }
}

package com.worldlearn.frontend;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LessonCreatorController {

    @FXML private TextField lessonTitleField;
    @FXML private TextArea lessonDescriptionField;

    @FXML
    private void goHome(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("teacher-dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    @FXML
    private void handleAddContent(ActionEvent event) {
        System.out.println("Add Content clicked for lesson: " + lessonTitleField.getText());
    }

    @FXML
    private void handleAddQuiz(ActionEvent event) {
        System.out.println("Add Quiz clicked for lesson: " + lessonTitleField.getText());
    }

    @FXML
    private void handleCreateLesson(ActionEvent event) {
        System.out.println("Lesson created: " + lessonTitleField.getText() +
                " | Description: " + lessonDescriptionField.getText());
    }
}
package com.worldlearn.frontend;

import com.worldlearn.backend.dto.CreateClassRequest;
import com.worldlearn.backend.dto.CreateQuizRequest;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.User;
import com.worldlearn.backend.models.WlClass;
import com.worldlearn.frontend.services.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClassCreatorController {
    private WlClass wlClass;


    @FXML private Button homeBtn;
    @FXML private TextField studentEmailField;
    @FXML private ListView<String> studentList;
    @FXML private Button createBtn;

    @FXML
    protected void handleHome() throws IOException {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("teacher-dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    @FXML
    protected void handleAddStudent() {
        String email = studentEmailField.getText();
        if (email != null && !email.isBlank()) {
            studentList.getItems().add(email.trim());
            studentEmailField.clear();
        }
    }

    @FXML
    protected void handleRemoveStudent() {
        String selected = studentList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            studentList.getItems().remove(selected);
        }
    }

    @FXML
    private void createClass() {
        // add createclass function
    }

    public void setClass(WlClass wlClass) {
        this.wlClass = wlClass;
    }

}

package com.worldlearn.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Question.QuestionType;
import com.worldlearn.backend.models.Question.Visibility;
import com.worldlearn.frontend.services.ApiService;

import java.io.IOException;
import java.sql.SQLException;

public class QuestionCreatorController {
    @FXML private Button homeButton;
    @FXML private Button saveBtn;
    @FXML private Button deleteBtn;

    @FXML private ComboBox<Visibility> visibilityCombo;

    @FXML private RadioButton mcqRadio;
    @FXML private RadioButton writtenRadio;
    @FXML private RadioButton mapRadio;

    @FXML private RadioButton correct1;
    @FXML private RadioButton correct2;
    @FXML private RadioButton correct3;
    @FXML private RadioButton correct4;
    @FXML private ToggleGroup correctAnswer;

    @FXML private VBox mcqFields;
    @FXML private VBox writtenFields;
    @FXML private VBox mapFields;

    @FXML private TextField promptField;
    @FXML private TextField pointsField;
    @FXML private TextField option1;
    @FXML private TextField option2;
    @FXML private TextField option3;
    @FXML private TextField option4;

    private QuestionType type;

    private ApiService apiService = new ApiService();

    @FXML
    private void initialize() {
        // group quiz type radios
        ToggleGroup quizTypes = new ToggleGroup();
        mcqRadio.setToggleGroup(quizTypes);
        writtenRadio.setToggleGroup(quizTypes);
        mapRadio.setToggleGroup(quizTypes);

        // group correct answer radios
        correctAnswer = new ToggleGroup();
        correct1.setToggleGroup(correctAnswer);
        correct2.setToggleGroup(correctAnswer);
        correct3.setToggleGroup(correctAnswer);
        correct4.setToggleGroup(correctAnswer);

        correct1.setUserData(option1);
        correct2.setUserData(option2);
        correct3.setUserData(option3);
        correct4.setUserData(option4);

        // hide fields initially
        showFields(null);

        // handle quiz type selection
        quizTypes.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                RadioButton selected = (RadioButton) newVal;
                switch (selected.getId()) {
                    case "mcqRadio":
                        this.type = QuestionType.mcq;
                        showFields(mcqFields);
                        break;
                    case "writtenRadio":
                        this.type = QuestionType.written;
                        showFields(writtenFields);
                        break;
                    case "mapRadio":
                        this.type = QuestionType.map;
                        showFields(mapFields);
                        break;
                }
            }
        });

        // populate visibility dropdown
        visibilityCombo.getItems().setAll(Visibility.values());
        visibilityCombo.setValue(Visibility.PRIVATE);

        // bind buttons
        saveBtn.setOnAction(e -> saveQuestion());
        deleteBtn.setOnAction(e -> clearFields());
    }

    @FXML
    private void showFields(VBox toShow) {
        VBox[] fields = { mcqFields, writtenFields, mapFields };
        for (VBox vbox : fields) {
            boolean show = (toShow != null && vbox == toShow);
            vbox.setVisible(show);
            vbox.setManaged(show);
        }
    }

    private String getAnswer() {
        if (type == QuestionType.mcq) {
            RadioButton selected = (RadioButton) correctAnswer.getSelectedToggle();
            if (selected == null) {
                throw new IllegalArgumentException("Please select the correct answer.");
            }

            // The selected radio button is linked to its TextField
            TextField linkedOption = (TextField) selected.getUserData();
            return linkedOption.getText().trim();
        }
        else if (type == QuestionType.written) {
            return null;
        }
        else if (type == QuestionType.map) {
            return null;
        }

        throw new IllegalArgumentException("No Type Selected");
    }



    private String[] getOptions() {
        TextField[] fields = {option1, option2, option3, option4};
        String[] options = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null || fields[i].getText() == null || fields[i].getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Option " + (i + 1) + " cannot be empty.");
            }
            options[i] = fields[i].getText().trim();
        }
        return options;
    }

    private String getPrompt() {
        if (promptField == null || promptField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a prompt.");
        }
        return promptField.getText().trim();
    }

    private int getPoints() {
        try {
            return Integer.parseInt(pointsField.getText());
        } catch (NumberFormatException e){
            throw new IllegalArgumentException("Points must be a number.");
        }
    }

    private Visibility getVisibility() {
        Visibility selected = visibilityCombo.getValue();
        if (selected == null) {
            throw new IllegalArgumentException("You must select a visibility.");
        }
        return selected;
    }

    private void saveQuestion() {
        String[] options = null;
        if (type == QuestionType.mcq){
            options = getOptions();
        }
        try {
            Question question = new Question(
                    0,
                    getPrompt(),
                    getAnswer(),
                    options,
                    getPrompt(),
                    type,
                    getPoints(),
                    getVisibility()
            );

            apiService.createQuestionAsync(question);


            System.out.println("Question saved!");
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            stage.close();
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        }
    }

    private void clearFields() {
        promptField.clear();
        pointsField.clear();
        option1.clear();
        option2.clear();
        option3.clear();
        option4.clear();
        correctAnswer.selectToggle(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public ApiService getApiService() {
        return apiService;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }
}

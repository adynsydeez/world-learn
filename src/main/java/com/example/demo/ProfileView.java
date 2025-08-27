package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import java.io.IOException;

public class ProfileView {

    @FXML
    private Button homeButtonProfilePage;

    @FXML
    protected void onHomeButtonClickProfilePage() throws IOException {
        Stage stage = (Stage) homeButtonProfilePage.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
    }

}

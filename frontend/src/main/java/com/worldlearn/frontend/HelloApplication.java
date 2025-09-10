package com.worldlearn.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {



    @Override
    public void start(Stage stage) throws Exception {
        // Easiest: package-relative (HelloApplication is in com.worldlearn.frontend)
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        // OR absolute (equivalent):
        // FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/worldlearn/frontend/hello-view.fxml"));

        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("WorldLearn");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
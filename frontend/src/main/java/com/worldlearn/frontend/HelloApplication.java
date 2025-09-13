package com.worldlearn.frontend;

import com.worldlearn.backend.database.AuthenticationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private String mode = "teacher" ; //change to teacher/student for testing while login no implemented


    @Override
    public void start(Stage stage) throws IOException {
        AuthenticationService auth = new AuthenticationService();


        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/worldlearn/frontend/Auth-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        AuthController controller = fxmlLoader.getController();
        controller.init(auth, stage);

        stage.setTitle("World Learn");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
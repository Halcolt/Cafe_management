package com.example.cafe_management;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    // Declare a static primaryStage field
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage; // Assign the primaryStage to the static field
        loadScene("login.fxml");
    }

    public static void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ShowWarning(String AlertName, String AlertTitle){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(AlertTitle);
        alert.setHeaderText(null);
        alert.setContentText(AlertName);
        alert.showAndWait();
    }

    public static void ShowConfirmation(String AlertName, String AlertTitle){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(AlertTitle);
        alert.setHeaderText(null);
        alert.setContentText(AlertName);
        alert.showAndWait();
    }
}

package com.example.cafe_management;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            // Get the controller instance
            LoginController loginController = loader.getController();

            // Connect to the database
            Connection connection = DatabaseUtil.connect();

            // Set the login functionality in the controller
            loginController.setDatabaseConnection(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

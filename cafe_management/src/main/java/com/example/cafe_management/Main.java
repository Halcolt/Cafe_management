package com.example.cafe_management;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    //CheckStock + Import function
    public static void insertStockChange(String username, java.sql.Date changeDate, java.sql.Time changeTime, String ingredient, String unit, float quantity, float price, float currentAmount, float newAmount) throws IOException {
        String insertSql = "INSERT INTO stock_change (username, changedate, changetime, ingredient, unit, old_amount, new_amount, quantity, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = DatabaseUtil.connect();
        try {
            PreparedStatement insertStatement = null;
            if (connection != null) {
                insertStatement = connection.prepareStatement(insertSql);
            }
            insertStatement.setString(1, username);
            insertStatement.setDate(2, changeDate);
            insertStatement.setTime(3, changeTime);
            insertStatement.setString(4, ingredient);
            insertStatement.setString(5, unit);
            insertStatement.setFloat(6, currentAmount);
            insertStatement.setFloat(7, newAmount);
            insertStatement.setFloat(8, quantity);
            insertStatement.setFloat(9, Float.parseFloat(String.valueOf(price)));
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            ShowWarning(String.valueOf(e),null);
        }
    }
}

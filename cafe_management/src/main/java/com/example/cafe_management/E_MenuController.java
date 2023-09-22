package com.example.cafe_management;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class E_MenuController {
    @FXML
    private Button PersonalInfo;

    @FXML
    private Button Importing;

    @FXML
    private Button CreateOrder;

    @FXML
    private Button CheckStock;

    @FXML
    private Button Statistic;

    @FXML
    private Button Logout;

    @FXML
    private Button EditMenu;

    private Connection connection;
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private int currentpermission = LoginController.loggedInUserData.getPermission();
    private Stage currentStage;

    @FXML
    private void initialize() {
        PersonalInfo.setOnAction(event -> {
            // Load the information scene
            loadScene("ChooseUser.fxml");
        });

        Importing.setOnAction(event -> {
            // Load the import scene
            loadScene("Import.fxml");
        });

        CreateOrder.setOnAction(event -> {
            // Load the create order scene
            loadScene("CreateOrder.fxml");
        });

        CheckStock.setOnAction(event -> {
            // Load the check stock scene
            loadScene("CheckStock.fxml");
        });

        EditMenu.setOnAction(event -> {
            if (currentpermission >= 4) {
                showAlert();
            } else {
                // Load the edit menu scene
                loadScene("EditMenu.fxml");
            }
        });

        Statistic.setOnAction(event -> {
            if (currentpermission >= 4) {
                showAlert();
            } else {
                // Load the statistic scene
                loadScene("Statistic.fxml");
            }
        });

        Logout.setOnAction(event -> {
            Platform.exit();
        });
    }

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage newStage = new Stage();

            if (currentStage != null) {
                // Close the current stage before opening the new one
                currentStage.close();
            }

            currentStage = newStage; // Update the current stage reference
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert( ) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Không thể truy cập");
        alert.setContentText("Người dùng không có quyền dùng chức năng này");
        alert.showAndWait();
    }
}

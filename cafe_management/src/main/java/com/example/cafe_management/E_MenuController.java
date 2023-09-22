package com.example.cafe_management;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


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
    @FXML
    private void initialize() {
        PersonalInfo.setOnAction(event -> {
            // Load the information scene
            Main.loadScene("ChooseUser.fxml");
        });

        Importing.setOnAction(event -> {
            // Load the import scene
            Main.loadScene("Import.fxml");
        });

        CreateOrder.setOnAction(event -> {
            // Load the create order scene
            Main.loadScene("CreateOrder.fxml");
        });

        CheckStock.setOnAction(event -> {
            // Load the check stock scene
            Main.loadScene("CheckStock.fxml");
        });

        EditMenu.setOnAction(event -> {
            if (currentpermission >= 4) {
                showAlert();
            } else {
                // Load the edit menu scene
                Main.loadScene("EditMenu.fxml");
            }
        });

        Statistic.setOnAction(event -> {
            if (currentpermission >= 4) {
                showAlert();
            } else {
                // Load the statistic scene
                Main.loadScene("Statistic.fxml");
            }
        });

        Logout.setOnAction(event -> Platform.exit());
    }

    private void showAlert( ) {
        Main.ShowWarning("Người dùng không có quyền dùng chức năng này","Không thể truy cập");
    }
}

package com.example.cafe_management;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChooseUserController {

    @FXML
    private ListView<UserInfo> userListView;

    @FXML
    private Button returnButton;

    @FXML
    private Button chooseButton;

    @FXML
    private Button CreateAccountButton;

    @FXML
    private void initialize() {
        // Retrieve the list of users with higher permissions
        List<UserInfo> usersWithHigherPermission = fetchUsersWithHigherPermission();

        // Add the information of the currently logged-in user to the list
        LoginController.UserData currentUserData = LoginController.loggedInUserData;
        usersWithHigherPermission.add(new UserInfo(currentUserData.getAccountName(), currentUserData.getPermission()));

        // Set a custom cell factory for the userListView
        userListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(UserInfo userInfo, boolean empty) {
                super.updateItem(userInfo, empty);
                if (empty || userInfo == null) {
                    setText(null);
                } else {
                    setText(userInfo.getAccountName() + " (Permission: " + userInfo.getPermissionLabel() + ")");
                }
            }
        });

        // Populate the ListView with the retrieved users
        userListView.getItems().addAll(usersWithHigherPermission);

        returnButton.setOnAction(event -> {
            // Load the E_menu scene
            Main.loadScene("E_Menu.fxml");
        });

        chooseButton.setOnAction(event -> {
            // Get the selected item in the ListView
            UserInfo selectedUserInfo = userListView.getSelectionModel().getSelectedItem();

            if (selectedUserInfo != null) {
                // Load the Information scene and pass the selected user's account name
                loadInformationScene(selectedUserInfo.getAccountName());
            }
        });

        CreateAccountButton.setOnAction(event -> {
            // Load the Create Account scene
            int currentperrmission = LoginController.loggedInUserData.getPermission();
            if (currentperrmission  < 4){
                loadCreateAccountScene();
            } else {
                Alert loginFailAlert = new Alert(Alert.AlertType.ERROR);
                loginFailAlert.setTitle("No Permission");
                loginFailAlert.setContentText("Tài khoản không thể dùng chức năng này");
                loginFailAlert.showAndWait();
            }
        });
    }

    private void loadCreateAccountScene() {
        try {
            // Load the Create Account scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateAccount.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the current window and set the scene
            Stage stage = (Stage) CreateAccountButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadInformationScene(String accountName) {
        try {
            // Load the Information scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Information.fxml"));
            Parent root = loader.load();
            InformationController controller = loader.getController();

            // Pass the selected account name to the InformationController
            controller.initData(accountName);

            Scene scene = new Scene(root);

            // Get the current window and set the scene
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<UserInfo> fetchUsersWithHigherPermission() {
        List<UserInfo> usersWithHigherPermission = new ArrayList<>();

        String currentUsername = LoginController.loggedInUserData.getUsername();
        // Connect to the database using DatabaseUtil
        try (Connection connection = DatabaseUtil.connect()) {
            if (connection != null) {
                // Prepare a SQL statement to retrieve users with higher permissions
                String sql = "SELECT AccountName, permission FROM login WHERE permission > " +
                        "(SELECT permission FROM login WHERE username = ?)";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, currentUsername);
                    ResultSet resultSet = statement.executeQuery();

                    while (resultSet.next()) {
                        String accountName = resultSet.getString("AccountName");
                        int permission = resultSet.getInt("permission");
                        usersWithHigherPermission.add(new UserInfo(accountName, permission));
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return usersWithHigherPermission;
    }

    public class UserInfo {
        private String accountName;
        private int permission;

        public UserInfo(String accountName, int permission) {
            this.accountName = accountName;
            this.permission = permission;
        }

        public String getAccountName() {
            return accountName;
        }

        public String getPermissionLabel() {
            return switch (permission) {
                case 1 -> "Admin";
                case 2 -> "CEO";
                case 3 -> "Manager";
                case 4 -> "Employee";
                default -> "Unknown";
            };
        }
    }
}

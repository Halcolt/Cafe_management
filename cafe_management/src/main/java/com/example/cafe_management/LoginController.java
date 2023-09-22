package com.example.cafe_management;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    private Connection connection;

    // UserData class to store user data
    public static class UserData {
        private int id;
        String accountName;
        String username;
        String password;
        int permission;
        String tel;
        String email;
        String identity;
        String usualSchedule;
        int hourPayment;

        // Constructor
        public UserData(int id, String accountName, String username, String password, int permission,
                        String tel, String email, String identity, String usualSchedule, int hourPayment) {
            this.id = id;
            this.accountName = accountName;
            this.username = username;
            this.password = password;
            this.permission = permission;
            this.tel = tel;
            this.email = email;
            this.identity = identity;
            this.usualSchedule = usualSchedule;
            this.hourPayment = hourPayment;
        }

        // Getters for user data
        public int getId() {
            return id;
        }
        public String getAccountName() {
            return accountName;
        }
        public String getUsername() {
            return username;
        }
        public String getPassword() {
            return password;
        }
        public int getPermission() {
            return permission;
        }
        public String getTel() {
            return tel;
        }
        public String getEmail() {
            return email;
        }
        public String getIdentity() {
            return identity;
        }
        public String getUsualSchedule() {
            return usualSchedule;
        }
        public int getHourPayment() {
            return hourPayment;
        }
    }

    // Store logged-in user data
    public static UserData loggedInUserData;

    public void setDatabaseConnection(Connection connection) {
        this.connection = connection;
    }

    @FXML
    private void loginButtonClicked() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if (connection != null) {
            try {
                // Prepare a SQL statement to check username and password
                String sql = "SELECT * FROM login WHERE username = ? AND Passwords = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, password);

                // Execute the query
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Login successful
                    System.out.println("Login successful!");

                    // Retrieve user data and store it in the loggedInUserData variable
                    int id = resultSet.getInt("ID");
                    String accountName = resultSet.getString("AccountName");
                    int permission = resultSet.getInt("permission");
                    String tel = resultSet.getString("tel");
                    String email = resultSet.getString("email");
                    String identity = resultSet.getString("identity");
                    String usualSchedule = resultSet.getString("usual_schedule");
                    int hourPayment = resultSet.getInt("hour_payment");

                    loggedInUserData = new UserData(id, accountName, username, password,
                            permission, tel, email, identity, usualSchedule, hourPayment);

                    // Load the new FXML file and set it as the new scene
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("E_Menu.fxml"));
                    Parent root = loader.load();
                    E_MenuController controller = loader.getController();
                    Scene scene = new Scene(root);

                    Stage stage = (Stage) usernameTextField.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } else {
                    // Login failed
                    System.out.println("Login failed!");
                    // Add code to display an error message or handle login failure
                    Alert loginFailAlert = new Alert(Alert.AlertType.ERROR);
                    loginFailAlert.setTitle("Login failed");
                    loginFailAlert.setContentText("Please check again username and password");
                    loginFailAlert.showAndWait();
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}

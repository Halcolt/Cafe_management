package com.example.cafe_management;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;// For listen to Enter key from keyboard
import javafx.scene.input.KeyEvent;//

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

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

    @FXML
    private void initialize() {
        //Enter = LoginButton
        // Initialize the Enter key press event handler for usernameTextField
        usernameTextField.setOnKeyPressed(this::handleKeyPress);
        // Initialize the Enter key press event handler for passwordTextField
        passwordTextField.setOnKeyPressed(this::handleKeyPress);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            // The "Enter" key was pressed, so trigger the loginButtonClicked() method
            try {
                loginButtonClicked();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void loginButtonClicked() throws IOException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();


        Connection connection = DatabaseUtil.connect();
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

                    // Load the E_Menu.fxml file
                    Main.loadScene("E_Menu.fxml");
                } else {
                    // Login failed
                    System.out.println("Login failed!");
                    // Add code to display an error message or handle login failure
                    Main.ShowWarning("Please check again username and password","Login failed");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}


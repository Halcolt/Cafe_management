package com.example.cafe_management;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InformationController {

    @FXML
    private TextField AccountNameTextField;

    @FXML
    private TextField PasswordsTextField;

    @FXML
    private TextField permissionTextField;

    @FXML
    private TextField telTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField identityTextField;

    @FXML
    private TextField usual_scheduleTextField;

    @FXML
    private TextField hour_paymentTextField;

    @FXML
    private Button return_button;

    @FXML
    private Button info_modify;

    private String selectedAccountName;

    // Initialize data with the selected account name
    public void initData(String accountName) {
        selectedAccountName = accountName;
        loadUserInfo();
    }

    private void loadUserInfo() {
        // Connect to the database using DatabaseUtil
        Connection connection ;
        try {connection = DatabaseUtil.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (connection != null) {
            // Prepare a SQL statement to retrieve data from the login table
            String sql = "SELECT * FROM login WHERE AccountName = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, selectedAccountName);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Populate the TextField elements with values from the database
                    AccountNameTextField.setText(resultSet.getString("AccountName"));
                    PasswordsTextField.setText(resultSet.getString("Passwords"));
                    int permissionValue = resultSet.getInt("permission");
                    permissionTextField.setText(getPermissionLabel(permissionValue));
                    telTextField.setText(resultSet.getString("tel"));
                    emailTextField.setText(resultSet.getString("email"));
                    identityTextField.setText(resultSet.getString("identity"));
                    usual_scheduleTextField.setText(resultSet.getString("usual_schedule"));
                    hour_paymentTextField.setText(resultSet.getString("hour_payment"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close the connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return_button.setOnAction(event -> {
            // Load the previous scene (Choose User scene)
            Main.loadScene("ChooseUser.fxml");
        });

        info_modify.setOnAction(event -> {
            // Get the logged-in user's permission level
            int userPermission = LoginController.loggedInUserData.getPermission();

            // Check if the user's permission is 3 or lower
            if (userPermission <= 3) {
                // Update the user's information
                updateUserInformation();
            } else {
                showAlert("Permission Denied", "You do not have permission to modify this account.");
            }
        });
    }

    private String getPermissionLabel(int permissionValue) {
        return switch (permissionValue) {
            case 1 -> "Admin";
            case 2 -> "CEO";
            case 3 -> "Manager";
            case 4 -> "Employee";
            default -> "Unknown";
        };
    }

    private void updateUserInformation() {
        // Get the updated information from the TextFields
        String updatedPasswords = PasswordsTextField.getText();
        String updatedTel = telTextField.getText();
        String updatedEmail = emailTextField.getText();
        String updatedIdentity = identityTextField.getText();
        String updatedUsualSchedule = usual_scheduleTextField.getText();
        String updatedHourPayment = hour_paymentTextField.getText();

        // Connect to the database using DatabaseUtil
        Connection connection;
        try {
            connection = DatabaseUtil.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (connection != null) {
            // Prepare a SQL statement to update the user's information
            String sql = "UPDATE login SET Passwords = ?, tel = ?, email = ?, identity = ?, usual_schedule = ?, hour_payment = ? WHERE AccountName = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, updatedPasswords);
                statement.setString(2, updatedTel);
                statement.setString(3, updatedEmail);
                statement.setString(4, updatedIdentity);
                statement.setString(5, updatedUsualSchedule);
                statement.setString(6, updatedHourPayment);
                statement.setString(7, selectedAccountName);

                // Execute the update query
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    showAlert("Update Successful", "Your information has been updated successfully.");
                } else {
                    showAlert("Update Failed", "Failed to update your information. Please try again.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close the connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAlert(String title, String content) {
        Main.ShowConfirmation(content,title);
    }
}

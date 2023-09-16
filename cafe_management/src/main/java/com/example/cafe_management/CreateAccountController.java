package com.example.cafe_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateAccountController {

    public Button return_button;
    public Button CreateAcc;
    @FXML
    private TextField AccountNameTextField;

    @FXML
    private TextField PasswordsTextField;

    @FXML
    private ComboBox<String> permissionComboBox;

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
    private TextField UsernameTextField;

    @FXML
    private void initialize() {
        int CurrentPermission = LoginController.loggedInUserData.getPermission();
        ObservableList<String> permissionItems = FXCollections.observableArrayList();

        if (CurrentPermission <= 1) {
            permissionItems.add("CEO");
        }
        if (CurrentPermission <= 2) {
            permissionItems.add("Manager");
        }
        if (CurrentPermission <= 3) {
            permissionItems.add("Employee");
        }

        permissionComboBox.setItems(permissionItems);
    }


    @FXML
    private void createAccountButtonClicked() {
        // Retrieve input values
        String accountName = AccountNameTextField.getText();
        String password = PasswordsTextField.getText();
        String permissionText = permissionComboBox.getValue();
        String tel = telTextField.getText();
        String email = emailTextField.getText();
        String identity = identityTextField.getText();
        String usualSchedule = usual_scheduleTextField.getText();
        String hourPayment = hour_paymentTextField.getText();
        String username = UsernameTextField.getText();

        // Validate required fields
        if (accountName.isEmpty() || password.isEmpty() || permissionText == null || username.isEmpty()) {
            showAlert("All fields marked with * are required.");
            return;
        }

        int permission = convertPermission(permissionText);

        if (permission == -1) {
            showAlert("Invalid permission value. Please select 'CEO', 'Manager', or 'Employee'.");
            return;
        }

        // Perform database insert
        try (Connection connection = DatabaseUtil.connect()) {
            if (connection != null) {
                // Prepare an SQL statement to insert a new account
                String sql = "INSERT INTO login (AccountName, username, Passwords, permission, tel, email, identity, usual_schedule, hour_payment) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, accountName);
                    statement.setString(2, username);
                    statement.setString(3, password);
                    statement.setInt(4, permission);
                    statement.setString(5, tel);
                    statement.setString(6, email);
                    statement.setString(7, identity);
                    statement.setString(8, usualSchedule);
                    statement.setString(9, hourPayment);

                    // Execute the insert statement
                    int affectedRows = statement.executeUpdate();

                    if (affectedRows > 0) {
                        showAlert("Account created successfully.");
                        clearFields();
                        // Change back to the previous scene (you may need to implement this)
                        returnToPreviousScene();
                    } else {
                        showAlert("Failed to create the account.");
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private int convertPermission(String permissionText) {
        // Convert permission text to an integer value
        return switch (permissionText) {
            case "CEO" -> 2;
            case "Manager" -> 3;
            case "Employee" -> 4;
            default -> -1; // Invalid permission value
        };
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create Account");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        // Clear input fields after successful account creation
        AccountNameTextField.clear();
        PasswordsTextField.clear();
        permissionComboBox.getSelectionModel().clearSelection();
        telTextField.clear();
        emailTextField.clear();
        identityTextField.clear();
        usual_scheduleTextField.clear();
        hour_paymentTextField.clear();
        UsernameTextField.clear();
    }
    @FXML
    private void returnToPreviousScene() {
        Stage stage = (Stage) AccountNameTextField.getScene().getWindow();
        stage.close();
    }
}

package com.example.cafe_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

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
            Main.ShowWarning("Cần điền đầy đủ thông tin trường có dấu (*)","Lỗi");
            return;
        }

        int permission = convertPermission(permissionText);

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
                        Main.ShowConfirmation("Tạo tài khoản thành công",null);
                        // Change back to the previous scene (you may need to implement this)
                        Main.loadScene("ChooseUser.fxml");
                    } else {
                        Main.ShowWarning("Lỗi tạo tài khoản",null);
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

    @FXML
    private void returnToPreviousScene() {
        Main.loadScene("ChooseUser.fxml");
    }
}

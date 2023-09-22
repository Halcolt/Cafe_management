package com.example.cafe_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class NewMaterialController {
    @FXML
    private ComboBox<String> unitComboBox;

    @FXML
    private TextField materialNameTextField;

    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = DatabaseUtil.connect();
            if (connection != null) {
                populateUnitComboBox();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            // Print the stack trace of the underlying exception
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleConfirm() {
        String materialName = materialNameTextField.getText();
        String selectedUnit = unitComboBox.getSelectionModel().getSelectedItem();

        if (materialName != null && !materialName.isEmpty() && selectedUnit != null) {
            try {
                // Insert the data into the stock table
                String insertQuery = "INSERT INTO stock (ingredient, unit) VALUES (?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                    statement.setString(1, materialName);
                    statement.setString(2, selectedUnit);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        // Data inserted successfully
                        System.out.println("Data inserted successfully into stock table.");
                    } else {
                        // Handle insertion failure
                        System.out.println("Failed to insert data into stock table.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Handle missing input
            System.out.println("Please enter both material name and select a unit.");
            // Add Alert
        }
    }

    private void populateUnitComboBox() throws SQLException {
        ObservableList<String> units = FXCollections.observableArrayList();
        // Query the database to retrieve unit data
        String query = "SELECT unit FROM measurement";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String unit = resultSet.getString("unit");
                units.add(unit);
            }
        }
        unitComboBox.setItems(units);
    }

    @FXML
    private void handleReturnButton() {
        // Close the current stage (scene)
        Main.loadScene("Import.fxml");
    }
}

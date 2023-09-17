package com.example.cafe_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditMenuController implements Initializable {

    @FXML
    private ListView<String> menuListView;

    @FXML
    private TextField ItemTextField;

    @FXML
    private TextField PriceTextField;

    @FXML
    private Button returnButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addToMenuButton;

    private Connection connection;

    private ObservableList<String> pendingDeletions = FXCollections.observableArrayList();
    private ObservableList<String> pendingAdditions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            try {
                connection = DatabaseUtil.connect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (connection != null) {
                populateMenuList();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateMenuList() throws SQLException {
        ObservableList<String> menuItems = FXCollections.observableArrayList();

        String query = "SELECT item, price FROM menu";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String item = resultSet.getString("item");
                float price = resultSet.getFloat("price");
                menuItems.add(String.format("%s - %.2f", item, price));
            }
        }

        menuListView.setItems(menuItems);
    }

    @FXML
    private void handleDeleteButton() {
        String selectedItem = menuListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            pendingDeletions.add(selectedItem);
            menuListView.getItems().remove(selectedItem);
        }
    }

    @FXML
    private void handleAddToMenuButton() {
        String itemName = ItemTextField.getText();
        String itemPriceStr = PriceTextField.getText();

        if (!itemName.isEmpty() && !itemPriceStr.isEmpty()) {
            float itemPrice = Float.parseFloat(itemPriceStr);
            String newItem = String.format("%s - %.2f", itemName, itemPrice);
            pendingAdditions.add(newItem);
            menuListView.getItems().add(newItem);
        }
    }

    @FXML
    private void handleConfirmButton() {
        // Delete items from the database
        for (String item : pendingDeletions) {
            String[] parts = item.split(" - ");
            if (parts.length == 2) {
                String itemName = parts[0];
                if (deleteMenuItem(itemName)) {
                    System.out.println("Item deleted successfully.");
                } else {
                    System.out.println("Item deletion failed.");
                    showAlert("Item Deletion Failed", "Failed to delete item: " + itemName, Alert.AlertType.ERROR);
                }
            }
        }
        pendingDeletions.clear();

        // Add new items to the database
        for (String item : pendingAdditions) {
            String[] parts = item.split(" - ");
            if (parts.length == 2) {
                String itemName = parts[0];
                float itemPrice = Float.parseFloat(parts[1]);
                if (addMenuItem(itemName, itemPrice)) {
                    System.out.println("Item added successfully.");
                } else {
                    System.out.println("Item addition failed.");
                    showAlert("Item Addition Failed", "Failed to add item: " + itemName, Alert.AlertType.ERROR);
                }
            }
        }
        pendingAdditions.clear();

        // Show a success alert
        showAlert("Update Successful", "Menu updated successfully.", Alert.AlertType.INFORMATION);
    }

    private boolean deleteMenuItem(String itemName) {
        String deleteQuery = "DELETE FROM menu WHERE item = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setString(1, itemName);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean addMenuItem(String itemName, float itemPrice) {
        String insertQuery = "INSERT INTO menu (item, price) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, itemName);
            statement.setFloat(2, itemPrice);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleReturnButton() {
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

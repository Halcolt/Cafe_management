package com.example.cafe_management;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StatisticController implements Initializable {

    @FXML
    private ListView<String> OrderLists;

    @FXML
    private ListView<String> StockLists;

    @FXML
    private Button ReturnButton;

    private Connection connection;
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = LoginController.connection;

            if (connection != null) {
                populateOrderLists();
                populateStockLists();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateOrderLists() throws SQLException {
        String query = "SELECT * FROM orders";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String username = resultSet.getString("username");
                String orderDate = resultSet.getDate("orderdate").toString();
                String orderTime = resultSet.getTime("ordertime").toString();
                String item = resultSet.getString("item");
                int amount = resultSet.getInt("amount");
                float totalPrice = resultSet.getFloat("total_price");

                String orderInfo = String.format("ID: %d, User: %s, Date: %s, Time: %s, Item: %s, Amount: %d, Total Price: %.2f",
                        id, username, orderDate, orderTime, item, amount, totalPrice);

                OrderLists.getItems().add(orderInfo);
            }
        }
    }

    private void populateStockLists() throws SQLException {
        String query = "SELECT * FROM stock_change";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String changeDate = resultSet.getDate("changedate").toString();
                String changeTime = resultSet.getTime("changetime").toString();
                String ingredient = resultSet.getString("ingredient");
                String unit = resultSet.getString("unit");
                float oldAmount = resultSet.getFloat("old_amount");
                float newAmount = resultSet.getFloat("new_amount");
                float quantity = resultSet.getFloat("quantity");
                float price = resultSet.getFloat("price");

                String stockChangeInfo = String.format("User: %s, Date: %s, Time: %s, Ingredient: %s, Unit: %s, Old Amount: %.2f, New Amount: %.2f, Quantity: %.2f, Price: %.2f",
                        username, changeDate, changeTime, ingredient, unit, oldAmount, newAmount, quantity, price);

                StockLists.getItems().add(stockChangeInfo);
            }
        }
    }

    @FXML
    private void handleReturnButton() {
        // Close the current stage (scene)
        Stage stage = (Stage) ReturnButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMonthlyStatisticButton() {
        LocalDate currentDate = LocalDate.now();
        LocalDate oneMonthAgo = currentDate.minusMonths(1);

        float orderTotal = calculateOrderTotal(oneMonthAgo, currentDate);
        float stockTotal = calculateStockTotal(oneMonthAgo, currentDate);

        showNotification(String.format("Total Order Amount in Last Month: %.2f\nTotal Stock Amount in Last Month: %.2f", orderTotal, stockTotal));
    }

    private float calculateOrderTotal(LocalDate startDate, LocalDate endDate) {
        String query = "SELECT SUM(total_price) FROM orders WHERE orderdate >= ? AND orderdate <= ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(startDate));
            statement.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getFloat(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0f;
    }

    private float calculateStockTotal(LocalDate startDate, LocalDate endDate) {
        String query = "SELECT SUM(price) FROM stock_change WHERE changedate >= ? AND changedate <= ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(startDate));
            statement.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getFloat(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0f;
    }

    private void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Monthly Statistics");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


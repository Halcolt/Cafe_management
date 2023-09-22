package com.example.cafe_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateOrderController {

    @FXML
    private ComboBox<String> itemComboBox;

    @FXML
    private TextField amountTextField;

    @FXML
    private ListView<HBox> cartListView; // Use HBox to include delete buttons

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button deleteButton;

    private Connection connection;
    private ObservableList<HBox> cartItems = FXCollections.observableArrayList();
    private int totalOrderPrice = 0;

    @FXML
    private void initialize() {
        try {
            connection = DatabaseUtil.connect();

            if (connection != null) {
                ObservableList<String> menuItems = FXCollections.observableArrayList(fetchMenuItems());
                itemComboBox.setItems(menuItems);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        // Set up a listener for the delete button
        deleteButton.setOnAction(event -> deleteSelectedItemFromCart());
    }

    @FXML
    private void addToCartButtonClicked() {
        String selectedItem = itemComboBox.getSelectionModel().getSelectedItem();
        int orderAmount = Integer.parseInt(amountTextField.getText());
        int totalPrice = calculateTotalPrice(selectedItem, orderAmount);
        // Create an HBox to hold the cart item and delete button
        HBox cartItemBox = new HBox();
        Label cartItemLabel = new Label(selectedItem + " x" + orderAmount + " - $" + totalPrice);

        // Add the cart item and delete button to the HBox
        cartItemBox.getChildren().addAll(cartItemLabel);

        // Add the HBox to the cartItems list
        cartItems.add(cartItemBox);

        // Update the total order price
        totalOrderPrice += totalPrice;

        // Update the cart ListView
        cartListView.setItems(cartItems);

        // Update the total price label
        updateTotalPriceLabel();
    }

    @FXML
    private void createOrderButtonClicked() {
        String username = LoginController.loggedInUserData.getUsername();
        for (HBox cartItemBox : cartItems) {
            String cartItemText = ((Label) cartItemBox.getChildren().get(0)).getText();
            String[] parts = cartItemText.split(" x| - \\$");
            String selectedItem = parts[0];
            int orderAmount = Integer.parseInt(parts[1]);
            int totalPrice = Integer.parseInt(parts[2]);

            try {
                insertOrder(username, selectedItem, orderAmount, totalPrice);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        cartItems.clear();
        cartListView.setItems(cartItems);
        totalOrderPrice = 0;
        updateTotalPriceLabel();
        Main.ShowConfirmation("Tạo đơn hàng thành công","Xác nhận");
    }


    @FXML
    private void returnButtonClicked() {
        Main.loadScene("E_Menu.fxml" );
    }

    private List<String> fetchMenuItems() throws SQLException {
        List<String> menuItems = new ArrayList<>();
        String sql = "SELECT item FROM menu";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String menuItem = resultSet.getString("item");
                menuItems.add(menuItem);
            }
        }
        return menuItems;
    }

    private int calculateTotalPrice(String item, int amount) {
        int totalPrice = 0;
        try {
            String sql = "SELECT price FROM menu WHERE item = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, item);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int price = resultSet.getInt("price");
                    totalPrice = price * amount;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalPrice;
    }

    private void insertOrder(String username, String item, int amount, int totalPrice) throws SQLException {
        Date currentDate = new Date();
        Time currentTime = new Time(currentDate.getTime());
        String sql = "INSERT INTO orders (username, orderdate, ordertime, item, amount, total_price) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setDate(2, new java.sql.Date(currentDate.getTime()));
            statement.setTime(3, currentTime);
            statement.setString(4, item);
            statement.setInt(5, amount);
            statement.setInt(6, totalPrice);

            statement.executeUpdate();
        }
    }

    private void deleteSelectedItemFromCart() {
        HBox selectedCartItemBox = cartListView.getSelectionModel().getSelectedItem();

        if (selectedCartItemBox != null) {
            cartItems.remove(selectedCartItemBox);
            updateTotalPriceLabel();
        }
    }

    private void updateTotalPriceLabel() {
        totalOrderPrice = cartItems.stream()
                .mapToInt(itemBox -> {
                    String cartItemText = ((Label) itemBox.getChildren().get(0)).getText();
                    String[] parts = cartItemText.split(" - \\$");
                    return Integer.parseInt(parts[1]);
                })
                .sum();

        totalPriceLabel.setText(String.format("%d", totalOrderPrice));
    }
}

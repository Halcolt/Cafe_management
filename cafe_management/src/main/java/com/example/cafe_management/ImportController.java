package com.example.cafe_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImportController {

    @FXML
    private ComboBox<String> ItemComboBox;

    @FXML
    private TextField unitTextfield;

    @FXML
    private TextField amountTextField;

    @FXML
    private ListView<String> cartListView;

    @FXML
    private TextField PriceTextField;

    private Connection connection;
    private String username;

    @FXML
    private void initialize() {
        try {
            connection = DatabaseUtil.connect();
            if (connection != null) {
                setupComboBox();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the username (You need to replace this with your actual way of getting the username)
        username = LoginController.loggedInUserData.getUsername();
    }

    private void setupComboBox() {
        ObservableList<String> stockIngredients = FXCollections.observableArrayList(fetchIngredientStock());
        ItemComboBox.setItems(stockIngredients);

        ItemComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String unit = fetchUnitForItem(newValue);
                unitTextfield.setText(unit);
            }
        });
    }

    private List<String> fetchIngredientStock() {
        List<String> stockIngredients = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT ingredient FROM stock");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String menuItem = resultSet.getString("ingredient");
                stockIngredients.add(menuItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockIngredients;
    }

    private String fetchUnitForItem(String item) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT unit FROM stock WHERE ingredient = ?")) {
            statement.setString(1, item);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("unit");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @FXML
    private void returnButtonClicked() {
       Main.loadScene("E_Menu.fxml");
    }

    @FXML
    private void AddToCart() {
        String selectedItem = ItemComboBox.getValue();
        String unit = unitTextfield.getText();
        float quantity = Float.parseFloat(amountTextField.getText());
        int price = Integer.parseInt(PriceTextField.getText());

        if (selectedItem != null && !selectedItem.isEmpty() && quantity > 0 && price > 0) {
            String cartItem = selectedItem + " - " + quantity + " " + unit + " - Price: " + price;
            cartListView.getItems().add(cartItem);
            clearInputFields();
        } else {
            showInputErrorAlert();
        }
    }

    private void clearInputFields() {
        ItemComboBox.setValue(null);
        unitTextfield.clear();
        amountTextField.clear();
        PriceTextField.clear();
    }

    private void showInputErrorAlert() {
        Main.ShowWarning("Vui lòng điền đầy đủ thông tin nhập hàng",null);
    }

    @FXML
    private void handleDelete() {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            cartListView.getItems().remove(selectedIndex);
        }
    }

    @FXML
    private void handleConfirm() throws IOException {
        java.util.Date currentDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
        java.sql.Time sqlTime = new java.sql.Time(currentDate.getTime());

        for (String cartItem : cartListView.getItems()) {
            String[] parts = cartItem.split(" - ");
            if (parts.length == 3) {
                String ingredient = parts[0];
                String quantityUnit = parts[1];
                float price = Float.parseFloat(parts[2].replace("Price: ", ""));
                String[] quantityParts = quantityUnit.split(" ");
                if (quantityParts.length == 2) {
                    String unit = quantityParts[1];
                    float quantity = Float.parseFloat(quantityParts[0]);
                    float currentAmount = fetchCurrentAmount(ingredient);
                    float newAmount = currentAmount + quantity;
                    updateStockTable(ingredient,  newAmount);
                    Main.insertStockChange(username, sqlDate, sqlTime, ingredient, unit, quantity, price, currentAmount, newAmount);
                }
            }
        }
        cartListView.getItems().clear();
        showConfirmationAlert();
    }

    @FXML
    private void NewMaterial() {
        int currentperrmision = LoginController.loggedInUserData.getPermission();
        // Load the NewMaterial.fxml file and create a new stage for it
        if (currentperrmision < 4) {
            Main.loadScene("NewMaterial.fxml");
        } else {
            Main.ShowWarning("Tài khoản không thể dùng chức năng này","No Permission");
        }
    }

    private void updateStockTable(String ingredient,  float newAmount) {
        String updateSql = "UPDATE stock SET amount = ? WHERE ingredient = ?";
        try {
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setFloat(1, newAmount);
            updateStatement.setString(2, ingredient);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private float fetchCurrentAmount(String ingredient) {
        float currentAmount = 0;
        try {
            PreparedStatement fetchAmountStatement = connection.prepareStatement("SELECT amount FROM stock WHERE ingredient = ?");
            fetchAmountStatement.setString(1, ingredient);
            ResultSet amountResultSet = fetchAmountStatement.executeQuery();
            if (amountResultSet.next()) {
                currentAmount = amountResultSet.getFloat("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentAmount;
    }

    private void showConfirmationAlert() {
        Main.ShowConfirmation("Nhập hàng đã được xác nhận và cập nhật.",null);
    }
}

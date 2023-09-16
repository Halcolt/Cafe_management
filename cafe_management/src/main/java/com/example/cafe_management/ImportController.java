package com.example.cafe_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
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

    private PreparedStatement fetchAmountStatement;
    private PreparedStatement updateStatement;
    private PreparedStatement insertStatement;

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
        Stage stage = (Stage) ItemComboBox.getScene().getWindow();
        stage.close();
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
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Lỗi");
        errorAlert.setContentText("Vui lòng điền đầy đủ thông tin nhập hàng");
        errorAlert.showAndWait();
    }

    @FXML
    private void handleDelete() {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            cartListView.getItems().remove(selectedIndex);
        }
    }

    private void showNoItemSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText("Vui lòng chọn một mục để xóa.");
        alert.showAndWait();
    }

    @FXML
    private void handleConfirm() {
        java.util.Date currentDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
        java.sql.Time sqlTime = new java.sql.Time(currentDate.getTime());

        for (String cartItem : cartListView.getItems()) {
            String[] parts = cartItem.split(" - ");
            if (parts.length == 3) {
                String ingredient = parts[0];
                String quantityUnit = parts[1];
                String price = parts[2].replace("Price: ", "");
                String[] quantityParts = quantityUnit.split(" ");
                if (quantityParts.length == 2) {
                    String unit = quantityParts[1];
                    float quantity = Float.parseFloat(quantityParts[0]);
                    float currentAmount = fetchCurrentAmount(ingredient);
                    float newAmount = currentAmount + quantity;
                    updateStockTable(ingredient,  newAmount);
                    insertStockChange(username, sqlDate, sqlTime, ingredient, unit, quantity, price, currentAmount, newAmount);
                }
            }
        }
        cartListView.getItems().clear();
        showConfirmationAlert();
    }

    private void updateStockTable(String ingredient,  float newAmount) {
        String updateSql = "UPDATE stock SET amount = ? WHERE ingredient = ?";
        try {
            updateStatement = connection.prepareStatement(updateSql);
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
            fetchAmountStatement = connection.prepareStatement("SELECT amount FROM stock WHERE ingredient = ?");
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

    private void insertStockChange(String username, java.sql.Date changeDate, java.sql.Time changeTime, String ingredient, String unit, float quantity, String price, float currentAmount, float newAmount) {
        String insertSql = "INSERT INTO stock_change (username, changedate, changetime, ingredient, unit, old_amount, new_amount, quantity, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setString(1, username);
            insertStatement.setDate(2, changeDate);
            insertStatement.setTime(3, changeTime);
            insertStatement.setString(4, ingredient);
            insertStatement.setString(5, unit);
            insertStatement.setFloat(6, currentAmount);
            insertStatement.setFloat(7, newAmount);
            insertStatement.setFloat(8, quantity);
            insertStatement.setFloat(9, Float.parseFloat(price));
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showConfirmationAlert() {
        Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
        confirmationAlert.setTitle("Xác nhận");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Nhập hàng đã được xác nhận và cập nhật.");
        confirmationAlert.showAndWait();
    }

    private void showUsernameErrorAlert() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Lỗi");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText("Không thể xác định tên người dùng.");
        errorAlert.showAndWait();
    }
}

package com.example.cafe_management;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.ResultSet;
import java.sql.Statement;

public class CheckStockController {
    @FXML
    private Label StockLabel;

    @FXML
    private void initialize(){
            //Connection connection = DatabaseUtil.connect();
            //if (connection != NULL){
                Statement statement;
                ResultSet resultSet;
                //try {
                //    statement = connection.createStatement();
                //    resultSet = statement.executeQuery("Select * from stock");
                //}
            //}


    }
}

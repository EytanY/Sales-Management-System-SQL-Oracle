package com.example.demo;


import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WarehouseController extends Controller implements Initializable {
    
    public TextField warehouseNameTF;
    public Button addNewWarehouseButton;
    public TextField searchWarehouseTF;
    public Button searchWarehouseButton;
    public Button showAllWarehousesButton;
    public Label resultLabel;
    public Button showStockButton;
    public Button searchProductButton;
    public TextField amountTF;
    public ChoiceBox<String> productIDChoice;
    public ChoiceBox<String> warehouseIDChoice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<String> itemsIDStr = getColFromTable(ITEM_ID, ITEM_TABLE);
        productIDChoice.getItems().addAll(itemsIDStr);
        if(itemsIDStr.size() > 0)
            productIDChoice.setValue(itemsIDStr.get(0));

        ArrayList<String> warehouseIDStr = getColFromTable(WAREHOUSE_ID, WAREHOUSE_TABLE);
        warehouseIDChoice.getItems().addAll(warehouseIDStr);
        if(warehouseIDStr.size() > 0)
            warehouseIDChoice.setValue(warehouseIDStr.get(0));
    }

    public void onAddNewWarehouseButtonClick() {
        try{
            Connection connection = new SQL().getConnection();
            String sql = String.format("INSERT INTO %s(%s)VALUES(?)", WAREHOUSE_TABLE, WAREHOUSE_NAME);
            PreparedStatement statement = connection.prepareStatement(sql);
            String warehouseName = warehouseNameTF.getText();
            if(validSyntax(warehouseName))
            {
                statement.setString(1, warehouseName);
                statement.executeUpdate();
                resultLabel.setText("SUCCESSES");
            }
            else {
                resultLabel.setText("Invalid Syntax!");
            }

        }catch (Exception exception){
            resultLabel.setText("ERROR!");
        }
    }

    public void onSearchWarehouseButtonClick() {
        String warehouseName = searchWarehouseTF.getText();

        if(validSyntax(warehouseName)){
            try{
                Connection connection = new SQL().getConnection();
                String sql = String.format("SELECT * FROM %s WHERE %s = ? ", WAREHOUSE_TABLE, WAREHOUSE_NAME);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, warehouseName);
                ResultSet rs = statement.executeQuery();
                StringBuilder sb = new StringBuilder();
                while (rs.next()){
                    sb.append("Warehouse ID:").append(rs.getString(WAREHOUSE_ID));
                    sb.append(" ,Warehouse's Name:").append(rs.getString(WAREHOUSE_NAME)).append("\n");
                }
                resultLabel.setText(sb.toString());
                if(sb.toString().equals(""))
                    resultLabel.setText("Not Found");
                rs.close();
                statement.close();
                connection.close();
            }
            catch (Exception exception){
                resultLabel.setText("Invalid Connection");

            }
        }
        else{
                resultLabel.setText("Invalid Syntax");
        }
    }

    public void onShowAllWarehousesButtonClick() {
        try{
            Connection connection = new SQL().getConnection();
            String sql = String.format("SELECT * FROM %s", WAREHOUSE_TABLE);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Warehouse ID:").append(rs.getString(WAREHOUSE_ID));
                sb.append(" ,Warehouse's Name:").append(rs.getString(WAREHOUSE_NAME)).append("\n");
            }
            resultLabel.setText(sb.toString());
            if(sb.toString().equals(""))
                resultLabel.setText("Not Found");
            rs.close();
            statement.close();
            connection.close();
        }
        catch (Exception exception){
            resultLabel.setText("Invalid Connection");

        }
    }

    public void onShowStockButtonClick() {
        try{
            Connection connection = new SQL().getConnection();
            String sql = String.format("SELECT  %s.%s, %s.%s ,NVL(sum(%s), 0) as amount " +
                                       "FROM %s LEFT  JOIN %s " +
                                        "ON %s.%s = %s.%s " +
                                        "GROUP BY %s.%s, %s.%s " +
                                        "ORDER BY amount DESC" , ITEM_TABLE, ITEM_ID, ITEM_TABLE,ITEM_PRICE, STOCK_AMOUNT,
                    ITEM_TABLE, STOCK_TABLE, STOCK_TABLE , STOCK_ITEM_ID, ITEM_TABLE, ITEM_ID,ITEM_TABLE, ITEM_ID, ITEM_TABLE, ITEM_PRICE);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Item ID:").append(rs.getInt(ITEM_ID));
                sb.append(" ,Price:").append(rs.getFloat(ITEM_PRICE));
                sb.append(" ,Total Amount:").append(rs.getInt(STOCK_AMOUNT)).append("\n");
            }
            if(sb.toString().equals("")){
                resultLabel.setText("Empty");
            }
            else {
                resultLabel.setText(sb.toString());
            }
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public void onAddAmountOfProductsToStockButtonClick() {
        try{
            int amount = Integer.parseInt(amountTF.getText());
            if(amount < 1){
                resultLabel.setText("Amount can not be negative");
                return;
            }
            Connection connection = new SQL().getConnection();
            String sqlCheck = String.format("INSERT INTO %s(%s, %s, %s) " +
                                        "SELECT ?, ? , 0 " +
                                        "  FROM DUAL " +
                                       " WHERE NOT EXISTS (SELECT NULL  FROM %s  WHERE  %s = ? AND  %s = ?)",
                    STOCK_TABLE, STOCK_WAREHOUSE_ID, STOCK_ITEM_ID, STOCK_AMOUNT, STOCK_TABLE, STOCK_WAREHOUSE_ID, STOCK_ITEM_ID) ;
            PreparedStatement checkItemInStockStatement = connection.prepareStatement(sqlCheck);
            int warehouseID = Integer.parseInt(warehouseIDChoice.getValue());
            int productID = Integer.parseInt(productIDChoice.getValue());
            checkItemInStockStatement.setInt(1, warehouseID);
            checkItemInStockStatement.setInt(2, productID);
            checkItemInStockStatement.setInt(3, warehouseID);
            checkItemInStockStatement.setInt(4, productID);
            checkItemInStockStatement.executeUpdate();

            String sql = String.format("UPDATE %s SET %s = %s + ? WHERE %s = ? AND %s = ?"
                    ,STOCK_TABLE, STOCK_AMOUNT,STOCK_AMOUNT ,STOCK_WAREHOUSE_ID, STOCK_ITEM_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, amount);
            statement.setInt(2, warehouseID);
            statement.setInt(3,productID);
            statement.executeUpdate();
            resultLabel.setText("SUCCESSES");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

}

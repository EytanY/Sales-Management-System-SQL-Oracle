package com.example.demo;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WarehouseController extends Controller {
    
    public TextField warehouseNameTF;
    public Button addNewWarehouseButton;
    public TextField searchWarehouseTF;
    public Button searchWarehouseButton;
    public Button showAllWarehousesButton;
    public Label resultLabel;
    public Button showStockButton;
    public TextField productIDTF;
    public Button searchProductButton;
    public TextField amountTF;
    public TextField warehouseIDTF;

    public void onAddNewWarehouseButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = "INSERT INTO WAREHOUSES(warehouses_name)VALUES(?)";
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

    public void onSearchWarehouseButtonClick(ActionEvent actionEvent) {
        String warehouseName = searchWarehouseTF.getText();

        if(validSyntax(warehouseName)){
            try{
                Connection connection = new SQL().getConnection();
                String sql = "SELECT * FROM WAREHOUSES WHERE warehouses_name = ? ";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, warehouseName);
                ResultSet rs = statement.executeQuery();
                StringBuilder sb = new StringBuilder();
                while (rs.next()){
                    sb.append("Warehouse ID:").append(rs.getString("warehouses_id"));
                    sb.append(" ,Warehouse's Name:").append(rs.getString("warehouses_name")).append("\n");
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

    public void onShowAllWarehousesButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = "SELECT * FROM WAREHOUSES";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Warehouse ID:").append(rs.getString("warehouses_id"));
                sb.append(" ,Warehouse's Name:").append(rs.getString("warehouses_name")).append("\n");
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

    public void onShowStockButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = "SELECT  items.item_id, items.price ,NVL(sum(amount), 0) as amount " +
                    "FROM items LEFT  JOIN stock " +
                    "ON stock.item_id = items.item_id " +
                    "GROUP BY items.item_id, items.price " +
                    "ORDER BY amount DESC";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Item ID:").append(rs.getInt("item_id"));
                sb.append(" ,Price:").append(rs.getFloat("price"));
                sb.append(" ,Total Amount:").append(rs.getInt("amount")).append("\n");
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

    public void onAddAmountOfProductsToStockButtonClick(ActionEvent actionEvent) {
        try{

            Connection connection = new SQL().getConnection();
            String sqlCheck = "INSERT INTO stock(warehouse_id, item_id, amount) " +
                    "SELECT ?, ? , 0" +
                    "  FROM dual " +
                    " WHERE NOT EXISTS (SELECT NULL  FROM stock  WHERE warehouse_id = ? AND  item_id = ?)" ;
            PreparedStatement checkItemInStockStatement = connection.prepareStatement(sqlCheck);
            checkItemInStockStatement.setInt(1, Integer.parseInt(warehouseIDTF.getText()));
            checkItemInStockStatement.setInt(2, Integer.parseInt(productIDTF.getText()));
            checkItemInStockStatement.setInt(3, Integer.parseInt(warehouseIDTF.getText()));
            checkItemInStockStatement.setInt(4, Integer.parseInt(productIDTF.getText()));
            checkItemInStockStatement.executeUpdate();

            String sql = "UPDATE stock SET amount = amount + ? WHERE warehouse_id = ? AND item_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(amountTF.getText()));
            statement.setInt(2, Integer.parseInt(warehouseIDTF.getText()));
            statement.setInt(3, Integer.parseInt(productIDTF.getText()));
            statement.executeUpdate();
            resultLabel.setText("SUCCESSES");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
}

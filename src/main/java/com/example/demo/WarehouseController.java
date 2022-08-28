package com.example.demo;


import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
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
            String sql = "{CALL add_warehouse(?, ?)}";
            CallableStatement statement = connection.prepareCall(sql);
            statement.setString(1, warehouseNameTF.getText());
            statement.registerOutParameter(2,Types.INTEGER);
            statement.execute();
            Alert message = new Alert(Alert.AlertType.INFORMATION);
            String messageStr = "SUCCESS! New Warehouse ID:" + statement.getInt(2);
            message.setContentText(messageStr);
            message.show();
            statement.close();
            onReturnToMenuButtonClick();


        }catch (Exception exception){
            resultLabel.setText("ERROR!");
        }
    }

    public void onSearchWarehouseButtonClick() {
        String warehouseName = searchWarehouseTF.getText();

        if(validSyntax(warehouseName)){
            try{
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
        }
        catch (Exception exception){
            resultLabel.setText("Invalid Connection");

        }
    }

    public void onShowStockButtonClick() {
        try{
            String sql = "select * from items_in_stock";
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
            if(amount < 0){
                resultLabel.setText("Amount can not be negative");
                return;
            }

            int warehouseID = Integer.parseInt(warehouseIDChoice.getValue());
            int productID = Integer.parseInt(productIDChoice.getValue());

            String sql = "{CALL ADD_ITEM_TO_STOCK_PR(?, ?, ?)}";
            CallableStatement statement = connection.prepareCall(sql);
            statement.setInt(1, productID);
            statement.setInt(2, warehouseID);
            statement.setInt(3,amount);
            statement.execute();
            resultLabel.setText("Item:" + productID+" added to warehouse:"+ warehouseID +" successfully");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

}

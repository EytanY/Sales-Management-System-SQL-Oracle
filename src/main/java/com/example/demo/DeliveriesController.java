package com.example.demo;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DeliveriesController extends Controller implements Initializable {
    public ArrayList<String> DELIVERY_TYPES = new ArrayList<>();
    public Label resultLabel;
    public ChoiceBox<String> deliveryTypeCB;
    public TextField deliveryIDTF;
    public TextField itemIDTF;
    public TextField amountTF;
    public ChoiceBox<String> ordersIDChoice;
    public ChoiceBox<String> warehouseIDChoice;
    public ChoiceBox<String> deliveryIDChoice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DELIVERY_TYPES.add("A");
        DELIVERY_TYPES.add("B");
        deliveryTypeCB.getItems().addAll(DELIVERY_TYPES);
        deliveryTypeCB.setValue(DELIVERY_TYPES.get(0));

        ArrayList<String> orderIDStr = getColFromTable(ORDER_ID, ORDER_TABLE);
        ordersIDChoice.getItems().addAll(orderIDStr);
        if(orderIDStr.size() > 1)
            ordersIDChoice.setValue(orderIDStr.get(0));

        ArrayList<String> warehouseIDStr = getColFromTable(WAREHOUSE_ID, WAREHOUSE_TABLE);
        warehouseIDChoice.getItems().addAll(warehouseIDStr);
        if(warehouseIDStr.size() > 1)
            warehouseIDChoice.setValue(warehouseIDStr.get(0));

        ArrayList<String> deliveryIDStr = getColFromTable(DELIVERY_ID, DELIVERY_TABLE);
        deliveryIDChoice.getItems().addAll(deliveryIDStr);
        if(deliveryIDStr.size() > 1)
            deliveryIDChoice.setValue(deliveryIDStr.get(0));

    }

    public void onAddNewDeliveryButtonClick() {

        try {
            int orderID = Integer.parseInt(ordersIDChoice.getValue());
            int warehouseID = Integer.parseInt(warehouseIDChoice.getValue());
            Connection connection = new SQL().getConnection();
            String sql = String.format("INSERT INTO %s (%s, %s, %s) VALUES ( ? , ? , ?)",
                    DELIVERY_TABLE, DELIVERY_ORDER_ID, DELIVERY_WAREHOUSE_ID, DELIVERY_TYPE);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,orderID);
            statement.setInt(2,warehouseID);
            statement.setInt(3, DELIVERY_TYPES.indexOf(deliveryTypeCB.getValue()));
            statement.executeUpdate();
            statement.close();

            String sql1 = String.format("SELECT max(%s) %s from %s"
            , DELIVERY_ID, DELIVERY_ID, DELIVERY_TABLE);
            PreparedStatement statement1 = connection.prepareStatement(sql1);
            ResultSet rs =  statement1.executeQuery();
            rs.next();
            Alert message = new Alert(Alert.AlertType.INFORMATION);
            String messageStr = "SUCCESS! New Delivery ID:" + rs.getInt(DELIVERY_ID);
            message.setContentText(messageStr);
            message.show();
            connection.close();
            statement1.close();
            onReturnToMenuButtonClick();
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public void onAddItemsToDeliveryButtonClick() {

        try{
            int deliveryID = Integer.parseInt(deliveryIDChoice.getValue());
            int itemID = Integer.parseInt(itemIDTF.getText());
            int amount = Integer.parseInt(amountTF.getText());
            Connection connection = new SQL().getConnection();
            String searchSQL = String.format("select * from %s where %s = ? and %s = ?",
                    ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_ORDERS_ITEM_ID, ITEMS_IN_DELIVERY_DELIVERY_ID);
            PreparedStatement searchStatement = connection.prepareStatement(searchSQL);
            searchStatement.setInt(1, itemID);
            searchStatement.setInt(2, deliveryID);
            ResultSet searchResultSet = searchStatement.executeQuery();
            if(searchResultSet.next()){

                String amountSQL = String.format("SELECT %s(?, ?) from DUAL", NUM_OF_ITEMS_TO_SEND_FUNC);
                PreparedStatement amountStatement = connection.prepareStatement(amountSQL);
                amountStatement.setInt(1, deliveryID);
                amountStatement.setInt(2, itemID);
                ResultSet rs = amountStatement.executeQuery();
                rs.next();

                int maxAmount = rs.getInt(1);
                if(amount > maxAmount) {
                    resultLabel.setText("Amount is too high! Can send max " + maxAmount + " items with item ID : " + itemID +".");
                    return;
                }
                else if(amount < 0){
                    resultLabel.setText("Amount can not be negative");
                    return;
                }
                else{
                    String updateSQL = String.format("UPDATE %s SET %s = %s + ? WHERE %s = ? AND %s = ?",
                            ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_ORDERS_AMOUNT, ITEMS_IN_DELIVERY_AMOUNT, ITEMS_IN_DELIVERY_DELIVERY_ID
                    , ITEMS_IN_DELIVERY_ITEM_ID);
                    PreparedStatement updateStatement = connection.prepareStatement(updateSQL);
                    updateStatement.setInt(1, amount);
                    updateStatement.setInt(2, deliveryID);
                    updateStatement.setInt(3, itemID);
                    updateStatement.executeUpdate();
                }

            }else {
                String sql = String.format("INSERT INTO %s(%s, %s, %s) VALUES(?, ?, ?)",
                        ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_DELIVERY_ID, ITEMS_IN_DELIVERY_ITEM_ID, ITEMS_IN_ORDERS_AMOUNT);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1,deliveryID);
                statement.setInt(2,itemID);
                statement.setInt(3, amount);
                statement.executeUpdate();
            }
            CallableStatement myCall = connection.prepareCall("{call update_order_status(?)}");
            myCall.setInt(1, deliveryID);
            myCall.executeUpdate();
            resultLabel.setText("SUCCESS");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public void onSearchDeliveryButtonClick() {
        try{
            Connection connection = new SQL().getConnection();
            String sql = String.format("SELECT * " +
                                        "FROM %s LEFT JOIN %s " +
                                        "ON %s.%s = %s.%s " +
                                        "where %s.%s = ?",
                    DELIVERY_TABLE, ITEMS_IN_DELIVERY_TABLE, DELIVERY_TABLE, DELIVERY_ID, ITEMS_IN_DELIVERY_TABLE
            , ITEMS_IN_DELIVERY_DELIVERY_ID, DELIVERY_TABLE, DELIVERY_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(deliveryIDChoice.getValue()));
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            boolean firstIteration = false;
            while (rs.next()){
                if(!firstIteration){
                    sb.append("Delivery ID:").append(rs.getInt(DELIVERY_ID));
                    sb.append("  Order ID:").append(rs.getInt(DELIVERY_ORDER_ID));
                    sb.append("  Warehouse ID:").append(rs.getInt(DELIVERY_WAREHOUSE_ID)).append("\n");
                    sb.append("Date:").append(rs.getDate(DELIVERY_DATE));
                    sb.append("  Type:").append(rs.getInt(DELIVERY_TYPE)).append("\n");
                    sb.append("Items:").append("\n");
                    firstIteration = true;
               }

                sb.append("Item ID:").append(rs.getInt(ITEMS_IN_DELIVERY_ITEM_ID));
                sb.append("  Amount:").append(rs.getInt(ITEMS_IN_DELIVERY_AMOUNT)).append("\n");


            }
            resultLabel.setText(sb.toString());

        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public void onSearchAllDeliveriesButtonClick() {
        try{
            Connection connection = new SQL().getConnection();
            String sql = String.format("SELECT * FROM %s",
                    DELIVERY_TABLE);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Delivery ID:").append(rs.getInt(DELIVERY_ID));
                sb.append("  Order ID:").append(rs.getInt(DELIVERY_ORDER_ID));
                sb.append("  Warehouse ID:").append(rs.getInt(DELIVERY_WAREHOUSE_ID)).append("\n");
                sb.append("Date:").append(rs.getDate(DELIVERY_DATE));
                sb.append("  Type:").append(rs.getInt(DELIVERY_TYPE)).append("\n\n");
            }
            resultLabel.setText(sb.toString());

        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
}

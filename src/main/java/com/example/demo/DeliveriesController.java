package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
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
    public TextField searchDeliveryIDTF;
    public ChoiceBox<String> ordersIDChoice;
    public ChoiceBox<String> warehouseIDChoice;

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
        warehouseIDChoice.getItems().addAll(orderIDStr);
        if(warehouseIDStr.size() > 1)
            warehouseIDChoice.setValue(warehouseIDStr.get(0));


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
            resultLabel.setText("SUCCESS! New Delivery ID:" + rs.getInt(DELIVERY_ID));
            connection.close();
            statement1.close();
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public void onAddItemsToDeliveryButtonClick(ActionEvent actionEvent) {

        try{
            int deliveryID = Integer.parseInt(deliveryIDTF.getText());
            int itemID = Integer.parseInt(itemIDTF.getText());
            int amount = Integer.parseInt(amountTF.getText());
            Connection connection = new SQL().getConnection();
            String searchSQL = "select * from items_in_delivery where item_id = ? and delivery_id = ?";
            PreparedStatement searchStatement = connection.prepareStatement(searchSQL);
            searchStatement.setInt(1, itemID);
            searchStatement.setInt(2, deliveryID);
            ResultSet searchResultSet = searchStatement.executeQuery();
            if(searchResultSet.next()){

                String amountSQL = "SELECT num_of_items_to_send(?, ?) from DUAL";
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
                    String updateSQL = "UPDATE items_in_delivery SET amount = amount + ? WHERE delivery_id = ? AND item_id = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateSQL);
                    updateStatement.setInt(1, amount);
                    updateStatement.setInt(2, deliveryID);
                    updateStatement.setInt(3, itemID);
                    updateStatement.executeUpdate();
                }

            }else {
                String sql = "INSERT INTO items_in_delivery(delivery_id, item_id, amount) VALUES(?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1,deliveryID);
                statement.setInt(2,itemID);
                statement.setInt(3, amount);
                statement.executeUpdate();
            }
            String sql = "EXEC update_order_status(?)";
            CallableStatement myCall = connection.prepareCall("{call update_order_status(?)}");
            myCall.setInt(1, deliveryID);
            myCall.executeUpdate();
            resultLabel.setText("SUCCESS");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public void onSearchDeliveryButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = "SELECT * " +
                    "FROM delivery LEFT JOIN items_in_delivery " +
                    "ON DELIVERY.DELIVERY_ID = ITEMS_IN_DELIVERY.DELIVERY_ID " +
                    "where delivery.delivery_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(searchDeliveryIDTF.getText()));
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            boolean firstIteration = false;
            while (rs.next()){
                if(!firstIteration){
                    sb.append("Delivery ID:").append(rs.getInt("delivery_id"));
                    sb.append("  Order ID:").append(rs.getInt("order_id"));
                    sb.append("  Warehouse ID:").append(rs.getInt("warehouses_id")).append("\n");
                    sb.append("Date:").append(rs.getDate("delivery_date"));
                    sb.append("  Type:").append(rs.getInt("delivery_type")).append("\n");
                    sb.append("Items:").append("\n");
                    firstIteration = true;
               }

                sb.append("Item ID:").append(rs.getInt("item_id"));
                sb.append("  Amount:").append(rs.getInt("amount")).append("\n");


            }
            resultLabel.setText(sb.toString());

        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public void onSearchAllDeliveriesButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = "SELECT * FROM delivery ";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Delivery ID:").append(rs.getInt("delivery_id"));
                sb.append("  Order ID:").append(rs.getInt("order_id"));
                sb.append("  Warehouse ID:").append(rs.getInt("warehouses_id")).append("\n");
                sb.append("Date:").append(rs.getDate("delivery_date"));
                sb.append("  Type:").append(rs.getInt("delivery_type")).append("\n\n");
            }
            resultLabel.setText(sb.toString());

        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
}

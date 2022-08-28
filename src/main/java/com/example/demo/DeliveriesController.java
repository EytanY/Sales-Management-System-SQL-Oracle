package com.example.demo;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DeliveriesController extends Controller implements Initializable {
    public ArrayList<String> DELIVERY_TYPES = new ArrayList<>();
    public Label resultLabel;
    public ChoiceBox<String> deliveryTypeCB;
    public ChoiceBox<String> ordersIDChoice;
    public ChoiceBox<String> warehouseIDChoice;
    public ChoiceBox<String> deliveryIDChoice;
    public Button addItemToDeliveryButton;

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
            statement1.close();
            onReturnToMenuButtonClick();
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }



    public void onSearchDeliveryButtonClick() {
        try{
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
                if(rs.getInt(ITEMS_IN_DELIVERY_ITEM_ID) != 0){
                    sb.append("Item ID:").append(rs.getInt(ITEMS_IN_DELIVERY_ITEM_ID));
                    sb.append("  Amount:").append(rs.getInt(ITEMS_IN_DELIVERY_AMOUNT)).append("\n");
                }

            }
            resultLabel.setText(sb.toString());

        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public void onSearchAllDeliveriesButtonClick() {
        try{
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

    public void addNewItemToDeliveryButtonClick() throws IOException {
        deliveryID = Integer.parseInt(deliveryIDChoice.getValue());
        changeScene(addItemToDeliveryButton, "Add New Item", "add-new-item-delivery-view.fxml");
    }
}

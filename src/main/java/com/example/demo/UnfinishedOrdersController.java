package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class UnfinishedOrdersController extends Controller implements Initializable {
    @FXML
    public Label resultLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String unfinishedOrders = printUnfinishedOrders();
        resultLabel.setText(Objects.requireNonNullElse(unfinishedOrders, "Error"));
    }
    public String printUnfinishedOrders(){
        try{
            String sql = String.format("SELECT %s " +
                    "FROM %s " +
                    "WHERE %s(%s) = 0"
            , ORDER_ID, ORDER_TABLE, IS_ORDER_DONE_FUNC, ORDER_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            StringBuilder stringBuilder = new StringBuilder();
            while (resultSet.next()){
                String res = printGetRestOfItemsToDeliverToOrder(resultSet.getInt(ORDER_ID));
                if(res != null){
                    stringBuilder.append(res);
                    stringBuilder.append("\n");
                }
            }
            return stringBuilder.toString();
        }catch (Exception exception){
            return null;
        }
    }

    public String printGetRestOfItemsToDeliverToOrder(int order_id){
        try{
            String sql = String.format("select %s.%s , %s.%s, %s.%s, NVL(%s.%s  - T1.total, %s.%s ) " +
                    "       from     %s  LEFT JOIN    ( select  %s.%s as item_id ,  sum(%s.%s)  as total " +
                    "                from %s join %s on %s.%s = %s.%s " +
                    "                join %s on %s.%s = %s.%s " +
                    "                where %s.%s = ? " +
                    "                and %s.%s = %s.%s " +
                    "                GROUP by %s.%s) T1 " +
                    "        ON %s.%s = T1.item_id  " +
                    "        join %s on %s.%s  = %s.%s " +
                    "        where %s.%s = ?",
                    ITEMS_IN_ORDERS_TABLE, ITEMS_IN_ORDERS_ITEM_ID, ITEM_TABLE, ITEM_DESCRIPTION, ITEM_TABLE, ITEM_PRICE,
                    ITEMS_IN_ORDERS_TABLE, ITEMS_IN_ORDERS_AMOUNT, ITEMS_IN_ORDERS_TABLE, ITEMS_IN_ORDERS_AMOUNT,
                    ITEMS_IN_ORDERS_TABLE, ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_ITEM_ID, ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_AMOUNT, ITEMS_IN_ORDERS_TABLE, DELIVERY_TABLE,
                    ITEMS_IN_ORDERS_TABLE, ITEMS_IN_ORDERS_ORDER_ID, DELIVERY_TABLE, DELIVERY_ORDER_ID, ITEMS_IN_DELIVERY_TABLE,
                    ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_DELIVERY_ID, DELIVERY_TABLE, DELIVERY_ID, DELIVERY_TABLE, DELIVERY_ORDER_ID,
                    ITEMS_IN_ORDERS_TABLE, ITEMS_IN_ORDERS_ITEM_ID, ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_ITEM_ID,
                    ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_ITEM_ID, ITEMS_IN_ORDERS_TABLE, ITEMS_IN_ORDERS_ITEM_ID,
                    ITEM_TABLE, ITEMS_IN_ORDERS_TABLE, ITEMS_IN_ORDERS_ITEM_ID, ITEM_TABLE, ITEM_ID,
                    ITEMS_IN_ORDERS_TABLE, ITEMS_IN_ORDERS_ORDER_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, order_id);
            statement.setInt(2, order_id);
            ResultSet resultSet = statement.executeQuery();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Order ID:").append(order_id).append("\n");
            stringBuilder.append("Items that not delivered yet:\n");

            while (resultSet.next()){
                stringBuilder.append("Item ID").append(resultSet.getInt(1)).append("  ");
                stringBuilder.append("Item Description:").append(resultSet.getString(2)).append("  ");
                stringBuilder.append("Item Price:").append(resultSet.getFloat(3)).append("$  ");
                stringBuilder.append(" Rest to Send:").append(resultSet.getInt(4)).append("  \n");
            }
            return stringBuilder.toString();
        }catch (Exception exception){
            return null;
        }
    }
}

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
            String sql = "select * from rest_items_to_send where order_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, order_id);
            ResultSet resultSet = statement.executeQuery();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Order ID:").append(order_id).append("\n");
            stringBuilder.append("Items that not delivered yet:\n");
            while (resultSet.next()){
                stringBuilder.append("Item ID").append(resultSet.getInt(2)).append("  ");
                stringBuilder.append(" Rest to Send:").append(resultSet.getInt(3)).append("  \n");
            }
            return stringBuilder.toString();
        }catch (Exception exception){
            return null;
        }
    }
}

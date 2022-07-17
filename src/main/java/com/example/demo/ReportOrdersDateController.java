package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportOrdersDateController extends Controller{
    @FXML
    public Label resultLabel;
    @FXML
    public DatePicker dateOrders;

    public void onSearchDeliveriesByDateButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = String.format("select * " +
                    "FROM %s  join %s on %s.%s = %s.%s " +
                    "JOIN %s ON %s.%s = %s.%s " +
                    " WHERE ? = TO_DATE(%s.%s)"
            , CUSTOMER_TABLE, ORDER_TABLE, ORDER_TABLE, ORDER_CUSTOMER_ID, CUSTOMER_TABLE, CUSTOMER_ID
            , DELIVERY_TABLE, ORDER_TABLE, ORDER_ID, DELIVERY_TABLE, DELIVERY_ORDER_ID
            , DELIVERY_TABLE, DELIVERY_DATE);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(dateOrders.getValue()));
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            sb.append("Deliveries::\n");
            while(rs.next()){
                sb.append("Delivery ID:").append(rs.getInt(DELIVERY_ID));
                sb.append( ",Type:").append(rs.getString(DELIVERY_TYPE)).append(" ").append(rs.getString("last_name"));
                sb.append(" From Warehouse ID:").append(rs.getInt(DELIVERY_WAREHOUSE_ID));
                sb.append(" ,Order ID:").append(rs.getInt(DELIVERY_ORDER_ID)).append(" ,Status").append(rs.getInt(ORDER_STATUS)).append("\n");
            }

            resultLabel.setText(sb.toString());

        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public void onSearchOrdersByDateButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = String.format("select * " +
                    "FROM %s  join %s on %s.%s = %s.%s " +
                    "WHERE ? = TO_DATE(%s.%s) ",
                    CUSTOMER_TABLE, ORDER_TABLE, ORDER_TABLE, ORDER_CUSTOMER_ID, CUSTOMER_TABLE, CUSTOMER_ID,
                    ORDER_TABLE, ORDER_DATE);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(dateOrders.getValue()));
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            sb.append("Orders:\n");
            while(rs.next()){
                sb.append("Customer ID:").append(rs.getInt(ORDER_CUSTOMER_ID));
                sb.append( ",Name:").append(rs.getString(CUSTOMER_FIRST_NAME)).append(" ").append(rs.getString(CUSTOMER_LAST_NAME));
                sb.append(" ,Order ID:").append(rs.getInt(ORDER_ID)).append(" ,Status").append(rs.getInt(ORDER_STATUS)).append("\n");
            }

            resultLabel.setText(sb.toString());

        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
}

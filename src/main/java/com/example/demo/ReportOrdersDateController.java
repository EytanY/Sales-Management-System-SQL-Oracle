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
            String sql = "select * " +
                    "FROM customers  join orders on orders.costumer_id = customers.customer_id " +
                    "JOIN delivery ON orders.order_id = delivery.order_id " +
                    " WHERE ? = TO_DATE(DELIVERY.DELIVERY_DATE)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(dateOrders.getValue()));
            ResultSet rs = statement.executeQuery();
            System.out.println("E");
            StringBuilder sb = new StringBuilder();
            sb.append("Deliveries::\n");
            while(rs.next()){
                sb.append("Delivery ID:").append(rs.getInt("delivery_id"));
                sb.append( ",Type:").append(rs.getString("delivery_type")).append(" ").append(rs.getString("last_name"));
                sb.append(" From Warehouse ID:").append(rs.getInt("warehouses_id"));
                sb.append(" ,Order ID:").append(rs.getInt("order_id")).append(" ,Status").append(rs.getInt("status")).append("\n");
            }

            resultLabel.setText(sb.toString());

        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public void onSearchOrdersByDateButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = "select * " +
                    "FROM customers  join orders on orders.costumer_id = customers.customer_id " +
                    "WHERE ? = TO_DATE(orders.order_date) ";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(dateOrders.getValue()));
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            sb.append("Orders:\n");
            while(rs.next()){
                sb.append("Customer ID:").append(rs.getInt("costumer_id"));
                sb.append( ",Name:").append(rs.getString("first_name")).append(" ").append(rs.getString("last_name"));
                sb.append(" ,Order ID:").append(rs.getInt("order_id")).append(" ,Status").append(rs.getInt("status")).append("\n");
            }

            resultLabel.setText(sb.toString());

        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
}

package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CostumerOrdersController extends Controller{
    @FXML
    public Label resultLabel;
    @FXML
    public TextField customerIDTF;

    public void onSearchCustomerButtonClick(ActionEvent actionEvent) {
        try {
            Connection connection = new SQL().getConnection();
            String sql = "select customers.first_name, customers.last_name, orders.order_date, orders.order_id, orders.status, get_total_price_of_order(orders.order_id) as price  " +
                    "FROM customers LEFT JOIN orders ON orders.costumer_id = customers.customer_id " +
                    "WHERE customers.customer_id  = ? ";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(customerIDTF.getText()));
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            boolean hasInfo = false;
            int orderID = 0;
            while (rs.next()){
                if(!hasInfo){
                    sb.append("Customer's Name:").append(rs.getString("first_name")).append(" ").append(rs.getString("last_name")).append("\n\n");
                    sb.append("Orders:\n");
                    hasInfo = true;
                }
                if(rs.getInt("order_id") != 0){
                    sb.append("Order ID:").append(rs.getInt("order_id"));
                    sb.append(" ,Date:").append(rs.getDate("order_date"));
                    sb.append(" ,Status").append(rs.getInt("status"));
                    sb.append(" ,Total Price:").append(rs.getFloat("price")).append("$\n");
                }


            }
            if(sb.toString().equals("")){
                resultLabel.setText("Not Found!");
            }
            else {
                resultLabel.setText(sb.toString());
            }
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
}

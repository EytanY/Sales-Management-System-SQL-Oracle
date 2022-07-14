package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ReportOpenOrdersController extends Controller implements Initializable {
    @FXML
    public Label resultLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resultLabel.setText(getAllOpenOrders());
    }

    public String getAllOpenOrders() {
        try {
            Connection connection = new SQL().getConnection();
            String sql = "SELECT * " +
                    "FROM ORDERS, CUSTOMERS " +
                    "WHERE CUSTOMERS.customer_id = orders.costumer_id AND ORDERS.status = 1 " ;
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            boolean found = false;
            while (rs.next()) {
                sb.append("Order ID:").append(rs.getInt("order_id"));
                sb.append("  Date:").append(rs.getDate("order_date"));
                sb.append("  Status:").append(rs.getInt("status")).append("\n");
                sb.append("Customer ID:").append(rs.getInt("customer_id"));
                sb.append("  First Name:").append(rs.getString("first_name"));
                sb.append("  Last Name:").append(rs.getString("last_name")).append("\n\n");
                found = true;

            }

            if (!found) {
                return "Not Found";
            }
            return sb.toString();
        } catch (Exception exception) {
            return "Error!";
        }
    }
}
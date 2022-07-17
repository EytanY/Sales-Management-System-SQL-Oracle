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
            String sql = String.format("SELECT * " +
                    "FROM %s, %s " +
                    "WHERE %s.%s = %s.%s AND %s.%s = 1 ",
                    ORDER_TABLE, CUSTOMER_TABLE, CUSTOMER_TABLE, CUSTOMER_ID, ORDER_TABLE, ORDER_CUSTOMER_ID, ORDER_TABLE, ORDER_STATUS);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            boolean found = false;
            while (rs.next()) {
                sb.append("Order ID:").append(rs.getInt(ORDER_ID));
                sb.append("  Date:").append(rs.getDate(ORDER_DATE));
                sb.append("  Status:").append(rs.getInt(ORDER_STATUS)).append("\n");
                sb.append("Customer ID:").append(rs.getInt(CUSTOMER_ID));
                sb.append("  First Name:").append(rs.getString(CUSTOMER_FIRST_NAME));
                sb.append("  Last Name:").append(rs.getString(CUSTOMER_LAST_NAME)).append("\n\n");
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
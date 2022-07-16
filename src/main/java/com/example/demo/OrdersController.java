package com.example.demo;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrdersController extends Controller{
    public TextField customerIDTF;
    public TextField amountTF;
    public Label resultLabel;
    public TextField orderIDTF;
    public TextField itemIDTF;
    public TextField searchOrderIDTF;

    public void onAddNewOrderButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();

            String sql = "INSERT INTO ORDERS(costumer_id) VALUES(?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(customerIDTF.getText()));
            statement.executeUpdate();

            String sql1 = "SELECT max(order_id) from orders";
            PreparedStatement statement1 = connection.prepareStatement(sql1);
            ResultSet rs =  statement1.executeQuery();
            rs.next();
            resultLabel.setText("SUCCESS! New Order ID:" + rs.getInt("max(order_id)"));

        }catch (Exception exception){
            resultLabel.setText("Error");
        }

    }

    public void onAddNewItemToOrderButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String searchSQL = "select * from items_in_orders where item_id = ? and order_id = ?";
            PreparedStatement searchStatement = connection.prepareStatement(searchSQL);
            searchStatement.setInt(1, Integer.parseInt(itemIDTF.getText()));
            searchStatement.setInt(2, Integer.parseInt(orderIDTF.getText()));
            ResultSet searchResultSet = searchStatement.executeQuery();

            String statusSql  = "SELECT get_status_of_order_by_id(?) FROM DUAL";
            PreparedStatement statusSteStatement = connection.prepareStatement(statusSql);
            statusSteStatement.setInt(1, Integer.parseInt(orderIDTF.getText()));
            ResultSet resultSet = statusSteStatement.executeQuery();
            resultSet.next();

            if(resultSet.getInt(1) == 0){
                resultLabel.setText("Order's Status Closed.");
                return;
            }
            if(searchResultSet.next()){

                String updateSQL = "UPDATE items_in_orders SET amount = amount + ? WHERE order_id = ? AND item_id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSQL);
                updateStatement.setInt(1, Integer.parseInt(amountTF.getText()));
                updateStatement.setInt(2, Integer.parseInt(orderIDTF.getText()));
                updateStatement.setInt(3, Integer.parseInt(itemIDTF.getText()));
                updateStatement.executeUpdate();

            }else {
                String sql = "INSERT INTO ITEMS_IN_ORDERS(order_id, item_id, amount) VALUES(?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(orderIDTF.getText()));
                statement.setInt(2, Integer.parseInt(itemIDTF.getText()));
                statement.setInt(3, Integer.parseInt(amountTF.getText()));
                statement.executeUpdate();
            }
            resultLabel.setText("SUCCESS");
        }catch (Exception exception){
            resultLabel.setText("Error");
        }
    }

    public void onSearchOrderButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = "SELECT * " +
                    "FROM customers JOIN orders ON customers.customer_id = orders.costumer_id " +
                    "LEFT JOIN items_in_orders ON items_in_orders.order_id = orders.order_id " +
                    "WHERE orders.order_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(searchOrderIDTF.getText()));
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            boolean found = false;
            while (rs.next()) {
                if(!found) {
                    sb.append("Order ID:").append(rs.getInt("order_id"));
                    sb.append("  Date:").append(rs.getDate("order_date"));
                    sb.append("  Status:").append(rs.getInt("status")).append("\n");
                    sb.append("Customer ID:").append(rs.getInt("customer_id"));
                    sb.append("  First Name:").append(rs.getString("first_name"));
                    sb.append("  Last Name:").append(rs.getString("last_name")).append("\n\n");
                    sb.append("Items:\n");
                    found = true;
                }
                if(rs.getInt("item_id") > 0){
                    sb.append("Item ID: ").append(rs.getInt("item_id"));
                    sb.append("  Amount: ").append(rs.getInt("amount")).append("\n");
                }

            }
            resultLabel.setText(sb.toString());
            if(!found){
                resultLabel.setText("Not Found");
            }

        }catch (Exception exception){
            resultLabel.setText("Error");
        }
    }

    public void onShowAllOrdersButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = "SELECT * " +
                    "FROM ORDERS, CUSTOMERS " +
                    "WHERE CUSTOMERS.customer_id = orders.costumer_id";
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
            resultLabel.setText(sb.toString());
            if(!found){
                resultLabel.setText("Not Found");
            }
        }catch (Exception exception){
            resultLabel.setText("Error");
        }
    }
}

package com.example.demo;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OrdersController extends Controller implements Initializable {
    public TextField amountTF;
    public Label resultLabel;
    public TextField orderIDTF;
    public ChoiceBox<String> customersIDChoice;
    public ChoiceBox<String> itemIDChoice;
    public ChoiceBox<String> orderIDChoice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<String> customersIDStr = getColFromTable(CUSTOMER_ID, CUSTOMER_TABLE);
        customersIDChoice.getItems().addAll(customersIDStr);
        if(customersIDStr.size() > 0)
            customersIDChoice.setValue(customersIDStr.get(0));


        ArrayList<String> ordersIDStr = getColFromTable(ORDER_ID, ORDER_TABLE);
        orderIDChoice.getItems().addAll(ordersIDStr);
        if(ordersIDStr.size() > 0)
            orderIDChoice.setValue(ordersIDStr.get(0));

        ArrayList<String> itemsIDStr = getColFromTable(ITEM_ID, ITEM_TABLE);
        itemIDChoice.getItems().addAll(itemsIDStr);
        if(itemsIDStr.size() > 0)
            itemIDChoice.setValue(itemsIDStr.get(0));
    }

    public void onAddNewOrderButtonClick() {
        try{
            String sql = "{CALL ADD_ORDER(?, ?)}";
            CallableStatement statement = connection.prepareCall(sql);
            statement.setInt(1, Integer.parseInt(customersIDChoice.getValue()));
            statement.registerOutParameter(2, Types.INTEGER);
            statement.execute();
            Alert message = new Alert(Alert.AlertType.INFORMATION);
            String messageStr = "SUCCESS! New Order ID:" + statement.getInt(2);
            message.setContentText(messageStr);
            message.show();
            onReturnToMenuButtonClick();
        }catch (Exception exception){
            resultLabel.setText("Error");
        }

    }

    public void onAddNewItemToOrderButtonClick() {
        try{
            int order_id = Integer.parseInt(orderIDChoice.getValue());
            int item_id = Integer.parseInt(itemIDChoice.getValue());
            int amount = Integer.parseInt(amountTF.getText());
            if(amount < 1 ) {
                resultLabel.setText("Amount can not be negative!");
                return;
            }
            String sql = "{CALL add_item_to_order_pr(?, ?, ?)}";
            CallableStatement statement = connection.prepareCall(sql);
            statement.setInt(1, order_id);
            statement.setInt(2, item_id);
            statement.setInt(3,amount);
            statement.execute();
            resultLabel.setText(amount + " items" + item_id + " added to order " + order_id +" successfully");
        }catch (Exception exception){
            resultLabel.setText("Error");
        }
    }

    public void onSearchOrderButtonClick() {
        try{
            String sql = "select * from customers_orders where order_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(orderIDTF.getText()));
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            boolean found = false;
            while (rs.next()) {
                if(!found) {
                    sb.append("Order ID:").append(rs.getInt(ORDER_ID));
                    sb.append("  Date:").append(rs.getDate(ORDER_DATE));
                    sb.append("  Status:").append(rs.getInt(ORDER_STATUS)).append("\n");
                    sb.append("Customer ID:").append(rs.getInt(ORDER_CUSTOMER_ID));
                    sb.append("  First Name:").append(rs.getString(CUSTOMER_FIRST_NAME));
                    sb.append("  Last Name:").append(rs.getString(CUSTOMER_LAST_NAME)).append("\n\n");
                    sb.append("Items:\n");
                    found = true;
                }
                if(rs.getInt(ITEMS_IN_ORDERS_ITEM_ID) > 0){
                    sb.append("Item ID: ").append(rs.getInt(ITEMS_IN_ORDERS_ITEM_ID));
                    sb.append("  Amount: ").append(rs.getInt(ITEMS_IN_ORDERS_AMOUNT)).append("\n");
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

    public void onShowAllOrdersButtonClick() {
        try{
            String sql = String.format("SELECT * FROM %s, %s WHERE %s.%s = %s.%s"
            , ORDER_TABLE, CUSTOMER_TABLE, CUSTOMER_TABLE, CUSTOMER_ID, ORDER_TABLE, ORDER_CUSTOMER_ID);
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
            resultLabel.setText(sb.toString());
            if(!found){
                resultLabel.setText("Not Found");
            }
        }catch (Exception exception){
            resultLabel.setText("Error");
        }
    }

    public void closeOrderStatusButtonClick() {
        try {
            int orderID = Integer.parseInt(orderIDTF.getText());
            connection = new SQL().getConnection();
            String sql = String.format("UPDATE %s SET %s = 0 WHERE %s =  ?", ORDER_TABLE,
                    ORDER_STATUS, ORDER_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderID);
            statement.executeUpdate();
            statement.close();
            resultLabel.setText("Order " + orderID + " closed successfully");
        }catch (Exception exception){
            resultLabel.setText("Error");
        }
    }
}

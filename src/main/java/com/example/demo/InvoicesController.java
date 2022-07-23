package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InvoicesController extends Controller{
    @FXML
    public TextField deliveryIDTF;
    @FXML
    public Label resultLabel;
    @FXML
    public TextField orderIDTF;

    @FXML
    public void onShowDeliveryInvoiceButtonClick() {
        try {
            int deliveryID = Integer.parseInt(deliveryIDTF.getText());
            resultLabel.setText(printInvoiceOfDelivery(deliveryID));
        }catch (Exception exception){
            resultLabel.setText("Please Enter Valid ID!");

        }
    }
    @FXML
    public void onShowOrderInvoiceButtonClick() {
        try {
            int orderID = Integer.parseInt(orderIDTF.getText());
            resultLabel.setText(printInvoiceOfOrder(orderID));
        }catch (Exception exception){
            resultLabel.setText("Please Enter Valid ID!");
        }
    }
    @FXML
    public void onCancelOrderButton() {
        try {
            int orderID = Integer.parseInt(orderIDTF.getText());
            String sql = String.format("DELETE FROM %s WHERE %s = ?", ORDER_TABLE, ORDER_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderID);
            statement.executeUpdate();
            resultLabel.setText("SUCCESS");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
    @FXML
    public void onCancelDeliveryButton() {
        try {
            int deliveryID = Integer.parseInt(deliveryIDTF.getText());
            String sql = String.format("DELETE FROM %s WHERE %s = ?", DELIVERY_TABLE, DELIVERY_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, deliveryID);
            statement.executeUpdate();
            resultLabel.setText("SUCCESS");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }

    public String printInvoiceOfDelivery(int deliveryID){
        try {
            float totalPrice = 0;
            String sql = String.format("select %s.%s, %s,%s, %s, %s.%s, %s, %s, " +
                            "%s * %s as price_items, %s(?) as total " +
                            "from %s left join %s on %s.%s = %s.%s " +
                            "INNER JOIN %s ON %s.%s = %s.%s " +
                            "where %s.%s = ?", DELIVERY_TABLE, DELIVERY_ID, DELIVERY_WAREHOUSE_ID, DELIVERY_DATE, DELIVERY_TYPE
                    ,ITEM_TABLE, ITEM_ID, DELIVERY_ORDER_ID, ITEMS_IN_DELIVERY_AMOUNT, ITEM_PRICE, ITEMS_IN_DELIVERY_AMOUNT
                    , GET_TOTAL_PRICE_OF_DELIVERY_FUNC, DELIVERY_TABLE, ITEMS_IN_DELIVERY_TABLE, DELIVERY_TABLE, DELIVERY_ID,
                    ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_DELIVERY_ID, ITEM_TABLE, ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_ITEM_ID,
                    ITEM_TABLE, ITEM_ID, DELIVERY_TABLE, DELIVERY_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, deliveryID);
            statement.setInt(2, deliveryID);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            boolean firstIteration = true;
            while (rs.next()){
                if(firstIteration){
                    sb.append("Delivery ID:").append(deliveryID);
                    sb.append("  Warehouse ID:").append(rs.getInt(DELIVERY_WAREHOUSE_ID)).append("\n");
                    sb.append(" Order ID:").append(rs.getInt(ORDER_ID));
                    sb.append("Date:").append(rs.getDate(DELIVERY_DATE));
                    sb.append("  Type:").append(rs.getInt(DELIVERY_TYPE)).append("\n");
                    sb.append("Items:").append("\n");
                    totalPrice = rs.getFloat("total");
                    firstIteration = false;
                }
                sb.append("Item ID:").append(rs.getInt(5));
                sb.append("  Amount:").append(rs.getInt(ITEMS_IN_DELIVERY_AMOUNT));
                sb.append(", Price:").append(rs.getFloat("price_items")).append("$.\n");
            }
            if(sb.toString().equals(""))
                return "Invoice Not Founded";
                sb.append("\nTotal Price of Delivery: ").append(totalPrice).append("$.");
            return sb.toString();
        }catch (Exception exception){
            return null;
        }
    }

    public String printInvoiceOfOrder(int orderID){
        try{
            String sql = String.format("select %s.%s,  %s(?) " +
                    "from %s LEFT JOIN %s ON %s.%s = %s.%s left join %s on %s.%s = %s.%s " +
                    "where %s.%s = ? " +
                    "GROUP by %s.%s",
                    DELIVERY_TABLE, DELIVERY_ID, GET_TOTAL_PRICE_OF_ORDER_THAT_SEND_FUNC, ORDER_TABLE, DELIVERY_TABLE, ORDER_TABLE, ORDER_ID,
                    DELIVERY_TABLE, DELIVERY_ORDER_ID, ITEMS_IN_DELIVERY_TABLE, DELIVERY_TABLE, DELIVERY_ID, ITEMS_IN_DELIVERY_TABLE,
                    ITEMS_IN_DELIVERY_DELIVERY_ID,  DELIVERY_TABLE, DELIVERY_ORDER_ID, DELIVERY_TABLE, DELIVERY_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderID);
            statement.setInt(2, orderID);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            boolean firstIteration = true;
            float totalPrice = 0;
            int deliveryID;
            while (rs.next()){
                if(firstIteration){
                    totalPrice = rs.getFloat(2);
                    sb.append("Order ID:").append(orderID).append("\n\n");
                    firstIteration = false;

                }
                deliveryID = rs.getInt(1);
                sb.append("Delivery ").append(deliveryID).append(":\n");
                sb.append(printInvoiceOfDelivery(deliveryID));
                sb.append("\n\n");
            }
            if(sb.toString().equals(""))
                return "Order is Empty(Doesn't has deliveries) Or Not Found";
            sb.append("TOTAL ORDER PRICE:").append(totalPrice).append("$.");
            return sb.toString();

        }catch (Exception exception){
            return null;
        }

    }
}

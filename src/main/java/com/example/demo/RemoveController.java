package com.example.demo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.PreparedStatement;

public class RemoveController extends Controller{
    @FXML
    public TextField deliveryIDTF;
    @FXML
    public TextField itemIDTF;
    @FXML
    public Label resultLabel;
    @FXML
    public TextField deliveryRemoveIDTF;
    @FXML
    public TextField orderIDTF;
    @FXML
    public TextField customerIDTF;

    @FXML
    public void onReturnProductButtonClick() {
        try{
            int deliveryID = Integer.parseInt(deliveryIDTF.getText());
            int itemID = Integer.parseInt(itemIDTF.getText());
            String sql = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", ITEMS_IN_DELIVERY_TABLE,
                    ITEMS_IN_DELIVERY_DELIVERY_ID, ITEMS_IN_DELIVERY_ITEM_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, deliveryID);
            statement.setInt(2, itemID);
            statement.executeUpdate();
            resultLabel.setText("SUCCESS");
        }catch (Exception exception){
            resultLabel.setText("Error!");
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
            resultLabel.setText("Order " + orderID + " has removed");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
    @FXML
    public void onCancelDeliveryButton() {
        try {
            int deliveryID = Integer.parseInt(deliveryRemoveIDTF.getText());
            String sql = String.format("DELETE FROM %s WHERE %s = ?", DELIVERY_TABLE, DELIVERY_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, deliveryID);
            statement.executeUpdate();
            resultLabel.setText("Delivery " + deliveryID  + " has removed");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
    @FXML
    public void onRemoveCustomerButton() {
        try {
            int customerID = Integer.parseInt(customerIDTF.getText());
            String sql = String.format("DELETE FROM %s WHERE %s = ?", CUSTOMER_TABLE, CUSTOMER_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            System.out.println(sql);
            statement.setInt(1, customerID);
            statement.executeUpdate();
            resultLabel.setText("Customer ID:"+ customerID + " has removed");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
}

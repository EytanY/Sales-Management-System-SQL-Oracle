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


}

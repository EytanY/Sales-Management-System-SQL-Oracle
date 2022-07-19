package com.example.demo;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.PreparedStatement;

public class ReturnProductController extends Controller{
    @FXML
    public TextField deliveryIDTF;
    @FXML
    public TextField itemIDTF;
    @FXML
    public Label resultLabel;
    @FXML
    public void onReturnProductButtonClick() {
        try{
            int deliveryID = Integer.parseInt(deliveryIDTF.getText());
            int itemID = Integer.parseInt(itemIDTF.getText());
            String sql = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", ITEMS_IN_DELIVERY_TABLE,
                    ITEMS_IN_DELIVERY_DELIVERY_ID, ITEMS_IN_DELIVERY_ITEM_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            System.out.println(sql);
            statement.setInt(1, deliveryID);
            statement.setInt(2, itemID);
            statement.executeUpdate();
            resultLabel.setText("SUCCESS!");
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
}

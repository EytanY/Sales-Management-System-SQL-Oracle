package com.example.demo;


import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddNewItemDeliveryController extends Controller implements Initializable {
    public ChoiceBox<String> itemsIDChoice;
    public TextField amountTF;
    public Label resultLabel;
    public Text titleTxt;

    public void onAddItemToDeliveryButtonClick() {
        try{
            int amount = Integer.parseInt(amountTF.getText());
            int item_id = Integer.parseInt(itemsIDChoice.getValue());
            int maxAmount = getMaxAmountOfItemInDelivery(deliveryID, item_id);
            if(amount < 1){
                resultLabel.setText("Amount can not be negative or zero!");
            }
            else if(amount > maxAmount) {
                resultLabel.setText("Amount too high! Max Amount is " + maxAmount);
                return;
            }

            String sql = "{CALL add_item_to_delivery_pr(?, ?, ?)}";
            CallableStatement statement = connection.prepareCall(sql);
            statement.setInt(1, deliveryID);
            statement.setInt(2, item_id);
            statement.setInt(3,amount);
            statement.execute();
            resultLabel.setText(amount + " items (ID:" + item_id + ") added to delivery " + deliveryID);
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleTxt.setText("Add Item to delivery:" + deliveryID);
        ArrayList<String> itemID = getAllItemsInOrder(deliveryID);
        itemsIDChoice.getItems().addAll(itemID);
        if(itemID.size() > 0){
            itemsIDChoice.setValue(itemID.get(0));
        }
    }

    public ArrayList<String> getAllItemsInOrder(int deliveryID){
        ArrayList<String> result = new ArrayList<>();
        try {

            Connection connection = new SQL().getConnection();
            String sql = "select * from items_that_need_to_send_with_delivery where delivery_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, deliveryID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                result.add(rs.getInt(ITEM_ID ) +"");
            }
            statement.close();
            return result;
        }catch (Exception exception){
            return null;
        }
    }

    public int getMaxAmountOfItemInDelivery(int deliveryID, int itemID){
        try{
            int maxAmount;
           Connection connection = new SQL().getConnection();
           String sql = String.format("SELECT %S(?,?) as amount FROM DUAL", MAX_AMOUNT_FOR_ITEM_THAT_CAN_DELIVERED);
           PreparedStatement statement = connection.prepareStatement(sql);
           statement.setInt(1, deliveryID);
           statement.setInt(2, itemID);
           ResultSet resultSet = statement.executeQuery();
           resultSet.next();
           maxAmount = resultSet.getInt("amount");
           statement.close();
            return maxAmount;
        }catch (Exception exception){
            return 0;
        }

    }
}

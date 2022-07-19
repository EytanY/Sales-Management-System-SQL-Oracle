package com.example.demo;


import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.net.URL;
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

            Connection connection = new SQL().getConnection();
            String sqlCheck = String.format("INSERT INTO %s(%s, %s, %s) " +
                            "SELECT ?, ? , 0 " +
                            "  FROM DUAL " +
                            " WHERE NOT EXISTS (SELECT NULL  FROM %s  WHERE  %s = ? AND  %s = ?)",
                    ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_DELIVERY_ID, ITEMS_IN_DELIVERY_ITEM_ID, ITEMS_IN_DELIVERY_AMOUNT,
                    ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_DELIVERY_ID, ITEMS_IN_DELIVERY_ITEM_ID) ;
            PreparedStatement checkItemInDeliveryStatement = connection.prepareStatement(sqlCheck);
            checkItemInDeliveryStatement.setInt(1, deliveryID);
            checkItemInDeliveryStatement.setInt(2, item_id);
            checkItemInDeliveryStatement.setInt(3, deliveryID);
            checkItemInDeliveryStatement.setInt(4, item_id);
            checkItemInDeliveryStatement.executeUpdate();

            String sql = String.format("UPDATE %s SET %s = %s + ? WHERE %s = ? AND %s = ?"
                    ,ITEMS_IN_DELIVERY_TABLE, ITEMS_IN_DELIVERY_AMOUNT,ITEMS_IN_DELIVERY_AMOUNT ,
                    ITEMS_IN_DELIVERY_DELIVERY_ID, ITEMS_IN_DELIVERY_ITEM_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, amount);
            statement.setInt(2, deliveryID);
            statement.setInt(3,item_id);
            statement.executeUpdate();
            resultLabel.setText("SUCCESSES");
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
            String sql = String.format("Select %s.%s as %s " +
                    "from %s  INNER JOIN %s on %s.%s = %s.%s " +
                    "INNER join %s on %s.%s = %s.%s " +
                    "where %s = ? " +
                    "and %s.%s = %s.%s "
            ,STOCK_TABLE, STOCK_ITEM_ID, ITEM_ID,ITEMS_IN_ORDERS_TABLE, DELIVERY_TABLE, ITEMS_IN_ORDERS_TABLE,
                    ITEMS_IN_ORDERS_ORDER_ID, DELIVERY_TABLE, DELIVERY_ORDER_ID, STOCK_TABLE, STOCK_TABLE,
                    STOCK_ITEM_ID, ITEMS_IN_ORDERS_TABLE, ITEMS_IN_ORDERS_ITEM_ID, DELIVERY_ID, STOCK_TABLE,
                    STOCK_WAREHOUSE_ID, DELIVERY_TABLE, DELIVERY_WAREHOUSE_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, deliveryID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                result.add(rs.getInt(ITEM_ID ) +"");
            }
            statement.close();
            connection.close();
            return result;
        }catch (Exception exception){
            return null;
        }
    }

    public int getMaxAmountOfItemInDelivery(int deliveryID, int itemID){
        try{
            int maxAmount;
           Connection connection = new SQL().getConnection();
           String sql = String.format("SELECT %S(?,?) as amount FROM DUAL", NUM_OF_ITEMS_TO_SEND_FUNC);
           PreparedStatement statement = connection.prepareStatement(sql);
           statement.setInt(1, deliveryID);
           statement.setInt(2, itemID);
           ResultSet resultSet = statement.executeQuery();
           resultSet.next();
           maxAmount = resultSet.getInt("amount");
           statement.close();
           connection.close();
            return maxAmount;
        }catch (Exception exception){
            return 0;
        }

    }
}

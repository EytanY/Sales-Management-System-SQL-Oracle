package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public abstract class Controller {
    public static int deliveryID;
    public static Connection connection = new SQL().getConnection();
    //Item attributes
    public final String ITEM_TABLE = "ITEMS";
    public final String ITEM_DESCRIPTION = "ITEM_DESCRIPTION";
    public final String ITEM_ID = "ITEM_ID";
    public final String ITEM_PRICE = "PRICE";
    //Warehouse attributes
    public final String WAREHOUSE_TABLE = "WAREHOUSES";
    public final String WAREHOUSE_ID = "WAREHOUSES_ID";
    public final String WAREHOUSE_NAME = "WAREHOUSES_NAME";
    //Stock attributes
    public final String STOCK_TABLE = "STOCK";
    public final String STOCK_ITEM_ID = "ITEM_ID";
    public final String STOCK_AMOUNT = "AMOUNT";
    public final String STOCK_WAREHOUSE_ID = "WAREHOUSE_ID";
    //Order attributes
    public final String ORDER_TABLE = "ORDERS";
    public final String ORDER_ID = "ORDER_ID";
    public final String ORDER_CUSTOMER_ID = "COSTUMER_ID";
    public final String ORDER_DATE = "ORDER_DATE";
    public final String ORDER_STATUS = "STATUS";
    //Customer attributes
    public final String CUSTOMER_TABLE = "CUSTOMERS";
    public final String CUSTOMER_ID = "CUSTOMER_ID";
    public final String CUSTOMER_FIRST_NAME = "FIRST_NAME";
    public final String CUSTOMER_LAST_NAME = "LAST_NAME";
    // Items In Order attributes
    public final String ITEMS_IN_ORDERS_TABLE = "ITEMS_IN_ORDERS";
    public final String ITEMS_IN_ORDERS_ORDER_ID = "ORDER_ID";
    public final String ITEMS_IN_ORDERS_ITEM_ID = "ITEM_ID";
    public final String ITEMS_IN_ORDERS_AMOUNT = "AMOUNT";
    //Delivery attributes
    public final String DELIVERY_TABLE = "DELIVERY";
    public final String DELIVERY_ID = "DELIVERY_ID";
    public final String DELIVERY_WAREHOUSE_ID = "WAREHOUSES_ID";
    public final String DELIVERY_ORDER_ID = "ORDER_ID";
    public final String DELIVERY_TYPE = "DELIVERY_TYPE";
    public final String DELIVERY_DATE = "DELIVERY_DATE";
    // Items In Delivery attributes
    public final String ITEMS_IN_DELIVERY_TABLE = "ITEMS_IN_DELIVERY";
    public final String ITEMS_IN_DELIVERY_ITEM_ID = "ITEM_ID";
    public final String ITEMS_IN_DELIVERY_DELIVERY_ID = "DELIVERY_ID";
    public final String ITEMS_IN_DELIVERY_AMOUNT = "AMOUNT";

    // Functions
    public final String NUM_OF_ITEMS_TO_SEND_FUNC = "NUM_OF_ITEMS_TO_SEND";
    public final String GET_TOTAL_PRICE_OF_ORDER = "GET_TOTAL_PRICE_OF_ORDER";
    public final String ITEM_TOTAL_AMOUNT_FUNC = "ITEM_TOTAL_AMOUNT";
    public final String NUM_OF_ITEMS_TO_SEND = "NUM_OF_ITEMS_TO_SEND";
    @FXML
    private Button menuButton;


    public void changeScene(Button button, String title, String resource) throws IOException {
        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(resource));
        Scene scene = new Scene(fxmlLoader.load(), 350, 630);
        stage.setTitle("Project Database - " + title);
        stage.setScene(scene);
        stage.show();
    }
    public void onReturnToMenuButtonClick() throws IOException {
        changeScene(menuButton, "Menu", "menu-view.fxml");
    }

    public boolean validSyntax(String sql){
        return sql.indexOf(';') == -1;
    }


    public ArrayList<String> getColFromTable(String colName, String tableName){
        try {
            String sql = String.format("SELECT %s FROM %s", colName, tableName);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<String> arrayList = new ArrayList<>();
            while (resultSet.next()){
                arrayList.add(resultSet.getInt(colName) + "");
            }
            statement.close();
            return arrayList;
        }catch (Exception exception){
            return null;
        }

    }

    public int getTotalAmountItem(int itemID){
        try {
            String sql = String.format("SELECT %s(?) FROM DUAL", ITEM_TOTAL_AMOUNT_FUNC);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, itemID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int amount = resultSet.getInt(1);
            statement.close();
            return amount;
        }catch (Exception exception){
            return 0;
        }
    }
}

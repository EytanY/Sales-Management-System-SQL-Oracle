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
    public final String GET_TOTAL_PRICE_OF_ORDER_FUNC = "GET_TOTAL_PRICE_OF_ORDER";
    public final  String IS_ORDER_DONE_FUNC = "IS_ORDER_DONE";
    public final String MAX_AMOUNT_FOR_ITEM_THAT_CAN_DELIVERED = "MAX_AMOUNT_FOR_ITEM_THAT_CAN_DELIVERED";
    public final String NUM_OF_ITEMS_TO_SEND_FUNC = "NUM_OF_ITEMS_TO_SEND";
    public final String GET_TOTAL_PRICE_OF_DELIVERY_FUNC = "GET_TOTAL_PRICE_OF_DELIVERY";
    public final String GET_TOTAL_PRICE_OF_ORDER_THAT_SEND_FUNC = "GET_TOTAL_PRICE_OF_ORDER_THAT_SEND";

    // Procedure
    public final String ADD_ITEM_TO_STOCK_PR = "ADD_ITEM_TO_STOCK_PR";
    @FXML
    private Button menuButton;


    public void changeScene(Button button, String title, String resource) throws IOException {
        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(resource));
        Scene scene = new Scene(fxmlLoader.load(), 350, 650);
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

    public String printInvoiceOfDelivery(int deliveryID){
        try {
            float totalPrice = 0;
            String sql = ("select * from invoices where delivery_id = ?");
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, deliveryID);
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
            String sql = "select delivery_id, get_total_price_of_order_that_send(order_id) from delivery where order_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderID);
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

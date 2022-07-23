package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CostumerOrdersController extends Controller{
    @FXML
    public Label resultLabel;
    @FXML
    public TextField customerIDTF;

    public void onSearchCustomerButtonClick() {
        try {
            String sql = String.format("select %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s(%s.%s) as price  " +
                    "FROM %s LEFT JOIN %s ON %s.%s = %s.%s " +
                    "WHERE %s.%s  = ? ",
                    CUSTOMER_TABLE, CUSTOMER_FIRST_NAME, CUSTOMER_TABLE, CUSTOMER_LAST_NAME, ORDER_TABLE, ORDER_DATE, ORDER_TABLE, ORDER_ID, ORDER_TABLE, ORDER_STATUS,
                    GET_TOTAL_PRICE_OF_ORDER_FUNC, ORDER_TABLE, ORDER_ID, CUSTOMER_TABLE, ORDER_TABLE, ORDER_TABLE, ORDER_CUSTOMER_ID, CUSTOMER_TABLE,
                    CUSTOMER_ID, CUSTOMER_TABLE, CUSTOMER_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(customerIDTF.getText()));
            ResultSet rs = statement.executeQuery();
            System.out.println(sql);
            StringBuilder sb = new StringBuilder();
            boolean hasInfo = false;
            while (rs.next()){
                if(!hasInfo){
                    sb.append("Customer's Name:").append(rs.getString(CUSTOMER_FIRST_NAME)).append(" ").append(rs.getString(CUSTOMER_LAST_NAME)).append("\n\n");
                    sb.append("Orders:\n");
                    hasInfo = true;
                }
                if(rs.getInt(ORDER_ID) != 0){
                    sb.append("Order ID:").append(rs.getInt(ORDER_ID));
                    sb.append(" ,Date:").append(rs.getDate(ORDER_DATE));
                    sb.append(" ,Status:").append(rs.getInt(ORDER_STATUS));
                    sb.append(" ,Total Price:").append(rs.getFloat("price")).append("$\n");
                }


            }
            statement.close();
            if(sb.toString().equals("")){
                resultLabel.setText("Not Found!");
            }
            else {
                resultLabel.setText(sb.toString());
            }
        }catch (Exception exception){
            resultLabel.setText("Error!");
        }

    }
}

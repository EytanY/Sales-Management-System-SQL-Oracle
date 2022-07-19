package com.example.demo;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ReportProductsController extends Controller implements Initializable {


    public Label availableProductsLabel;
    public Label availableProductsInWarehousesLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        availableProductsLabel.setText(getAvailableProducts());
        availableProductsInWarehousesLabel.setText(getAvailableProductsByWarehouses());
    }


    public String getAvailableProducts() {
        try {
            String sql = String.format("SELECT  %s.%s, %s.%s, %s.%s ,NVL(sum(%s), 0) as %s  " +
                    "FROM %s LEFT  JOIN %s " +
                    "ON %s.%s = %s.%s " +
                    "GROUP BY %s.%s, %s.%s, %s.%s " +
                    "HAVING NVL(sum(%s), 0) > 0 "+
                    "ORDER BY 4 DESC",
                    ITEM_TABLE, ITEM_ID, ITEM_TABLE, ITEM_PRICE, ITEM_TABLE, ITEM_DESCRIPTION, STOCK_AMOUNT, STOCK_AMOUNT,
                    ITEM_TABLE, STOCK_TABLE, STOCK_TABLE, STOCK_ITEM_ID, ITEM_TABLE, ITEM_ID, ITEM_TABLE, ITEM_ID, ITEM_TABLE, ITEM_PRICE,
                    ITEM_TABLE, ITEM_DESCRIPTION, STOCK_AMOUNT);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Item ID:").append(rs.getInt(ITEM_ID));
                sb.append(" ,Description:").append(rs.getString(ITEM_DESCRIPTION));
                sb.append(" ,Price:").append(rs.getFloat(ITEM_PRICE));

                sb.append(" ,Total Amount:").append(rs.getInt(STOCK_AMOUNT)).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Error!";
        }
    }

    public String getAvailableProductsByWarehouses(){
        try {
            String sql = String.format("SELECT  * " +
                    "FROM %s LEFT JOIN %s ON %s.%s = %s.%s " +
                    "WHERE %s.%s > 0 " +
                    "order by %s.%s ",
                    WAREHOUSE_TABLE, STOCK_TABLE, STOCK_TABLE, STOCK_WAREHOUSE_ID, WAREHOUSE_TABLE, WAREHOUSE_ID,
                    STOCK_TABLE, STOCK_AMOUNT, STOCK_TABLE, STOCK_WAREHOUSE_ID);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            int warehouseID = 0;
            boolean hasInfo = false;
            while (rs.next()) {

                if(warehouseID != rs.getInt(1))
                    hasInfo = false;
                if(!hasInfo) {
                    sb.append("\n");
                    warehouseID = rs.getInt(1);
                    sb.append("Warehouse ID:").append(warehouseID).append(" ,Name:").append(rs.getString(2)).append("\n");
                    sb.append("Items:").append("\n");
                    hasInfo = true;
                }
                if(rs.getInt(4) != 0){
                    sb.append("Item ID:").append(rs.getInt(4));
                    sb.append(" ,Amount:").append(rs.getString(5)).append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "Error!";
        }
    }


}
package com.example.demo;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.net.URL;
import java.sql.Connection;
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
            Connection connection = new SQL().getConnection();
            String sql = "SELECT  items.item_id, items.price, items.item_description ,NVL(sum(amount), 0) as amount  " +
                    "FROM items LEFT  JOIN stock " +
                    "ON stock.item_id = items.item_id " +
                    "GROUP BY items.item_id, items.price, items.item_description " +
                    "HAVING NVL(sum(amount), 0) > 0 "+
                    "ORDER BY 4 DESC";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Item ID:").append(rs.getInt("item_id"));
                sb.append(" ,Description:").append(rs.getString("item_description"));
                sb.append(" ,Price:").append(rs.getFloat("price"));

                sb.append(" ,Total Amount:").append(rs.getInt("amount")).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Error!";
        }
    }

    public String getAvailableProductsByWarehouses(){
        try {
            Connection connection = new SQL().getConnection();
            String sql = "SELECT  * " +
                    "FROM warehouses LEFT JOIN stock ON stock.warehouse_id = warehouses.warehouses_id " +
                    "WHERE STOCK.amount > 0 " +
                    "order by stock.warehouse_id ";
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
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
            String sql = "select * from items_in_stock";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Item ID:").append(rs.getInt(1));
                sb.append(" ,Description:").append(rs.getString(3));
                sb.append(" ,Price:").append(rs.getFloat(2));

                sb.append(" ,Total Amount:").append(rs.getInt(4)).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Error!";
        }
    }

    public String getAvailableProductsByWarehouses(){
        try {
            String sql = "select * from items_in_warehouses";
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
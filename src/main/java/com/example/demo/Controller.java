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

    public final String ITEM_TABLE = "ITEMS";
    public final String ITEM_ID = "ITEM_ID";
    public final String ITEM_PRICE = "PRICE";
    public final String WAREHOUSE_TABLE = "WAREHOUSES";
    public final String WAREHOUSE_ID = "WAREHOUSES_ID";
    public final String WAREHOUSE_NAME = "WAREHOUSES_NAME";

    public final String STOCK_TABLE = "STOCK";
    public final String STOCK_ITEM_ID = "ITEM_ID";
    public final String STOCK_AMOUNT = "AMOUNT";
    public final String STOCK_WAREHOUSE_ID = "WAREHOUSE_ID";
    @FXML
    private Button menuButton;


    public void changeScene(Button button, String title, String resource) throws IOException {
        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(resource));
        Scene scene = new Scene(fxmlLoader.load(), 350, 620);
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
            Connection connection = new SQL().getConnection();
            String sql = String.format("SELECT %s FROM %s", colName, tableName);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<String> arrayList = new ArrayList<>();
            while (resultSet.next()){
                arrayList.add(resultSet.getInt(colName) + "");
            }
            return arrayList;
        }catch (Exception exception){
            return null;
        }

    }
}

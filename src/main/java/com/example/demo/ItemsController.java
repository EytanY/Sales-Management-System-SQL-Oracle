package com.example.demo;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ItemsController extends Controller {

    public TextField itemIDTF;
    public TextField itemDecTF;
    public TextField itemPriceTF;
    public Button addNewItemButton;
    public TextField searchItemIDTF;
    public Button searchItemButton;
    public Label resultLabel;
    public Button showAllItemsButton;


    public void onAddNewItemButtonClick() {
        try {
            String query = "INSERT INTO items (item_id, item_description, price) VALUES(?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(itemIDTF.getText()));
            statement.setString(2, itemDecTF.getText());
            statement.setFloat(3, Float.parseFloat(itemPriceTF.getText()));
            statement.executeUpdate();
            resultLabel.setText("Item " + itemIDTF.getText() + " inserted successfully!");
            statement.close();
        }catch (Exception exception){
            resultLabel.setText("Error- check the input(ID already used,price not a number...)");
        }
    }
    public void onSearchItemButtonClick() {
        try{
            String sql = "SELECT * FROM items WHERE item_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(searchItemIDTF.getText()));
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Item ID:").append(rs.getString("item_id")).append("\n");
                sb.append("Description:").append(rs.getString("item_description")).append("\n");
                sb.append("Price:").append(rs.getString("price")).append("\n");

            }
            resultLabel.setText(sb.toString());
            if(sb.toString().equals(""))
                resultLabel.setText("Not Found");

            rs.close();
            statement.close();
        }
        catch (Exception exception){
            resultLabel.setText("Invalid Connection");
        }
    }
    public void onShowAllItemsButtonClick() {
        try{
            String sql = "SELECT * FROM items";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Item ID:").append(rs.getString("item_id"));
                sb.append(" , Description:").append(rs.getString("item_description"));
                sb.append(" , Price:").append(rs.getString("price")).append("\n");

            }
            resultLabel.setText(sb.toString());
            if(sb.toString().equals(""))
                resultLabel.setText("Empty");

            rs.close();
            statement.close();
        }
        catch (Exception exception){
            resultLabel.setText("Invalid Connection");
        }

    }
}

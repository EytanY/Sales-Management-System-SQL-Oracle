package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.*;


public class CustomersController extends Controller{

    @FXML
    public TextField firstName;
    @FXML
    public TextField lastName;
    @FXML
    public Label resultLabel;
    @FXML
    public TextField costumerID;
    @FXML
    public Button addNewCustomerButton;
    @FXML
    public Button searchCostumerButton;
    @FXML
    public Button removeCostumerButton;
    @FXML
    public Label removeLabel;
    @FXML
    public Button showAllCostumersButton;

    @FXML
    public void onAddNewCustomerButtonClick(ActionEvent actionEvent) {
        String firstNameStr = firstName.getText();
        String lastNameStr = lastName.getText();

        if(InvalidName(firstNameStr) || InvalidName(lastNameStr))
        {
            resultLabel.setText("Invalid name!");
        }

        try{
            Connection connection = new SQL().getConnection();
            String query = "INSERT INTO customers (first_name, last_name) VALUES(?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, firstNameStr);
            statement.setString(2, lastNameStr);
            statement.executeUpdate();


            String sql1 = "SELECT max(customer_id) from customers";
            PreparedStatement statement1 = connection.prepareStatement(sql1);
            ResultSet rs =  statement1.executeQuery();
            rs.next();
            resultLabel.setText("SUCCESS! New Customer ID:" + rs.getInt("max(customer_id)"));

            statement.close();
            statement1.close();
            connection.close();
        }catch (Exception exception){
            resultLabel.setText("Error");
        }

    }

    public void onSearchCostumerButtonClick(ActionEvent actionEvent) {
        String firstNameStr = firstName.getText();
        String lastNameStr = lastName.getText();

        if(InvalidName(firstNameStr) || InvalidName(lastNameStr))
        {
            resultLabel.setText("Invalid name!");
        }
        try{
            Connection connection = new SQL().getConnection();
            String sql = "SELECT * FROM customers WHERE first_name = ? AND last_name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, firstNameStr);
            statement.setString(2, lastNameStr);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Customer ID:").append(rs.getString("customer_id"));
                sb.append(" First Name:").append(rs.getString("first_name"));
                sb.append(" Last Name:").append(rs.getString("last_name")).append("\n");
            }
            resultLabel.setText(sb.toString());
            if(sb.toString().equals(""))
                resultLabel.setText("Not Found");
            rs.close();
            statement.close();
            connection.close();
        }
        catch (Exception exception){
            resultLabel.setText("Invalid Connection");

        }
    }

    public void onRemoveCostumerButtonClick(ActionEvent actionEvent) {
        String costumerIDStr =  costumerID.getText();
        try {
            Connection connection = new SQL().getConnection();
            String rmSql = "DELETE FROM customers WHERE customer_id = ?";
            PreparedStatement rmStatement = connection.prepareStatement(rmSql);
            rmStatement.setString(1, costumerIDStr);
            rmStatement.executeUpdate();
            removeLabel.setText("SUCCESSES");
            rmStatement.close();
            connection.close();
        }catch (Exception exception){
            removeLabel.setText("Invalid Connection");
        }
    }

    public boolean InvalidName(String name){
        return name.indexOf(';') != -1;
    }

    public void onShowAllCostumersButtonClick(ActionEvent actionEvent) {
        try{
            Connection connection = new SQL().getConnection();
            String sql = "SELECT * FROM customers";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Customer ID:").append(rs.getString("customer_id"));
                sb.append(" First Name:").append(rs.getString("first_name"));
                sb.append(" Last Name:").append(rs.getString("last_name")).append("\n");
            }
            resultLabel.setText(sb.toString());
            if(sb.toString().equals(""))
                resultLabel.setText("Not Found");
            rs.close();
            statement.close();
            connection.close();
        }
        catch (Exception exception){
            resultLabel.setText("Invalid Connection");

        }
    }
}

package com.example.demo;


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
    public Button addNewCustomerButton;
    @FXML
    public Button searchCostumerButton;
    @FXML
    public Button showAllCostumersButton;

    @FXML
    public void onAddNewCustomerButtonClick() {
        String firstNameStr = firstName.getText();
        String lastNameStr = lastName.getText();

        try {
            String sql = "{CALL ADD_CUSTOMER(?, ?, ?)}";
            CallableStatement statement = connection.prepareCall(sql);
            statement.setString(1, firstNameStr);
            statement.setString(2, lastNameStr);
            statement.registerOutParameter(3, Types.INTEGER);
            statement.execute();
            resultLabel.setText("SUCCESS! New Customer ID:" + statement.getInt(3));

            statement.close();
        }catch (SQLException exception){
            resultLabel.setText(exception.getMessage());
        }catch (Exception exception){
            resultLabel.setText("Error");
        }

    }

    public void onSearchCostumerButtonClick() {
        String firstNameStr = firstName.getText();
        String lastNameStr = lastName.getText();

        try{
            connection = new SQL().getConnection();

            String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?"
            , CUSTOMER_TABLE, CUSTOMER_FIRST_NAME, CUSTOMER_LAST_NAME);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, firstNameStr);
            statement.setString(2, lastNameStr);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Customer ID:").append(rs.getString(CUSTOMER_ID));
                sb.append(" First Name:").append(rs.getString(CUSTOMER_FIRST_NAME));
                sb.append(" Last Name:").append(rs.getString(CUSTOMER_LAST_NAME)).append("\n");
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

    public void onShowAllCostumersButtonClick() {
        try{
            connection = new SQL().getConnection();
            String sql = String.format("SELECT * FROM %s", CUSTOMER_TABLE);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()){
                sb.append("Customer ID:").append(rs.getString(CUSTOMER_ID));
                sb.append(" First Name:").append(rs.getString(CUSTOMER_FIRST_NAME));
                sb.append(" Last Name:").append(rs.getString(CUSTOMER_LAST_NAME)).append("\n");
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
}

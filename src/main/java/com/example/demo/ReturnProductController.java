package com.example.demo;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;

public class ReturnProductController extends Controller{
    @FXML
    public TextField deliveryIDTF;
    @FXML
    public TextField itemIDTF;
    @FXML
    public Label resultLabel;
    @FXML
    public void onReturnProductButtonClick() {
        try{


        }catch (Exception exception){
            resultLabel.setText("Error!");
        }
    }
}

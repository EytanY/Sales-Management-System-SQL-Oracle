package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.ImagePattern;

import java.io.IOException;

public class MenuController extends Controller {
    @FXML
    public Button itemsButton;
    @FXML
    public Button warehouseButton;
    @FXML
    public Button ordersButton;
    @FXML
    public Button deliveriesButton;
    @FXML
    public Button customersButton;
    @FXML
    public Button availableProductsButton;
    @FXML
    public Button customerOrderButton;
    @FXML
    public Button searchOrderByDateButton;
    @FXML
    public Button openOrdersButton;

    @FXML
    public void onCostumersButtonClick() throws IOException {
        changeScene(customersButton, "Costumers", "customers-view.fxml");
    }

    @FXML
    public void onRemoveButtonClick() {
        //code
    }

    public void onItemsButtonClick(ActionEvent actionEvent) throws IOException{
        changeScene(itemsButton, "Items", "items-view.fxml");
    }

    public void onWarehouseButtonClick(ActionEvent actionEvent) throws IOException{
        changeScene(warehouseButton, "Warehouse And Stock", "warehouse-view.fxml");
    }

    public void onOrdersButtonClick(ActionEvent actionEvent) throws IOException {
        changeScene(ordersButton, "Orders", "orders-view.fxml");
    }

    public void onDeliveriesButtonClick(ActionEvent actionEvent) throws IOException {
        changeScene(deliveriesButton, "Deliveries", "deliveries-view.fxml");
    }

    public void onAvailableProductsButtonClick(ActionEvent actionEvent) throws IOException{
        changeScene(availableProductsButton, "Available Products", "report-products-view.fxml");
    }

    public void onCustomerOrdersButtonClick(ActionEvent actionEvent) throws IOException{
        changeScene(customerOrderButton, "Customer's Orders", "customer-orders-view.fxml");
    }

    public void onSearchOrdersByDateButtonClick(ActionEvent actionEvent) throws IOException {
        changeScene(searchOrderByDateButton, "Orders by Date", "report-orders-date-view.fxml");
    }

    public void onAllOpenOrdersButtonClick(ActionEvent actionEvent) throws IOException {
        changeScene(openOrdersButton, "Open Orders", "report-open-orders-view.fxml");
    }
}
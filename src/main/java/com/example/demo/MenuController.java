package com.example.demo;



import javafx.fxml.FXML;
import javafx.scene.control.Button;

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
    public Button invoicesButton;

    @FXML
    public void onCostumersButtonClick() throws IOException {
        changeScene(customersButton, "Costumers", "customers-view.fxml");
    }

    @FXML
    public void onRemoveButtonClick() {
        //code
    }

    public void onItemsButtonClick() throws IOException{
        changeScene(itemsButton, "Items", "items-view.fxml");
    }

    public void onWarehouseButtonClick() throws IOException{
        changeScene(warehouseButton, "Warehouse And Stock", "warehouse-view.fxml");
    }

    public void onOrdersButtonClick() throws IOException {
        changeScene(ordersButton, "Orders", "orders-view.fxml");
    }

    public void onDeliveriesButtonClick() throws IOException {
        changeScene(deliveriesButton, "Deliveries", "deliveries-view.fxml");
    }

    public void onAvailableProductsButtonClick() throws IOException{
        changeScene(availableProductsButton, "Available Products", "report-products-view.fxml");
    }

    public void onCustomerOrdersButtonClick() throws IOException{
        changeScene(customerOrderButton, "Customer's Orders", "customer-orders-view.fxml");
    }

    public void onSearchOrdersByDateButtonClick() throws IOException {
        changeScene(searchOrderByDateButton, "Orders by Date", "report-orders-date-view.fxml");
    }

    public void onAllOpenOrdersButtonClick() throws IOException {
        changeScene(openOrdersButton, "Open Orders", "report-open-orders-view.fxml");
    }

    public void onInvoicesButtonClick() throws IOException {
        changeScene(openOrdersButton, "Invoices", "invoices-view.fxml");

    }
}
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
    public Button returnProductButton;
    @FXML
    public Button unfinishedOrdersButton;

    @FXML
    public void onCostumersButtonClick() throws IOException {
        changeScene(customersButton, "Costumers", "customers-view.fxml");
    }

    @FXML
    public void onItemsButtonClick() throws IOException{
        changeScene(itemsButton, "Items", "items-view.fxml");
    }
    @FXML
    public void onWarehouseButtonClick() throws IOException{
        changeScene(warehouseButton, "Warehouse And Stock", "warehouse-view.fxml");
    }
    @FXML
    public void onOrdersButtonClick() throws IOException {
        changeScene(ordersButton, "Orders", "orders-view.fxml");
    }
    @FXML
    public void onDeliveriesButtonClick() throws IOException {
        changeScene(deliveriesButton, "Deliveries", "deliveries-view.fxml");
    }
    @FXML
    public void onAvailableProductsButtonClick() throws IOException{
        changeScene(availableProductsButton, "Available Products", "report-products-view.fxml");
    }
    @FXML
    public void onCustomerOrdersButtonClick() throws IOException{
        changeScene(customerOrderButton, "Customer's Orders", "customer-orders-view.fxml");
    }
    @FXML
    public void onSearchOrdersByDateButtonClick() throws IOException {
        changeScene(searchOrderByDateButton, "Orders by Date", "report-orders-date-view.fxml");
    }
    @FXML
    public void onAllOpenOrdersButtonClick() throws IOException {
        changeScene(openOrdersButton, "Open Orders", "report-open-orders-view.fxml");
    }
    @FXML
    public void onInvoicesButtonClick() throws IOException {
        changeScene(openOrdersButton, "Invoices", "invoices-view.fxml");

    }
    @FXML
    public void onReturnProductButtonClick() throws IOException {
        changeScene(returnProductButton, "Return Product", "remove-view.fxml");
    }

    public void onUnfinishedOrdersButtonClick() throws IOException {
        changeScene(unfinishedOrdersButton, "Unfinished Orders", "unfinished-orders-view.fxml");
    }
}
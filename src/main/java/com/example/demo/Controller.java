package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public abstract class Controller {
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
}

package com.adr.imagechooser;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public class FXMLController {
    
    @FXML private ResourceBundle resources;
    @FXML private URL location;

    @FXML
    private ImageChooser imagechooser;
    
    @FXML
    void handleButtonAction(ActionEvent event) {
        imagechooser.chooseImage();
    }

    @FXML
    void clearAction(ActionEvent event) {
        imagechooser.setImage(null);
    }

    
    @FXML
    public void initialize() {   
    }    
}
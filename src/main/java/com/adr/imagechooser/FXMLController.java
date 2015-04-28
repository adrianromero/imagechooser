//    AwesomeIcon is a library to select and display images control for JavaFX
//    Copyright (C) 2014 Adrián Romero Corchado.
//
//    This file is part of ImageChooser
//
//     Licensed under the Apache License, Version 2.0 (the "License");
//     you may not use this file except in compliance with the License.
//     You may obtain a copy of the License at
//     
//         http://www.apache.org/licenses/LICENSE-2.0
//     
//     Unless required by applicable law or agreed to in writing, software
//     distributed under the License is distributed on an "AS IS" BASIS,
//     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//     See the License for the specific language governing permissions and
//     limitations under the License.

package com.adr.imagechooser;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public class FXMLController {
    
    @FXML private ResourceBundle resources;
    @FXML private URL location;

    
    @FXML private ImageChooser imagechooser;
    @FXML private ImageWebcam imagewebcam;
    
    @FXML
    void handleButtonAction(ActionEvent event) {
        imagechooser.chooseImage();
    }

    @FXML
    void clearAction(ActionEvent event) {
        imagechooser.setImage(null);
    }
    
    @FXML
    void onStartWebcam(ActionEvent event) {
        try {
            imagewebcam.startWebCam();
        } catch (ImageWebCamException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void onStopWebcam(ActionEvent event) {
        imagewebcam.stopWebCam();
    }

    
    @FXML
    public void initialize() {   
    }    
}
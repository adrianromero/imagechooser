//    AwesomeIcon is a library to select and display images control for JavaFX
//    Copyright (C) 2015 Adrián Romero Corchado.
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
//     limitations under the License

package com.adr.imagechooser;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author adrian
 */
public class ImageWebcam extends ImageView {
    
    private Webcam mywebcam;
    
    private volatile boolean displaying = false; 
    private Thread backgroundThread = null;
    
    public ImageWebcam() {  
        this(null);    
    } 
    
    public ImageWebcam(String name) {   
        for (Webcam webcam : Webcam.getWebcams()) {           
            if (name == null || name.equals(webcam.getName())) {
                mywebcam = webcam;
                if (mywebcam.isOpen()) {
                    mywebcam.close();
                }
                return;
            }
        }
        mywebcam = null;   
    }
    
    public boolean hasWebCam() {
        return mywebcam != null;
    }

    public void startWebCam() throws ImageWebCamException {
        
        if (mywebcam == null) {
            throw new ImageWebCamException("WebCam not found.") ;
        }
        if (isRunning()) {
            throw new ImageWebCamException("WebCam is running.");
        }
        
        setImage(null);
        backgroundThread = new Thread(() -> {
            
            displaying = true;
            Platform.runLater(() -> setBusy(true));
            try {
                mywebcam.open();
                
                while (displaying) {
                    BufferedImage bimg =  mywebcam.getImage();
                    if (bimg == null) {
                        throw new WebcamException("Cannot get image.");
                    } else {
                        Image image = SwingFXUtils.toFXImage(bimg, null);
                        bimg.flush();
                        Platform.runLater(() -> {
                            setImage(image);
                            setCapturing(true);
                        });
                    }
                } 
                Platform.runLater(() -> setCapturing(false));
                mywebcam.close();
                Platform.runLater(() -> setBusy(false));
            } catch (WebcamException ex) {
                Logger.getLogger(ImageWebcam.class.getName()).log(Level.SEVERE, "Web cam error", ex);
                displaying = false;
                Platform.runLater(() -> setCapturing(false));
                Platform.runLater(() -> setBusy(false));
            }                    
        });
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }
    
    public void stopWebCam() {
        displaying = false; 
    }
    
    public boolean isRunning() {
        return backgroundThread != null && backgroundThread.isAlive();
    }

    private ReadOnlyBooleanWrapper capturing = new ReadOnlyBooleanWrapper(false);
    private void setCapturing(boolean value) {
        capturing.set(value);
    }    
    public final boolean isCapturing() {
        return capturing.get();
    }
    public final ReadOnlyBooleanProperty capturingProperty() {
        return capturing.getReadOnlyProperty();
    }  
    
    private ReadOnlyBooleanWrapper busy = new ReadOnlyBooleanWrapper(false);
    private void setBusy(boolean value) {
        busy.set(value);
    }    
    public final boolean isBusy() {
        return busy.get();
    }
    public final ReadOnlyBooleanProperty busyProperty() {
        return busy.getReadOnlyProperty();
    }     
}

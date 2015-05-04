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
//     limitations under the License

package com.adr.imagechooser;

import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.imageio.ImageIO;

/**
 *
 * @author adrian
 */
public class ImageChooser extends AnchorPane {
    
    @FXML private ResourceBundle resources;
    
    @FXML private HBox  toolbar;
    @FXML private StackPane imagepane;
    @FXML private ImageView imageview;
    @FXML private StackPane panecam;
    @FXML private ImageWebcam imagecam;
    
    @FXML private Button choosebutton;
    @FXML private Button clearbutton;
    @FXML private Button webcambutton;
    @FXML private Button webcamtrigger;
    @FXML private Button webcamcancel;
    
    @FXML private StackPane flash;
    private Transition flashtransition;
    
    private FadeTransition fadetransition;
    private boolean hasmouse;
    private boolean hasfocus;
    
    private AudioClip triggerclip;
    
    public ImageChooser() {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adr/imagechooser/fxml/imagechooser.fxml"));
        loader.setResources(ResourceBundle.getBundle("com/adr/imagechooser/fxml/imagechooser"));
        loader.setController(this);
        loader.setRoot(this);
        
        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    @FXML
    public void initialize() {   
        
        triggerclip = new AudioClip(getClass().getResource("/com/adr/imagechooser/clips/51360__thecheeseman__camera-snap1.wav").toExternalForm());

        choosebutton.setGraphic(IconBuilder.create(FontAwesome.FA_FOLDER_O).build());
        clearbutton.setGraphic(IconBuilder.create(FontAwesome.FA_TRASH).build());
        webcambutton.setGraphic(IconBuilder.create(FontAwesome.FA_CAMERA).build());
        webcamtrigger.setGraphic(IconBuilder.create(FontAwesome.FA_CIRCLE).style("-fx-fill: green;").build());
        webcamcancel.setGraphic(IconBuilder.create(FontAwesome.FA_SQUARE).style("-fx-fill: red;").build());

        imageview.fitHeightProperty().bind(imagepane.heightProperty().subtract(imagemargin.multiply(2.0)));
        imageview.fitWidthProperty().bind(imagepane.widthProperty().subtract(imagemargin.multiply(2.0))); 
        imagecam.fitHeightProperty().bind(imagepane.heightProperty().subtract(imagemargin.multiply(2.0)));
        imagecam.fitWidthProperty().bind(imagepane.widthProperty().subtract(imagemargin.multiply(2.0)));    
        
        panecam.setStyle("-fx-background-color: BLACK");
               
        panecam.visibleProperty().bind(imagecam.busyProperty());
        imageview.visibleProperty().bind(imagecam.busyProperty().not());
        
        choosebutton.visibleProperty().bind(imagecam.busyProperty().not());
        clearbutton.visibleProperty().bind(imagecam.busyProperty().not());
        webcambutton.setDisable(!imagecam.hasWebCam());
        webcambutton.visibleProperty().bind(imagecam.busyProperty().not());
        webcamtrigger.visibleProperty().bind(imagecam.busyProperty());
        webcamtrigger.disableProperty().bind(imagecam.capturingProperty().not());
        webcamcancel.visibleProperty().bind(imagecam.busyProperty());    
        
        toolbar.setOpacity(0.0);
        fadetransition = new FadeTransition(Duration.millis(200), toolbar);
        fadetransition.setCycleCount(1);
        fadetransition.setInterpolator(Interpolator.EASE_BOTH);  
        
        flashtransition = createFlashTransition(flash);
        
        focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            hasfocus = newPropertyValue;
            buttonsVisibility();
        });        

        hasmouse = false;
        hasfocus = this.isFocused();
    }    
    
    @FXML
    void chooseAction(ActionEvent event) {
        requestFocus();
        chooseImage();
    }

    @FXML
    void clearAction(ActionEvent event) {
        requestFocus();
        clearImage();
    }
    
    @FXML
    void webcamAction(ActionEvent event) {
        requestFocus();
        try {
            imagecam.startWebCam();
        } catch (ImageWebCamException ex) {
            Logger.getLogger(ImageChooser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void onWebcamTrigger(ActionEvent event) {
        requestFocus();
        if (imagecam.isCapturing()) {
            setImage(imagecam.getImage());
            imagecam.stopWebCam();
            flashtransition.playFromStart();
            triggerclip.play();
        }
    }
    
    @FXML
    void onWebcamCancel(ActionEvent event) {
        requestFocus();
        imagecam.stopWebCam();
    }
    
    @FXML
    void keyPressedAction(KeyEvent event) {
        if (KeyCode.SPACE == event.getCode()) {
            chooseImage();
        } else if (KeyCode.DELETE == event.getCode()) {
            clearImage();
        }
    }    
    
    @FXML
    void mouseEnterAction(MouseEvent event) {
        hasmouse = true;
        buttonsVisibility();
    }
    
    @FXML
    void clickedAction(MouseEvent event) {
        requestFocus();
    }    
    
    @FXML
    void mouseExitAction(MouseEvent event) {
        hasmouse = false;
        buttonsVisibility();
    }
    
    void clearImage() {
        imagecam.stopWebCam();
        setImage(null);
    }
    
    void chooseImage() {
        
        imagecam.stopWebCam();
        
        Platform.runLater(() -> {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose image");
            fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
            );         
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(resources.getString("label.allimages"), "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
            );
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (file != null) {
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    Image image = SwingFXUtils.toFXImage(bufferedImage, null);                
                    setImage(image);
                } catch (IOException ex) {
                    Logger.getLogger(ImageChooser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    public void cancelWebCam() {
        imagecam.stopWebCam();
    }

    public final void setImage(Image value) {
        imageview.setImage(value);
    }
    public final Image getImage() {
        return imageview.getImage();
    }
    public final ObjectProperty<Image> imageProperty() {
        return imageview.imageProperty();
    }
    
    private DoubleProperty imagemargin = new SimpleDoubleProperty(10.0);

    public final void setImageMargin(double value) {
        imageMarginProperty().set(value);
    }
    
    public final double getImageMargin() {
        return imagemargin.get();
    }

    public final DoubleProperty imageMarginProperty() {
        return imagemargin;
    }    
    
    private void buttonsVisibility() {
        if (hasmouse || hasfocus) {
            fadetransition.setFromValue(toolbar.getOpacity());
            fadetransition.setToValue(1.0);
            fadetransition.playFromStart();            
        } else {
           fadetransition.setFromValue(toolbar.getOpacity());
           fadetransition.setToValue(0.0);
           fadetransition.playFromStart();           
        }
    }
    
    private Transition createFlashTransition(Node n) { 

        FadeTransition s1 = new FadeTransition(Duration.millis(50), n);
        s1.setInterpolator(Interpolator.EASE_OUT);
        s1.setFromValue(0.0);
        s1.setToValue(1.0);
        
        FadeTransition s2 = new FadeTransition(Duration.millis(600), n);
        s2.setInterpolator(Interpolator.EASE_BOTH);
        s2.setFromValue(1.0);
        s2.setToValue(0.0);
        
        return new SequentialTransition(s1, s2);
    }
}

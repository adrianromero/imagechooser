/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.imagechooser;

import com.adr.awesomeicon.AwesomeIcon;
import com.adr.awesomeicon.Icon;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 *
 * @author adrian
 */
public class ImageChooser extends AnchorPane {
    
    @FXML private ResourceBundle resources;
    
    @FXML private HBox  toolbar;
    @FXML private StackPane imagepane;
    @FXML private ImageView imageview;
    @FXML private Button choosebutton;
    @FXML private Button deletebutton;
    
    private FadeTransition fadetransition;
    
    public ImageChooser() {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/imagechooser.fxml"));
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
    void chooseAction(ActionEvent event) {
        chooseImage();
    }

    @FXML
    void clearAction(ActionEvent event) {
        setImage(null);
    }
    
    @FXML
    void keyPressedAction(KeyEvent event) {
        if (KeyCode.SPACE == event.getCode()) {
            chooseImage();
        } else if (KeyCode.DELETE == event.getCode()) {
            setImage(null);
        }
    }    
    
    @FXML
    void mouseEnterAction(MouseEvent event) {
        fadetransition.setFromValue(toolbar.getOpacity());
        fadetransition.setToValue(1.0);
        fadetransition.playFromStart();
    }
    
    @FXML
    void clickedAction(MouseEvent event) {
        requestFocus();
    }    
    
    @FXML
    void mouseExitAction(MouseEvent event) {
        fadetransition.setFromValue(toolbar.getOpacity());
        fadetransition.setToValue(0.0);
        fadetransition.playFromStart();
    }
    
    void chooseImage() {
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
                setImage(new Image(file.toURI().toURL().toString()));
            } catch (MalformedURLException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
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
    
    private DoubleProperty imagemargin = new SimpleDoubleProperty(2.0);

    public final void setImageMargin(double value) {
        imageMarginProperty().set(value);
    }
    public final double getImageMargin() {
        return imagemargin.get();
    }

    public final DoubleProperty imageMarginProperty() {
        return imagemargin;
    }    
    
    @FXML
    public void initialize() {   
        
        toolbar.setOpacity(0.0);
        
        choosebutton.setGraphic(new Icon(AwesomeIcon.FOLDER_O, 14.0));
        deletebutton.setGraphic(new Icon(AwesomeIcon.TRASH, 14.0));
        
        imageview.fitHeightProperty().bind(imagepane.heightProperty().subtract(imagemargin.multiply(2.0)));
        imageview.fitWidthProperty().bind(imagepane.widthProperty().subtract(imagemargin.multiply(2.0)));       
        
        fadetransition = new FadeTransition(Duration.millis(200), toolbar);
        fadetransition.setCycleCount(1);
        fadetransition.setInterpolator(Interpolator.EASE_BOTH);        
    }    
}

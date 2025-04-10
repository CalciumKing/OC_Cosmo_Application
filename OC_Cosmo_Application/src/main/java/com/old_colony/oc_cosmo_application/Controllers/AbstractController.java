package com.old_colony.oc_cosmo_application.Controllers;

import com.old_colony.oc_cosmo_application.DataClasses.User;
import com.old_colony.oc_cosmo_application.MainApplication;
import com.old_colony.oc_cosmo_application.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class AbstractController {
    @FXML
    protected AnchorPane main_pane;
    protected boolean isMaximized;
    private double xOffset, yOffset, defaultWidth, defaultHeight;
    
    
    protected void changeScene(String sceneName, User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(sceneName));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image("AppIcon.png"));
            
            if (user != null) { // entering application
                stage.setTitle("Cosmetology Application | " + user.getUsername());
                DashboardController dashboardController = fxmlLoader.getController();
                dashboardController.welcome(user, isMaximized);
            } else // exiting application
                stage.setTitle("Cosmetology Application | Login");
            
            stage.show();
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Scene Error",
                    "Error Changing Scene",
                    "There was an error changing scenes, please try again"
            );
            e.printStackTrace();
        }
    }
    
    // region Window Methods
    @FXML
    protected void windowMinimize(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }
    
    @FXML
    protected void windowClose() {
        Platform.exit();
    }
    
    @FXML
    protected void windowClick(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }
    
    @FXML
    protected void windowDrag(MouseEvent event) {
        if (isMaximized)
            windowMaximize(); // undoing maximization
        
        Stage stage = (Stage) main_pane.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
    
    @FXML
    protected void windowMaximize() {
        if (!isMaximized) {
            Scene scene = main_pane.getScene();
            double initWidth = scene.getWidth(),
                    initHeight = scene.getHeight();
            
            defaultWidth = (defaultWidth == 0) ? scene.getWidth() : defaultWidth;
            defaultHeight = (defaultHeight == 0) ? scene.getHeight() : defaultHeight;
            
            maxHelper(initWidth, initHeight, false);
        } else
            maxHelper(defaultWidth, defaultHeight, true);
        
        isMaximized = !isMaximized;
    }
    
    protected void maxHelper(double width, double height,
                           boolean alreadyMaximized) {
        Stage stage = (Stage) main_pane.getScene().getWindow();
        Scene scene = stage.getScene();
        
        stage.setMaximized(!alreadyMaximized);
        
        double ratio = width / height,
                newWidth = scene.getWidth(), newHeight = scene.getHeight(),
                scaleFactor = (newWidth / newHeight > ratio) ? newHeight / height : newWidth / width;
        boolean condition = scaleFactor >= 1;
        
        if (condition) {
            Scale scale = new Scale(scaleFactor, scaleFactor);
            scale.setPivotX(0);
            scale.setPivotY(0);
            scene.getRoot().getTransforms().setAll(scale);
        }
        
        main_pane.setPrefWidth(condition ? newWidth / scaleFactor : Math.max(width, newWidth));
        main_pane.setPrefHeight(condition ? newHeight / scaleFactor : Math.max(height, newHeight));
    }
    // endregion
}
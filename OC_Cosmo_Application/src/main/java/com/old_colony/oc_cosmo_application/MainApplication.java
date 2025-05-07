package com.old_colony.oc_cosmo_application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;

import java.io.IOException;

/**
 * <h2>Old Colony Cosmetology Application Project</h2>
 * <p>This application was developed to help the cosmetology shop with scheduling,
 * service tracking, and user management.</p>
 * <p><a href="https://github.com/CalciumKing/OC_Cosmo_Application">GitHub Repository</a></p>
 * <p>License<br>
 * MIT - Copyright (c) 2025 Old Colony Regional Vocational Technical High School</p>
 * <p>Technologies Used:</p>
 * <ul>
 *     <li>Java</li>
 *     <li>JavaFX</li>
 *     <li>MySQL</li>
 *     <li>FXML</li>
 *     <li>CSS</li>
 *     <li>Font-Awesome</li>
 * </ul>
 *
 * @author Landen Ingerslev (CalciumKing)
 * @author Mason Peets
 * @version 1.0.0
 * @since 5/30/2025
 */

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // if mr SeanRiley.jpg is not found in images folder, do not start program
        File file = new File("src/main/resources/images/SeanRiley.jpg");
        if(!file.exists())
            Platform.exit();
        
        Scene scene = new Scene(fxmlLoader.load());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Cosmetology Application | Login");
        URL url = getClass().getResource("/images/AppIcon.png");
        if(url != null)
            stage.getIcons().add(new Image(url.toExternalForm()));
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
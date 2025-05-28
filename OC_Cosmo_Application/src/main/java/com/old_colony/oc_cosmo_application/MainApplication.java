package com.old_colony.oc_cosmo_application;

import com.old_colony.oc_cosmo_application.Controllers.StartController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * <h1>Old Colony Cosmetology Application Project</h1>
 *
 * <p>This application was developed to help our school's cosmetology shop with scheduling and service tracking.</p>
 *
 * <p>License:
 * <br>
 * MIT - Copyright (c) 2025 Old Colony Regional Vocational Technical High School</p>
 *
 * <p>Technologies Used:</p>
 * <ul>
 *     <li>Java</li>
 *     <li>JavaFX</li>
 *     <li>MySQL</li>
 *     <li>FXML</li>
 *     <li>CSS</li>
 *     <li>Font-Awesome</li>
 *     <li>Markdown</li>
 *     <p>PDF</p>
 * </ul>
 *
 * @author Landen Ingerslev (CalciumKing)
 * @author Mason Peets
 * @version 1.0.0
 * @see <a href="https://github.com/CalciumKing/OC_Cosmo_Application">GitHub Repository</a>
 * @see com.old_colony.oc_cosmo_application.Controllers Controllers Folder
 * @see com.old_colony.oc_cosmo_application.Data Data Folder
 * @see com.old_colony.oc_cosmo_application.Misc Misc Utils Folder
 * @since 5/30/2025
 */

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // if mr SeanRiley.jpg is not found in images folder, do not start program
        // DO NOT DELETE EVER!!!
        if (!new File("src/main/resources/images/SeanRiley.jpg").exists())
            Platform.exit();

        URL url = getClass().getResource("/images/AppIcon.png");
        if (url != null)
            stage.getIcons().add(new Image(url.toExternalForm()));

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("start.fxml"));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Cosmetology Application | Login");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setAlwaysOnTop(true);
        stage.show();

        ((StartController) fxmlLoader.getController()).handleShortcuts();
    }

    public static void main(String[] args) {
        launch();
    }
}
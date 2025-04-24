package com.old_colony.oc_cosmo_application.Controllers;

import com.old_colony.oc_cosmo_application.DataClasses.User;
import com.old_colony.oc_cosmo_application.MainApplication;
import com.old_colony.oc_cosmo_application.Utils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
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

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * This controller should only ever be the parent of fxml controllers.
 * <p>Methods include: window settings, dark mode, scene changing, and init.</p>
 * <p>init() should be run in change scene to transfer information like user data or dark mode and maximized.</p>
 * <p>After some simple fxids are assigned and onActions are implemented, the heavy duty work is finished.</p>
 * */

@SuppressWarnings({"CallToPrintStackTrace", "unused"})
abstract class AbstractController {
    @FXML
    protected AnchorPane main_pane;
    @FXML
    protected FontAwesomeIcon maximizeIcon, darkModeIcon;
    protected boolean isMaximized, isDarkMode;
    private double xOffset, yOffset, defaultWidth, defaultHeight;


    /**
     * @param user pass in the logged-in user, null if exiting
     * @param isDarkMode if the user was in dark mode before logging in
     * @param isMaximized if the user was maximized before logging in
     */
    protected abstract void init(User user, boolean isDarkMode, boolean isMaximized);

    /**
     * @param sceneName .fxml file to change to, only enter file name without .fxml
     * @param user logged-in user as user object, null if exiting
     */
    protected final void changeScene(String sceneName, User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(sceneName + ".fxml"));
            Parent root = fxmlLoader.load();
            Stage oldStage = (Stage) main_pane.getScene().getWindow(), // getting main_pane from other controller
                    stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(new Scene(root));

            stage.setX(oldStage.getX());
            stage.setY(oldStage.getY());

            URL url = getClass().getResource("/images/AppIcon.png");
            if (url == null) throw new FileNotFoundException();
            stage.getIcons().add(new Image(url.toExternalForm()));

            stage.show();

            if (user != null) { // entering application
                stage.setTitle("Cosmetology Application | " + user.getUsername());
                DashboardController dashboardController = fxmlLoader.getController();
                dashboardController.init(user, isDarkMode, isMaximized);
            } else { // exiting application
                stage.setTitle("Cosmetology Application | Login");
                StartController startController = fxmlLoader.getController();
                startController.init(null, isDarkMode, isMaximized);
            }
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

    @FXML
    protected final void toggleDarkMode() {
        main_pane.getStylesheets().removeLast();
        String theme = (isDarkMode ? "lightMode" : "darkMode") + ".css",
                icon = isDarkMode ? "MOON_ALT" : "SUN_ALT";

        URL url = getClass().getResource("/com/old_colony/oc_cosmo_application/CSSFiles/" + theme);
        if (url == null) return;

        main_pane.getStylesheets().addLast(url.toExternalForm());
        darkModeIcon.setGlyphName(icon);

        isDarkMode = !isDarkMode;
    }

    // region Window Methods
    @FXML
    protected final void windowMinimize(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    protected final void windowClose() {
        Platform.exit();
    }

    @FXML
    protected final void windowClick(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    protected final void windowDrag(MouseEvent event) {
        if (isMaximized)
            toggleMaximize(); // undoing maximization

        Stage stage = (Stage) main_pane.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    protected final void toggleMaximize() {
        if (!isMaximized) { // maximizing
            Scene scene = main_pane.getScene();
            double initWidth = scene.getWidth(),
                    initHeight = scene.getHeight();

            defaultWidth = (defaultWidth == 0) ? initWidth : defaultWidth;
            defaultHeight = (defaultHeight == 0) ? initHeight : defaultHeight;

            maxHelper(initWidth, initHeight);
        } else // un-maximizing
            maxHelper(defaultWidth, defaultHeight);

        isMaximized = !isMaximized;
    }

    private void maxHelper(double width, double height) {
        Stage stage = (Stage) main_pane.getScene().getWindow();
        Scene scene = stage.getScene();

        stage.setMaximized(!isMaximized);
        maximizeIcon.setGlyphName(isMaximized ? "SQUARE" : "MINUS_SQUARE");

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
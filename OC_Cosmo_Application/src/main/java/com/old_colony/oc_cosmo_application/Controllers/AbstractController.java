package com.old_colony.oc_cosmo_application.Controllers;

import com.old_colony.oc_cosmo_application.Data.User;
import com.old_colony.oc_cosmo_application.MainApplication;
import com.old_colony.oc_cosmo_application.Misc.Utils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * This controller should only ever be the parent of fxml controllers.
 * <p>Methods include: various window settings, dark mode, scene changing, and init.</p>
 * <p>{@link #init(User, boolean, boolean) init()} should be run in change scene to transfer information like user data or dark mode and maximized.</p>
 * <p>After some simple fx-ids are assigned and onActions are implemented, the heavy duty work is finished.</p>
 *
 * @see #init(User, boolean, boolean) init()
 */

abstract class AbstractController {
    @FXML
    protected AnchorPane main_pane;
    @FXML
    protected FontAwesomeIcon maximizeIcon, darkModeIcon;
    private boolean isMaximized, maximizedFromClick, preventDrag, isDarkMode;
    private double xOffset, yOffset, defaultWidth, defaultHeight;

    /**
     * Abstract method required for every controller class
     * <p>Manages what happens to user data parameters and can initialize anything that needs user data here</p>
     *
     * @param user        pass in the logged-in user, null if exiting
     * @param isDarkMode  if the user was in dark mode before logging in
     * @param isMaximized if the user was maximized before logging in
     * @see User
     */
    @SuppressWarnings("unused")
    protected abstract void init(User user, boolean isDarkMode, boolean isMaximized);

    /**
     * Changes to desired scene based on file name, user parameter can be used to transfer user data excluding dark mode and maximized
     *
     * @param sceneName .fxml file to change to, only enter file name without .fxml
     * @param user      logged-in user as user object, null if exiting
     * @see User
     */
    @SuppressWarnings({"CallToPrintStackTrace"})
    protected final void changeScene(String sceneName, User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(sceneName + ".fxml"));
            Parent root = fxmlLoader.load();
            Stage oldStage = (Stage) main_pane.getScene().getWindow(), // getting main_pane from other controller
                    stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(new Scene(root));
            stage.setAlwaysOnTop(true);

            // setting new stage in the same position as the previous one
            stage.setX(oldStage.getX());
            stage.setY(oldStage.getY());

            URL url = getClass().getResource("/images/AppIcon.png");
            if (url == null) throw new FileNotFoundException();
            stage.getIcons().add(new Image(url.toExternalForm()));

            stage.show();

            switch (sceneName) {
                case "dashboard":
                    stage.setTitle("Cosmetology Application | " + user.username());
                    DashboardController dashboardController = fxmlLoader.getController();
                    dashboardController.init(user, isDarkMode, isMaximized);
                    break;
                case "start":
                    stage.setTitle("Cosmetology Application | Login");
                    StartController startController = fxmlLoader.getController();
                    startController.init(null, isDarkMode, isMaximized);
                    break;
                case "analytics":
                    stage.setTitle("Cosmetology Analytics Application | " + user.username());
                    AnalyticsController analyticsController = fxmlLoader.getController();
                    analyticsController.init(user, isDarkMode, isMaximized);
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + sceneName);
            }

            main_pane.getScene().getWindow().hide();
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

    /**
     * Toggles dark mode based on the isDarkMode variable
     * <p>Changes the dark mode icon and alters current scene's css file with lightMode.css and darkMode.css</p>
     */
    @FXML
    protected final void toggleDarkMode() {
        main_pane.getStylesheets().removeLast();
        String theme = (isDarkMode ? "lightMode" : "darkMode") + ".css",
                icon = isDarkMode ? "MOON_ALT" : "SUN_ALT";

        URL url = getClass().getResource("/com/old_colony/oc_cosmo_application/CSS/" + theme);
        if (url != null)
            main_pane.getStylesheets().addLast(url.toExternalForm());
        darkModeIcon.setGlyphName(icon);

        isDarkMode = !isDarkMode;
    }

    // region Window Methods
    @FXML
    protected final void windowMinimize(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    /**
     * Safely exits the application.
     * <p>Program ends here.</p>
     */
    @FXML
    protected final void windowClose() {
        Platform.exit();
    }

    /**
     * This method is needed to set the original x and y position to be used in {@code windowDrag()} and {@code windowRelease()}
     * <p>Maximizes the window if clicked twice quickly.</p>
     *
     * @param event mouse clicking event
     */
    @FXML
    protected final void windowClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            toggleMaximize();
            maximizedFromClick = true;
            preventDrag = true;
            return;
        }

        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Moves the window and sets opacity to 75% upon mouse drag
     * <p>Base cases prevent window toggling maximization twice, and double clicking maximization glitching, then exits function.</p>
     *
     * @param event mouse dragging event
     */
    @FXML
    protected final void windowDrag(MouseEvent event) {
        if (preventDrag) {
            preventDrag = false;
            return;
        }

        if (maximizedFromClick) {
            maximizedFromClick = false;
            return;
        }

        if (isMaximized)
            toggleMaximize(); // undoing maximization

        Stage stage = (Stage) main_pane.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);

        stage.setOpacity(.75);
    }

    /**
     * Maximizes the window if the distance between the window and the top of the screen is less than 25 pixels.
     * <p>Screen bound precautions preventing window from getting stuck anywhere outside screen bounds</p>
     * <p>Sets window opacity to 100% upon dragging stopped</p>
     */
    @FXML
    protected final void windowRelease() {
        if (maximizedFromClick) {
            maximizedFromClick = false;
            preventDrag = false;
            return;
        }

        Scene scene = main_pane.getScene();
        Window window = scene.getWindow();

        ObservableList<Screen> allScreens = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight());

        Screen screen;
        Stage stage = (Stage) window;

        if (allScreens.isEmpty())
            screen = Screen.getPrimary();
        else
            screen = allScreens.get(0);

        Rectangle2D bounds = screen.getVisualBounds();

        if (Math.abs(window.getY() - bounds.getMinY()) <= 25) // maximize window when placed at top of screen
            toggleMaximize();
        else if (window.getY() <= bounds.getMinY()) // prevents window getting stuck above screen
            stage.setY(bounds.getMinY());
        else if (window.getY() + window.getHeight() >= bounds.getMaxY()) // prevents window getting stuck below screen
            stage.setY(bounds.getMaxY() - stage.getHeight());

        if (window.getX() <= bounds.getMinX()) // prevents window getting stuck to left of screen
            stage.setX(bounds.getMinX());
        else if (window.getX() + window.getWidth() >= bounds.getMaxX()) // prevents window getting stuck to right of screen
            stage.setX(bounds.getMaxX() - stage.getWidth());

        window.setOpacity(1);

        maximizedFromClick = false;
    }

    /**
     * Toggles window maximization based on isMaximized variable
     */
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

    /**
     * Does the actual window and icon changing that {@code toggleMaximize()} sets up for it
     *
     * @param width  desired width of the screen
     * @param height desired height of the screen
     */
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
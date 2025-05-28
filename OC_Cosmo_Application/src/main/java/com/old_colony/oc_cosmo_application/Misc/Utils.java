package com.old_colony.oc_cosmo_application.Misc;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;

/**
 * This file is for any miscellaneous utility methods that don't use SQL and aren't local SQL helper methods
 */
public class Utils {
    // region Alert Methods
    /**
     * Creates a regular alert with the contents of the parameters
     *
     * @param type        alert type of type {@code Alert.AlertType} (Ex: information, confirmation, error, none, warning, etc.)
     * @param title       window title text
     * @param headerText  main error header text
     * @param contentText error description or future instructions
     */
    public static void normalAlert(Alert.AlertType type, String title,
                                   String headerText, String contentText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        alert.showAndWait();
    }

    /**
     * Creates a confirmation alert with the contents of the parameters
     *
     * @param type        alert type of type {@code Alert.AlertType} (Ex: information, confirmation, error, none, warning, etc.)
     * @param title       window title text
     * @param headerText  main error header text
     * @param contentText error description or future instructions
     * @param aText       the confirm button text, can be used to confirm the user's choice of the alert
     * @param bText       the cancel button text, can be used to confirm the users choice of the alert
     * @return returns {@code Optional<ButtonType>} to be used with {@code isPresent()} and {@code getText()} to confirm the user's choice of the alert
     */
    public static Optional<ButtonType> confirmAlert(Alert.AlertType type, String title,
                                                    String headerText, String contentText,
                                                    String aText, String bText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        ButtonType yes = new ButtonType(aText, ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType(bText, ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no);
        return alert.showAndWait();
    }

    /**
     * Does the actual work of creating a regular alert.
     * <p>Used by {@code normalAlert()} and {@code confirmAlert()}</p>
     *
     * @param type        alert type of type {@code Alert.AlertType} (Ex: information, confirmation, error, none, warning, etc.)
     * @param title       window title text
     * @param headerText  main error header text
     * @param contentText error description or future instructions
     * @return returns {@code Alert} to be used elsewhere
     */
    private static Alert createAlert(Alert.AlertType type, String title,
                                     String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.initOwner(getCurrentStage());
        return alert;
    }
    // endregion Alert Methods

    // region Misc
    /**
     * Gets the current stage based on the available windows.
     * <p>Used only in {@link #createAlert(Alert.AlertType, String, String, String) createAlert()} if no stage data is given and the stage is needed.</p>
     *
     * @return the currently open application's stage
     * @see #createAlert(Alert.AlertType, String, String, String) createAlert()
     */
    private static Stage getCurrentStage() {
        for (Window window : Window.getWindows())
            if (window instanceof Stage)
                return (Stage) window;
        return null;
    }
    // endregion
}
package com.old_colony.oc_cosmo_application;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;

/**
 * This file is for any utility methods that don't use SQL and aren't local SQL helper methods
 */
public class Utils {
    // region Alert Methods
    public static void normalAlert(Alert.AlertType type, String title,
                                   String headerText, String contentText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        alert.showAndWait();
    }

    public static Optional<ButtonType> confirmAlert(Alert.AlertType type, String title,
                                                    String headerText, String contentText,
                                                    String aText, String bText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        ButtonType yes = new ButtonType(aText, ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType(bText, ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no);
        return alert.showAndWait();
    }

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

    private static Stage getCurrentStage() {
        for (Window window : Window.getWindows())
            if (window instanceof Stage)
                return (Stage) window;
        return null;
    }
}
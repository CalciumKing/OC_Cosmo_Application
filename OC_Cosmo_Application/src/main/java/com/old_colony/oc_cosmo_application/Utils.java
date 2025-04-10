package com.old_colony.oc_cosmo_application;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

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
        return alert;
    }
    // endregion Alert Methods
}
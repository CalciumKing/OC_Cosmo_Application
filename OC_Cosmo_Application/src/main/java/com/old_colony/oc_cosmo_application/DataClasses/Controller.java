package com.old_colony.oc_cosmo_application.DataClasses;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public interface Controller {
    // region Window Settings
    void windowMinimize(ActionEvent event);
    void windowClose();
    void windowClick(MouseEvent event);

//    trying to get these working:
//
//    default void windowMinimize(ActionEvent event) {
//        ((Stage) ((Button) event.getSource()).getScene().getWindow()).setIconified(true);
//    }
//
//    default void windowClose() {
//        Platform.exit();
//    }
//
//    default void windowClick(MouseEvent event) {
//        Utils.windowClick(event);
//    }

    void windowDrag(MouseEvent event);

    void windowMaximize();
    // endregion
}
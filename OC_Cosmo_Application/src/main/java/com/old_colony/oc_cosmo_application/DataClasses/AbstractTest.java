package com.old_colony.oc_cosmo_application.DataClasses;

import com.old_colony.oc_cosmo_application.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public abstract class AbstractTest {
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
        Utils.windowClick(event);
    }

    protected abstract void windowDrag(MouseEvent event);
    protected abstract void windowMaximize();
}
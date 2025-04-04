package com.old_colony.oc_cosmo_application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class StartController {
    @FXML
    private TextField forgotAnswer_txt, forgotPassword_txt, forgotUsername_txt, securityAnswer_txt, username_txt;

    @FXML
    private Hyperlink forgotPassword_link, login_link;

    @FXML
    private AnchorPane forgotPassword_pane, login_pane;

    @FXML
    private ComboBox<String> forgotQuestion_box;

    @FXML
    private PasswordField password_txt;

    @FXML
    private Label securityQuestion_lbl;



    @FXML
    private void swapPane() {
        if (login_pane.isVisible()) {
            login_pane.setVisible(false);
            forgotPassword_pane.setVisible(true);
        }else if(forgotPassword_pane.isVisible()) {
            forgotPassword_pane.setVisible(false);
            login_pane.setVisible(true);
        }
    }
}
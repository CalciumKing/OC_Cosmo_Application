package com.old_colony.oc_cosmo_application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartController{
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
    private Label securityQuestion_lbl, forgotQuestion_lbl;

    @FXML
    private void changeSecurityLbl() {
        fillQuestionLbl(securityQuestion_lbl, username_txt);
    }
    @FXML
    private void changeForgotSecurityLbl(){
        fillQuestionLbl(forgotQuestion_lbl, forgotUsername_txt);
    }

    private void fillQuestionLbl(Label question_lbl, TextField username_txt) {
        question_lbl.setText(SQLUtils.getSecurityQuestion(username_txt.getText()));
    }

    private boolean checkInformation(){
        return SQLUtils.logInCheck(username_txt.getText(), password_txt.getText(), securityAnswer_txt.getText());
    }

    @FXML
    private void enableForgotPassword() {
        if(!forgotAnswer_txt.getText().isEmpty() || !forgotUsername_txt.getText().isEmpty()){
            forgotPassword_txt.setDisable(false);
        }
    }

    private void clearForgotPassword(){
        forgotPassword_txt.clear();
        forgotUsername_txt.clear();
        forgotAnswer_txt.clear();
        forgotQuestion_lbl.setText("");
    }

    @FXML
    private void setNewPassword(){
        if(forgotPassword_txt.getText().isEmpty() || forgotUsername_txt.getText().isEmpty() || forgotAnswer_txt.getText().isEmpty()){
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Not all boxes filled out",
                    "Please Fill Out all the boxes"
            );
            return;
        }
        SQLUtils.changePassword(forgotUsername_txt.getText(), forgotPassword_txt.getText());
        clearForgotPassword();
    }

    private void setInfoOnSwap(){
        forgotUsername_txt.setText(username_txt.getText());
        forgotQuestion_lbl.setText(securityQuestion_lbl.getText());
        forgotAnswer_txt.setText(securityAnswer_txt.getText());
    }

    @FXML
    private void logIn(){
        if(username_txt.getText().isEmpty() || password_txt.getText().isEmpty() || securityAnswer_txt.getText().isEmpty())
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Not all boxes filled out",
                    "Please Fill Out all the boxes"
            );
        else{
            if(checkInformation())
                Utils.changeScene("dashboard.fxml", SQLUtils.getUser(username_txt.getText()));
            else
                Utils.normalAlert(
                        Alert.AlertType.ERROR,
                        "Failure",
                        "Your information is incorrect",
                        "Please Try Again"
                );
        }
    }

    @FXML
    private void swapPane() {
        if (login_pane.isVisible()) {
            login_pane.setVisible(false);
            forgotPassword_pane.setVisible(true);
            setInfoOnSwap();
        }else if(forgotPassword_pane.isVisible()) {
            forgotPassword_pane.setVisible(false);
            login_pane.setVisible(true);
        }
    }
}
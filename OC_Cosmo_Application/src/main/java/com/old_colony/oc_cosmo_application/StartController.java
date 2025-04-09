package com.old_colony.oc_cosmo_application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
    private AnchorPane forgotPassword_pane, login_pane, start;

    @FXML
    private ComboBox<String> forgotQuestion_box;

    @FXML
    private PasswordField password_txt;

    @FXML
    private Label securityQuestion_lbl, forgotQuestion_lbl;
    
    private double defaultWidth, defaultHeight;
    private boolean isMaximized;

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
                System.out.println();
//                Utils.changeScene("dashboard.fxml", SQLUtils.getUser(username_txt.getText()));
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
    
    // region Window Settings
    @FXML
    private void windowMinimize(ActionEvent event) {
        Utils.windowMinimize(event);
    }
    
    @FXML
    private void windowClose() {
        Utils.windowClose();
    }
    
    @FXML
    private void windowClick(MouseEvent event) {
        Utils.windowClick(event);
    }
    
    @FXML
    private void windowDrag(MouseEvent event) {
        if (isMaximized)
            windowMaximize(); // undoing maximization
        Utils.windowDrag(event, start);
    }
    
    @FXML
    private void windowMaximize() {
        if (!isMaximized) {
            Scene scene = start.getScene();
            double initWidth = scene.getWidth(),
                    initHeight = scene.getHeight();
            
            defaultWidth = (defaultWidth == 0) ? scene.getWidth() : defaultWidth;
            defaultHeight = (defaultHeight == 0) ? scene.getHeight() : defaultHeight;
            
            Utils.windowMaximize(start, initWidth, initHeight, false);
        } else
            Utils.windowMaximize(start, defaultWidth, defaultHeight, true);
        
        isMaximized = !isMaximized;
    }
    // endregion
}
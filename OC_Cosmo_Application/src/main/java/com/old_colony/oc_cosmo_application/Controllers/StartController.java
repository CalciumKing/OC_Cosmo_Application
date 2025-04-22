package com.old_colony.oc_cosmo_application.Controllers;

import com.old_colony.oc_cosmo_application.DataClasses.User;
import com.old_colony.oc_cosmo_application.SQLUtils;
import com.old_colony.oc_cosmo_application.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public final class StartController extends AbstractController {
    // region FXML Variables
    @FXML
    private TextField forgotAnswer_txt, forgotUsername_txt,
            securityAnswer_txt, username_txt;
    
    @FXML
    private AnchorPane forgotPassword_pane, login_pane;
    
    @FXML
    private PasswordField password_txt, forgotPassword_txt;
    
    @FXML
    private Label securityQuestion_lbl, forgotQuestion_lbl;
    // endregion
    
    @Override
    protected void init(User user, boolean darkMode) {
        isDarkMode = darkMode;
        if(darkMode) toggleDarkMode();
    }
    
    // region FXML Methods
    @FXML
    private void changeSecurityLbl() {
        fillQuestionLbl(securityQuestion_lbl, username_txt);
    }
    
    @FXML
    private void changeForgotSecurityLbl() {
        fillQuestionLbl(forgotQuestion_lbl, forgotUsername_txt);
    }
    
    @FXML
    private void enableForgotPassword() {
        if (!forgotAnswer_txt.getText().isEmpty() || !forgotUsername_txt.getText().isEmpty())
            forgotPassword_txt.setDisable(false);
    }
    
    @FXML
    private void setNewPassword() {
        if (forgotPassword_txt.getText().isEmpty() ||
            forgotUsername_txt.getText().isEmpty() ||
            forgotAnswer_txt.getText().isEmpty()) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Not all boxes filled out",
                    "Please Fill Out all the boxes"
            );
            return;
        }
        
        SQLUtils.changePassword(forgotUsername_txt.getText(), forgotPassword_txt.getText());
        logIn();
        clearForgotPassword();
    }
    
    @FXML
    private void logIn() {
        if(forgotPassword_pane.isVisible()) {
            changeScene("dashboard.fxml", SQLUtils.getUser(forgotUsername_txt.getText()));
            main_pane.getScene().getWindow().hide();
            return;
        }

        if (username_txt.getText().isEmpty() ||
            password_txt.getText().isEmpty() ||
            securityAnswer_txt.getText().isEmpty()) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Not all boxes filled out",
                    "Please Fill Out all the boxes"
            );
            return;
        }

        if (checkLoginInfo()) {
            changeScene("dashboard.fxml", SQLUtils.getUser(username_txt.getText()));
            main_pane.getScene().getWindow().hide();
        } else
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Your information is incorrect",
                    "Please Try Again"
            );
    }
    
    @FXML
    private void swapPane() {
        if (login_pane.isVisible()) {
            login_pane.setVisible(false);
            forgotPassword_pane.setVisible(true);
            setInfoOnSwap();
        } else if (forgotPassword_pane.isVisible()) {
            forgotPassword_pane.setVisible(false);
            login_pane.setVisible(true);
        }
    }
    // endregion
    
    // region Private Helper Methods
    private void fillQuestionLbl(Label question_lbl, TextField username_txt) {
        question_lbl.setText(SQLUtils.getSecurityQuestion(username_txt.getText()));
    }
    
    private boolean checkLoginInfo() {
        return SQLUtils.logInCheck(username_txt.getText(), password_txt.getText(), securityAnswer_txt.getText());
    }
    
    private void clearForgotPassword() {
        forgotPassword_txt.clear();
        forgotUsername_txt.clear();
        forgotAnswer_txt.clear();
        forgotQuestion_lbl.setText("");
    }
    
    private void setInfoOnSwap() {
        forgotUsername_txt.setText(username_txt.getText());
        forgotQuestion_lbl.setText(securityQuestion_lbl.getText());
        forgotAnswer_txt.setText(securityAnswer_txt.getText());
    }
    // endregion
}
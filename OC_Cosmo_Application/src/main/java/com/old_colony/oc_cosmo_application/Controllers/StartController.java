package com.old_colony.oc_cosmo_application.Controllers;

import com.old_colony.oc_cosmo_application.DataClasses.User;
import com.old_colony.oc_cosmo_application.SQLUtils;
import com.old_colony.oc_cosmo_application.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class StartController extends AbstractController {
    // region FXML Variables
    @FXML
    private TextField forgotAnswer_field, forgotUsername_field,
            securityAnswer_field, username_field;

    @FXML
    private AnchorPane forgotPassword_pane, login_pane;

    @FXML
    private PasswordField password_field, forgotPassword_field;

    @FXML
    private Label securityQuestion_lbl, forgotQuestion_lbl;
    // endregion

    @Override
    protected void init(User user, boolean isDarkMode, boolean isMaximized) {
        // default is light mode and not maximized
        // toggles to the previous applications settings if needed
        if (isDarkMode) toggleDarkMode();
        if (isMaximized) toggleMaximize();
    }

    // region FXML Methods
    @FXML
    private void updateSecQuestionLbl(KeyEvent event) {
        TextField source = (TextField) event.getSource();

        if(source.equals(username_field))
            securityQuestion_lbl.setText(SQLUtils.getSecurityQuestion(username_field.getText()));
        else if(source.equals(forgotUsername_field))
            forgotQuestion_lbl.setText(SQLUtils.getSecurityQuestion(forgotUsername_field.getText()));
        else
            System.out.println("not working");
    }

    @FXML
    private void enableForgotPassword() { // for what purpose???
        if (!forgotAnswer_field.getText().isEmpty() || !forgotUsername_field.getText().isEmpty())
            forgotPassword_field.setDisable(false);
    }

    @FXML
    private void setNewPassword() {
        if (forgotPassword_field.getText().isEmpty() ||
                forgotUsername_field.getText().isEmpty() ||
                forgotAnswer_field.getText().isEmpty()) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Not all boxes filled out",
                    "Please Fill Out all the boxes"
            );
            return;
        }

        SQLUtils.changePassword(forgotUsername_field.getText(), forgotPassword_field.getText());
        logIn();
    }

    @FXML
    private void logIn() {
        // skip checks because its already been verified in setNewPassword()
        if (forgotPassword_pane.isVisible()) {
            changeScene("dashboard", SQLUtils.getUser(forgotUsername_field.getText()));
            main_pane.getScene().getWindow().hide();
            return;
        }

        String username = username_field.getText(),
                password = password_field.getText(),
                secAnswer = securityAnswer_field.getText();

        if (username.isEmpty() || password.isEmpty() || secAnswer.isEmpty()) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Not all boxes filled out",
                    "Please Fill Out all the boxes"
            );
            return;
        }

        if (SQLUtils.logInCheck(username, password, secAnswer)) {
            changeScene("dashboard", SQLUtils.getUser(username));
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
        boolean toForgot = login_pane.isVisible();

        login_pane.setVisible(!toForgot);
        forgotPassword_pane.setVisible(toForgot);

        TextField fromUsername = toForgot ? username_field : forgotUsername_field,
                fromAnswer = toForgot ? securityAnswer_field : forgotAnswer_field,
                toUsername = toForgot ? forgotUsername_field : username_field,
                toAnswer = toForgot ? forgotAnswer_field : securityAnswer_field;

        Label fromQuestion = toForgot ? securityQuestion_lbl : forgotQuestion_lbl,
                toQuestion = toForgot ? forgotQuestion_lbl : securityQuestion_lbl;

        if (toUsername.getText().isEmpty())
            toUsername.setText(fromUsername.getText());
        if (toQuestion.getText().isEmpty())
            toQuestion.setText(fromQuestion.getText());
        if (toAnswer.getText().isEmpty())
            toAnswer.setText(fromAnswer.getText());
    }
    // endregion
}
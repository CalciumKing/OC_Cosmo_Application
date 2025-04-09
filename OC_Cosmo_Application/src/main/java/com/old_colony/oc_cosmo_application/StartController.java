package com.old_colony.oc_cosmo_application;

import com.old_colony.oc_cosmo_application.DataClasses.Controller;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class StartController implements Controller {
    // region Variables
    // region FXML Variables
    @FXML
    private TextField forgotAnswer_txt, forgotPassword_txt,
            forgotUsername_txt, securityAnswer_txt, username_txt;

    @FXML
    private FontAwesomeIcon usernameIcon, passwordIcon,
            answerIcon, forgotAnswerIcon, forgotUsernameIcon;

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
    // endregion

    // region Private Variables
    private double defaultWidth, defaultHeight;
    private boolean isMaximized;
    // endregion
    // endregion

    // region FXML Methods
    @FXML
    private void changeSecurityLbl(KeyEvent event) {
        fillQuestionLbl(securityQuestion_lbl, username_txt);
        inputValidation(event);
    }

    @FXML
    private void changeForgotSecurityLbl(KeyEvent event) {
        fillQuestionLbl(forgotQuestion_lbl, forgotUsername_txt);
        inputValidation(event);
    }

    private void fillQuestionLbl(Label question_lbl, TextField username_txt) {
        question_lbl.setText(SQLUtils.getSecurityQuestion(username_txt.getText()));
    }

    private boolean checkInformation() {
        return SQLUtils.logInCheck(username_txt.getText(), password_txt.getText(), securityAnswer_txt.getText());
    }

    @FXML
    private void inputValidation(KeyEvent event) { // try to find a way to clean this up, this hurts to look at
        Object source = event.getSource();
        if (source.equals(username_txt)) {
            if (SQLUtils.checkTextBoxes(username_txt.getText(), "username")) {
                usernameIcon.setGlyphName("CHECK");
            } else {
                usernameIcon.setGlyphName("CLOSE");
            }
        } else if (source.equals(password_txt)) {
            if (SQLUtils.checkTextBoxes(password_txt.getText(), "password")) {
                passwordIcon.setGlyphName("CHECK");
            } else {
                passwordIcon.setGlyphName("CLOSE");
            }
        } else if (source.equals(securityAnswer_txt)) {
            if (SQLUtils.checkTextBoxes(securityAnswer_txt.getText(), "securityAnswer")) {
                answerIcon.setGlyphName("CHECK");
            } else {
                answerIcon.setGlyphName("CLOSE");
            }
        } else if (source.equals(forgotUsername_txt)) {
            if (SQLUtils.checkTextBoxes(username_txt.getText(), "username")) {
                forgotUsernameIcon.setGlyphName("CHECK");
            } else {
                forgotUsernameIcon.setGlyphName("CLOSE");
            }
        } else if (source.equals(forgotAnswer_txt)) {
            if (SQLUtils.checkTextBoxes(securityAnswer_txt.getText(), "securityAnswer")) {
                forgotAnswerIcon.setGlyphName("CHECK");
            } else {
                forgotAnswerIcon.setGlyphName("CLOSE");
            }
        }
    }

    @FXML
    private void enableForgotPassword() {
        if (!forgotAnswer_txt.getText().isEmpty() || !forgotUsername_txt.getText().isEmpty())
            forgotPassword_txt.setDisable(false);
    }

    private void clearForgotPassword() {
        forgotPassword_txt.clear();
        forgotUsername_txt.clear();
        forgotAnswer_txt.clear();
        forgotQuestion_lbl.setText("");
    }

    @FXML
    private void setNewPassword() {
        if (forgotPassword_txt.getText().isEmpty() || forgotUsername_txt.getText().isEmpty() || forgotAnswer_txt.getText().isEmpty()) {
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

    private void setInfoOnSwap() {
        forgotUsername_txt.setText(username_txt.getText());
        forgotQuestion_lbl.setText(securityQuestion_lbl.getText());
        forgotAnswer_txt.setText(securityAnswer_txt.getText());
    }

    @FXML
    private void logIn() {
        if (username_txt.getText().isEmpty() || password_txt.getText().isEmpty() || securityAnswer_txt.getText().isEmpty()) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Not all boxes filled out",
                    "Please Fill Out all the boxes"
            );
            return;
        }

        if (checkInformation()) {
            Utils.changeScene("dashboard.fxml", SQLUtils.getUser(username_txt.getText()), isMaximized);
            start.getScene().getWindow().hide();
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

    // region Window Methods
    @FXML
    @Override
    public void windowMinimize(ActionEvent event) {
        Utils.windowMinimize(event);
    }

    @FXML
    @Override
    public void windowClose() {
        Utils.windowClose();
    }

    @FXML
    @Override
    public void windowClick(MouseEvent event) {
        Utils.windowClick(event);
    }

    @FXML
    @Override
    public void windowDrag(MouseEvent event) {
        if (isMaximized)
            windowMaximize(); // undoing maximization
        Utils.windowDrag(event, start);
    }

    @FXML
    @Override
    public void windowMaximize() {
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
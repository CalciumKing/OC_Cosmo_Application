package com.old_colony.oc_cosmo_application.Controllers;

import com.old_colony.oc_cosmo_application.Data.User;
import com.old_colony.oc_cosmo_application.Misc.SQLUtils;
import com.old_colony.oc_cosmo_application.Misc.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for start.fxml (login and sign up pages)
 */

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

    // region Override Methods
    /**
     * {@inheritDoc}
     * <br>
     * No user is ever passed in when exiting to start.fxml,
     * so we don't need to manage any user information or
     * initialize anything other than dark mode, maximized, and shortcuts
     */
    @Override
    public void init(User user, boolean isDarkMode, boolean isMaximized) {
        // default is light mode and not maximized
        // toggles to the previous applications settings if needed
        if (isDarkMode) toggleDarkMode();
        if (isMaximized) toggleMaximize();
        handleShortcuts();
    }

    /**
     * {@inheritDoc}
     * <br>
     * 1 and 2 swap between login and forgot password pages
     */
    @Override
    public void handleShortcuts() {
        main_pane.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DIGIT1, DIGIT2 -> swapPane();
                case F -> toggleMaximize();
                case D -> toggleDarkMode();
                case H -> toggleLegend();
                case M -> windowMinimize();
                case Q -> {
                    if (event.isControlDown())
                        windowClose();
                }
                case ESCAPE -> {
                    if (isMaximized)
                        toggleMaximize();
                }
            }
        });
    }
    // endregion

    // region FXML Methods
    /**
     * Updates security label based on corresponding page being typed on.
     * <p>If the normal login username field is being typed, fill in the security question on that page,
     * same with the username field on the forgot page.</p>
     *
     * @param event whichever text field the user is typing in
     */
    @FXML
    private void updateSecQuestionLbl(KeyEvent event) {
        TextField source = (TextField) event.getSource();

        if (source.equals(username_field))
            securityQuestion_lbl.setText(SQLUtils.getSecurityQuestion(username_field.getText()));
        else if (source.equals(forgotUsername_field))
            forgotQuestion_lbl.setText(SQLUtils.getSecurityQuestion(forgotUsername_field.getText()));
    }

    /**
     * Changes the user's password if the all information is filled in
     */
    @FXML
    private void setNewPassword() {
        if (forgotPassword_field.getText().trim().isEmpty() ||
                forgotUsername_field.getText().trim().isEmpty() ||
                forgotAnswer_field.getText().trim().isEmpty()) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Not all boxes filled out",
                    "Please Fill Out all the boxes"
            );
            return;
        }

        User user = SQLUtils.getUser(forgotUsername_field.getText());
        if (user == null) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "User Does Not Exist",
                    "Enter a username for a user that already exists."
            );
            return;
        } else if (!user.securityAnswer().equals(forgotAnswer_field.getText())) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Incorrect Security Answer",
                    "Enter the correct security answer for the entered user."
            );
            return;
        }

        SQLUtils.changePassword(forgotUsername_field.getText(), forgotPassword_field.getText());
        logIn();
    }

    /**
     * Used for both standard login and forgot password login.
     * <p>Forgot password information is already verified in {@code setNewPassword()},
     * base case handles that without checking other information.</p>
     */
    @FXML
    private void logIn() {
        // skip checks because its already been verified in setNewPassword()
        if (forgotPassword_pane.isVisible()) {
            changeScene("dashboard", SQLUtils.getUser(forgotUsername_field.getText()));
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
        } else
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Failure",
                    "Your information is incorrect",
                    "Please Try Again"
            );
    }

    /**
     * When swapping between login and forgot password panes,
     * this method fills in any already user filled out information
     * to avoid rewriting information after changing screens.
     */
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

        if (toUsername.getText().trim().isEmpty())
            toUsername.setText(fromUsername.getText().trim());

        if (toQuestion.getText() != null && toQuestion.getText().trim().isEmpty())
            toQuestion.setText(fromQuestion.getText().trim());

        if (toAnswer.getText().trim().isEmpty())
            toAnswer.setText(fromAnswer.getText().trim());
    }
    // endregion
}
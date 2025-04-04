package com.old_colony.oc_cosmo_application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

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

    private boolean checkInformation(){
        return SQLUtils.logInCheck(username_txt.getText(), password_txt.getText(), securityAnswer_txt.getText());
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
            if(checkInformation()){
                goToDashboard();
            }else
                Utils.normalAlert(
                        Alert.AlertType.ERROR,
                        "Failure",
                        "Your information is incorrect",
                        "Please Try Again"
                );
        }
    }

    private void goToDashboard(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            //call set user
            stage.setTitle("Cosmetology Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ((Stage) login_pane.getScene().getWindow()).close();

    }

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
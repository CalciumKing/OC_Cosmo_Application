package com.old_colony.oc_cosmo_application;

import com.old_colony.oc_cosmo_application.DataClasses.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

public class Utils {
    // region Alert Methods
    public static void normalAlert(Alert.AlertType type, String title,
                                   String headerText, String contentText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        alert.showAndWait();
    }
    
    public static Optional<ButtonType> confirmAlert(Alert.AlertType type, String title,
                                                    String headerText, String contentText,
                                                    String aText, String bText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        ButtonType yes = new ButtonType(aText, ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType(bText, ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no);
        return alert.showAndWait();
    }
    
    private static Alert createAlert(Alert.AlertType type, String title,
                                     String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }
    // endregion Alert Methods
    
    public static void changeScene(String sceneName, User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(sceneName));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(new Scene(root));
            
            if (user != null) { // entering application
                stage.setTitle("Cosmotology Application | " + user.getUsername());
                DashboardController dashboardController = fxmlLoader.getController();
                dashboardController.welcome(user);
            } else { // editing application
                stage.setTitle("Cosmetology Application | Login");
            }
            
            stage.show();
        } catch (Exception e) {
            normalAlert(
                    Alert.AlertType.ERROR,
                    "Scene Error",
                    "Error Changing Scene",
                    "There was an error changing scenes, please try again"
            );
            e.printStackTrace();
        }
    }
}
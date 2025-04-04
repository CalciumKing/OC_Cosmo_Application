package com.old_colony.oc_cosmo_application;

import com.old_colony.oc_cosmo_application.DataClasses.Appointment;
import com.old_colony.oc_cosmo_application.DataClasses.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class DashboardController {
    // region Variables
    @FXML
    private Button home_btn, schedule_btn, appointment_btn, account_btn, admin_btn, logOut_btn;
    
    @FXML
    private ColorPicker color_picker;
    
    @FXML
    private Label welcome_lbl, costDur_lbl, windowTitle_lbl;
    
    @FXML
    private TextArea note_area;
    
    @FXML
    private AnchorPane main_pane, home_pane, schedule_pane, create_pane, account_pane, admin_pane;
    
    @FXML
    private TextField customerName_field;
    
    @FXML
    private TableView<Appointment> dailySchedule_table, schedule_table;
    
    @FXML
    private DatePicker dateSelect_picker;
    
    @FXML
    private Spinner<Integer> hour_spinner, minute_spinner;
    
    @FXML
    private ComboBox<String> services_combobox;
    
    @FXML
    private ComboBox<User> student_combobox;
    
    
    private User currentUser;
    // endregion
    
    public void welcome(User user) {
        welcome_lbl.setText("Welcome, " + user.getUsername());
        windowTitle_lbl.setText("Cosmetology Application | " + user.getUsername());
        currentUser = user;
    }
    
    @FXML
    void resetForm(ActionEvent event) {
    
    }
    
    @FXML
    void scheduleTableSelected(MouseEvent event) {
    
    }
    
    @FXML
    void showPage(ActionEvent event) {
        AnchorPane[] panes = new AnchorPane[]{home_pane, schedule_pane, create_pane, account_pane, admin_pane};
        for(AnchorPane pane : panes)
            pane.setVisible(false);
        
        Button[] buttons = new Button[]{home_btn, schedule_btn, appointment_btn, account_btn, admin_btn, logOut_btn};
        Button button = (Button) event.getSource();
        AnchorPane pageToShow = null;
        
        for (int i = 0; i < buttons.length; i++) {
            if(button.equals(buttons[i])) {
                pageToShow = panes[i];
                break;
            }
        }
        
        if(pageToShow != null)
            pageToShow.setVisible(true);
    }
    
    @FXML
    void submitForm(ActionEvent event) {
    
    }
    
    @FXML
    void toggleMenu(ActionEvent event) {
    
    }
    
    @FXML
    void logOut(ActionEvent event) {
        Utils.changeScene("start.fxml", null);
        main_pane.getScene().getWindow().hide();
    }
    
    @FXML
    void windowClick(MouseEvent event) {
    
    }
    
    @FXML
    void windowClose(ActionEvent event) {
    
    }
    
    @FXML
    void windowDrag(MouseEvent event) {
    
    }
    
    @FXML
    void windowMaximize(ActionEvent event) {
    
    }
    
    @FXML
    void windowMinimize(ActionEvent event) {
    
    }
}
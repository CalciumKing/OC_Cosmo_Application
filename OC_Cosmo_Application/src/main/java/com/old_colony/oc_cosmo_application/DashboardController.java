package com.old_colony.oc_cosmo_application;

import com.old_colony.oc_cosmo_application.DataClasses.Appointment;
import com.old_colony.oc_cosmo_application.DataClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

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
    private GridPane monday_gridPane;
    
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
    
    private void init() {
        currentUser = null;
        initSchedules();
        initForm();
    }
    
    private void initForm() {
        hour_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1));
        minute_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 15));
        services_combobox.setItems(FXCollections.observableArrayList(
                "Haircut", "Extension", "Coloring",
                "Straighten/Perm", "Braiding/Styling",
                "Manicure", "Pedicure", "Other Nail Service",
                "Facial", "Waxing", "Consultation"
        ));
        student_combobox.setItems(SQLUtils.getAllUsers());
        costDur_lbl.setText("Cost: $0.00 Duration: 0.00");
    }
    
    private void initSchedules() {
        ObservableList<Appointment> appointments = SQLUtils.getAllAppointments();
        if (appointments == null) return;
        
        for (Appointment appointment : appointments) {
            Pane pane = new Pane();
            pane.setStyle("-fx-background-color: " + appointment.getColor() +";");
            GridPane.setRowIndex(pane, startRow);
            GridPane.setRowSpan(pane, appointment.getDuration() / 15);
            GridPane.setColumnIndex(pane, personColumn);
            monday_gridPane.getChildren().add(pane);
        }
    }
    
    public void welcome(User user) {
        init();
        welcome_lbl.setText("Welcome, " + user.getUsername());
        windowTitle_lbl.setText("Cosmetology Application | " + user.getUsername());
        currentUser = user;
    }
    
    @FXML
    void scheduleTableSelected(MouseEvent event) {
    
    }
    
    @FXML
    void showPage(ActionEvent event) {
        AnchorPane[] panes = new AnchorPane[]{home_pane, schedule_pane, create_pane, account_pane};
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
    void resetForm(ActionEvent event) {
        dateSelect_picker.setValue(null);
        hour_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1, 1));
        minute_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15));customerName_field.clear();
        services_combobox.setValue(null);
        student_combobox.setValue(null);
        color_picker.setValue(Color.RED);
        note_area.clear();
        costDur_lbl.setText("Cost: $0.00 Duration: 0.00");
    }
    
    @FXML
    void submitForm(ActionEvent event) {
    
    }
    
    @FXML
    void toggleMenu(ActionEvent event) {
    
    }
    
    @FXML
    void logOut() {
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
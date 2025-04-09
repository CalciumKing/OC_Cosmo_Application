package com.old_colony.oc_cosmo_application;

import com.old_colony.oc_cosmo_application.DataClasses.Appointment;
import com.old_colony.oc_cosmo_application.DataClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    // region Variables
    // region FXML Variables
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
    private ComboBox<String> services_combobox, student_combobox;
    
    @FXML
    private VBox sideMenu;
    // endregion
    
    // region Private Variables
    private User currentUser;
    private final GridPane[] weekPanes = new GridPane[] {monday_gridPane};
    private double defaultWidth, defaultHeight;
    private boolean isMaximized, isCollapsed;
    // endregion
    // endregion
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = null;
        initForm();
    }
    
    // region FXML Methods
    public void welcome(User user, boolean maximized) {
        welcome_lbl.setText("Welcome, " + user.getUsername());
        windowTitle_lbl.setText("Cosmetology Application | " + user.getUsername());
        currentUser = user;
        isMaximized = maximized;
        if (maximized) windowMaximize();
        initSchedules();
    }
    
    @FXML
    private void setAMPM(ActionEvent event) {
        hour_spinner.setValueFactory(
                ((RadioButton) event.getSource()).getText().equals("AM") ?
                new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 12, 1) :
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2, 1)
        );
    }
    
    @FXML
    private void scheduleTableSelected(MouseEvent event) {
    
    }
    
    @FXML
    private void showPage(ActionEvent event) {
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
    private void resetForm() {
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
    private void submitForm(ActionEvent event) {
    
    }
    
    @FXML
    private void toggleMenu() {
        sideMenu.setPrefWidth(isCollapsed ? 200 : 50);
        for(Node node : sideMenu.getChildren()) {
            if(node instanceof Button button) {
                if(isCollapsed)
                    button.setText((String) button.getUserData());
                else {
                    button.setUserData(button.getText());
                    button.setText("");
                }
            }
        }
        isCollapsed = !isCollapsed;
    }
    
    @FXML
    private void logOut() {
        Utils.changeScene("start.fxml", null, isMaximized);
        main_pane.getScene().getWindow().hide();
    }
    
    // endregion
    
    // region Private Helper Methods
    private void initForm() {
        costDur_lbl.setText("Cost: $0.00 Duration: 0.00");
        hour_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1, 1));
        minute_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15));
        services_combobox.setItems(FXCollections.observableArrayList(
                "Haircut", "Extension", "Coloring",
                "Straighten/Perm", "Braiding/Styling",
                "Manicure", "Pedicure", "Other Nail Service",
                "Facial", "Waxing", "Consultation"
        ));
        
        ObservableList<User> allUsers = SQLUtils.getAllUsers();
        if(allUsers == null) return;
        
        allUsers.forEach(user -> student_combobox.getItems().add(user.getUsername()));
        
        dateSelect_picker.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                
                LocalDate today = LocalDate.now();
                LocalDate oneYearFromToday = today.plusYears(1);
                if (date.isBefore(today) || date.isAfter(oneYearFromToday)) {
                    setDisable(true);
                    setStyle("-fx-background-color: lightgrey;");
                }
            }
        });
    }
    
    private void initSchedules() {
        ObservableList<Appointment> appointments = SQLUtils.getAllAppointments(1); // currentUser.getUserID()
        if (appointments == null) return;
        
        for (Appointment appointment : appointments) {
            int remainingMinutes = appointment.getDuration(),
                    currentMinute = appointment.getMinute(),
                    rowOffset = 0;
            
            while (remainingMinutes > 0) {
                int minutesThisHour = Math.min(60 - currentMinute, remainingMinutes);
                
                Pane pane = createAppointmentPane(appointment);
                GridPane.setRowIndex(pane, appointment.getHour() - 8 + rowOffset);
                GridPane.setColumnIndex(pane, currentMinute / 15 + 1);
                GridPane.setColumnSpan(pane, minutesThisHour / 15);
                
                switch(appointment.getDate().getDay()) {
                    case 1 -> System.out.println("monday");
                    case 2 -> System.out.println("Tuesday");
                    case 3 -> System.out.println("Wednesday");
                    case 5 -> System.out.println("Thursday");
                    case 4 -> monday_gridPane.getChildren().add(pane);
                    default -> System.out.println("Unknown date: " + appointment.getDate().getDay());
                }
                
                remainingMinutes -= minutesThisHour;
                currentMinute = 0;
                rowOffset++;
            }
        }
    }
    
    private Pane createAppointmentPane(Appointment appointment) {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: " + appointment.getColor() + ";");
        
        Label label = new Label(appointment.getCustomer() + ": " + appointment.getService() + " " + formatTime(appointment));
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        
        pane.getChildren().add(label);
        return pane;
    }
    
    private String formatTime(Appointment a) {
        int hour = a.getHour(),
                min = a.getMinute(),
                dur = a.getDuration(),
                endHour = hour,
                endMinute = min + dur;
        
        hour -= hour > 12 ? 12 : 0;
        while (endMinute > 60) {
            endHour++;
            endMinute -= 60;
        }
        
        return hour + ":" + min + "-" + endHour + ":" + endMinute;
    }
    // endregion
    
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
        Utils.windowDrag(event, main_pane);
    }
    
    @FXML
    private void windowMaximize() {
        if (!isMaximized) {
            Scene scene = main_pane.getScene();
            double initWidth = scene.getWidth(),
                    initHeight = scene.getHeight();
            
            defaultWidth = (defaultWidth == 0) ? scene.getWidth() : defaultWidth;
            defaultHeight = (defaultHeight == 0) ? scene.getHeight() : defaultHeight;
            
            Utils.windowMaximize(main_pane, initWidth, initHeight, false);
        } else
            Utils.windowMaximize(main_pane, defaultWidth, defaultHeight, true);
        
        isMaximized = !isMaximized;
    }
    // endregion
}
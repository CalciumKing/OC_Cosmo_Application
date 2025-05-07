package com.old_colony.oc_cosmo_application.Controllers;

import com.old_colony.oc_cosmo_application.Data.Appointment;
import com.old_colony.oc_cosmo_application.Data.Status;
import com.old_colony.oc_cosmo_application.Data.User;
import com.old_colony.oc_cosmo_application.SQLUtils;
import com.old_colony.oc_cosmo_application.Utils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.ResourceBundle;

@SuppressWarnings("SpellCheckingInspection")
public class DashboardController extends AbstractController implements Initializable {
    // region FXML Variables
    @FXML
    private Button home_btn, schedule_btn,
            account_btn, admin_btn;

    @FXML
    private ColorPicker color_picker, updateColor_picker;

    @FXML
    private Label welcome_lbl, costDur_lbl, windowTitle_lbl,
            id_label, username_label, secQuestion_label,
            secAnswer_label, status_label, date_label, customer_label,
            cost_label, service_label, student_label, duration_label;

    @FXML
    private TextArea note_area, updateNote_area;

    @FXML
    private AnchorPane home_pane, schedule_pane,
            account_pane, admin_pane;

    @FXML
    private GridPane monday_gridPane, tuesday_gridPane,
            wednesday_gridPane, thursday_gridPane, friday_gridPane;

    @FXML
    private TextField customerName_field, username_field, password_field,
            secQuestion_field, secAnswer_field, updateCustomerName_field;

    // region Tables
    @FXML
    private TableView<Appointment> dailySchedule_table,
            schedule_table, adminDeleteAppointment_table,
            adminUpdateAppointment_table;

    @FXML
    private TableView<User> users_table;

    @FXML
    private TableColumn<Appointment, Double> cost_col;

    @FXML
    private TableColumn<Appointment, String> date_col, custName_col, service_col,
            student_col, homeTime_col, homeName_col, homeService_col, adminDeleteDate_col,
            adminDeleteCust_col, adminDeleteService_col, adminDeleteStudent_col, adminUpdateDate_col,
            adminUpdateCust_col, adminUpdateService_col;

    @FXML
    private TableColumn<Appointment, Integer> duration_col;

    @FXML
    private TableColumn<User, String> usersUsername_col;

    @FXML
    private TableColumn<User, Status> usersStatus_col;
    // endregion

    @FXML
    private DatePicker dateSelect_picker, updateDate_picker;

    @FXML
    private Spinner<Integer> hour_spinner, minute_spinner,
            updateHour_spinner, updateMinute_spinner;

    @FXML
    private ComboBox<String> services_combobox, student_combobox,
            updateServices_combobox, updateStudent_combobox;

    @FXML
    private RadioButton am_radio, updateAm_radio, updatePm_radio, admin_radio, student_radio;

    @FXML
    private TabPane weekSchedules_tabPane;

    @FXML
    private VBox sideMenu;
    // endregion

    // region Private Variables
    private User currentUser;
    private boolean isCollapsed;
    private final LinkedHashMap<String, int[]> servicesAndCost = new LinkedHashMap<>(); // maintains inserted order
    // endregion

    /**
     * Method from {@code Initializable} interface
     * <p>Initializes everything that doesn't need user data.</p>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = null;
        initHashMap();
        initForm();
        initTabDays();
    }

    /**
     * Sets information around the application that uses user data like username, dark mode, and maximized
     * <p>Initializes everything that requires user data (everything that wasn't initialized already in above method)</p>
     * <p>Makes admin panel visible if currentUser has the status of admin</p>
     */
    @Override
    protected void init(User user, boolean isDarkMode, boolean isMaximized) {
        welcome_lbl.setText("Welcome, " + user.username());
        windowTitle_lbl.setText("Cosmetology Application | " + user.username());
        currentUser = user;

        // default is light mode and not maximized
        // toggles to the previous application page's settings if needed
        if (isDarkMode) toggleDarkMode();
        if (isMaximized) toggleMaximize();

        initTables();
        initNeatView();
        initAccountInfo();
        admin_btn.setVisible(currentUser.status().isAdmin());
    }

    // region FXML Methods
    @FXML
    private void setAMPM() {
        hour_spinner.setValueFactory(
                am_radio.isSelected() ?
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 12, 1, 1) :
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2, 1, 1)
        );
    }
    
    @FXML
    private void setUpdateAMPM() {
        updateHour_spinner.setValueFactory(
                updateAm_radio.isSelected() ?
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 12, 1, 1) :
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2, 1, 1)
        );
    }

    @FXML
    private void serviceSelect() {
        String service = services_combobox.getValue();

        // when clearing the service in resetForm, it triggers the onAction method with the value as null
        if (service == null || invalidService(service)) return;

        int[] item = servicesAndCost.get(service);

        int cost = item[0],
            duration = item[1],
            hour = hour_spinner.getValue(),
            minute = minute_spinner.getValue();

        invalidAppointmentTime(minute, hour, duration);

        costDur_lbl.setText("Cost: $" + cost + " Duration: " + duration);
    }

    /**
     * Sets all panes to not visible
     * <p>Finds what button the ActionEvent was triggered by</p>
     * <p>The list of buttons and list of panes have correlating indexes (both home pane and button are 0, both admin pane and button are 3)</p>
     * <p>When adding pages to dashboard, just add the pane to the list and its button to activate it at the same index in the buttons list</p>
     * @param event ActionEvent that caused the method to be called, should only be triggered by buttons
     */
    @FXML
    private void showPage(ActionEvent event) {
        AnchorPane[] panes = new AnchorPane[]{
                home_pane, schedule_pane,
                account_pane, admin_pane
        };
        for (AnchorPane pane : panes)
            pane.setVisible(false);

        Button[] buttons = new Button[]{
                home_btn, schedule_btn,
                account_btn, admin_btn
        };
        Button button = (Button) event.getSource();
        AnchorPane pageToShow = null;

        for (int i = 0; i < buttons.length; i++) {
            if (button.equals(buttons[i])) {
                pageToShow = panes[i];
                break;
            }
        }

        if (pageToShow != null)
            pageToShow.setVisible(true);
    }

    @FXML
    private void resetForm() {
        dateSelect_picker.setValue(null);
        hour_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 12, 8, 1));
        minute_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15));
        customerName_field.clear();
        services_combobox.setValue(null);
        student_combobox.setValue("Select Student");
        color_picker.setValue(Color.GREY);
        note_area.clear();
        am_radio.setSelected(true);
        costDur_lbl.setText("Cost: $0.00 Duration: 0.00");
    }
    
    @FXML
    private void resetUpdateAppointment() {
        updateDate_picker.setValue(null);
        updateCustomerName_field.clear();
        updateServices_combobox.setValue(null);
        updateStudent_combobox.setValue("Select Student");
        updateHour_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 12, 8, 1));
        updateMinute_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15));
        updateAm_radio.setSelected(true);
        color_picker.setValue(Color.GREY);
        updateNote_area.clear();
    }

    @FXML
    private void submitForm() {
        String customer = customerName_field.getText(),
                service = services_combobox.getValue();

        if (invalidService(service)) return;

        User user = SQLUtils.getUser(student_combobox.getValue());
        if (user == null) return;

        int hour = hour_spinner.getValue(),
                minute = minute_spinner.getValue(),
                studentID = user.userID(),
                cost = servicesAndCost.get(service)[0],
                duration = servicesAndCost.get(service)[1];

        if (invalidAppointmentTime(minute, hour, duration)) return;

        String note = note_area.getText();
        LocalDate date = dateSelect_picker.getValue();
        Color color = color_picker.getValue();

        if(!am_radio.isSelected())
            hour += 12;

        if (studentUnavailable(studentID, date, hour, minute, duration)) return;
        
        Appointment appointment = new Appointment(
                customer,
                user,
                color.toString(),
                service,
                cost,
                Date.valueOf(date),
                hour,
                minute,
                duration,
                note
        );

        SQLUtils.createAppointment(appointment);
        resetForm();
        reloadAppointmentTables();
    }

    @FXML
    private void addUser() {
        String username = username_field.getText(),
                password = password_field.getText(),
                secQuestion = secQuestion_field.getText(),
                secAnswer = secAnswer_field.getText();
        
        student_combobox.getItems().add(username);

        // check if a user with that name is already in database
        ObservableList<User> users = users_table.getItems();
        for (User user : users) {
            if (user.username().equals(username)) {
                Utils.normalAlert(
                        Alert.AlertType.ERROR,
                        "User Add Failure",
                        "User Already In Database",
                        "Please enter a username for a user that does not already exist"
                );
                return;
            }
        }
        
        User user = new User(
                username,
                password,
                secQuestion,
                secAnswer,
                -1, // not used
                Utils.createStatus(admin_radio.isSelected())
        );

        SQLUtils.createUser(user);
        reloadUserTable();
        clearUserForm();
    }

    @FXML
    private void deleteUser() {
        Optional<ButtonType> optionSelected = Utils.confirmAlert(
                Alert.AlertType.CONFIRMATION,
                "Confirm Delete User",
                "Are You Sure You Want To Delete This User?",
                "Delete user: " + username_field.getText(),
                "Yes, Delete " + username_field.getText(),
                "Cancel"
        );

        if (optionSelected.isPresent() && !optionSelected.get().getText().equals("Cancel")) {
            SQLUtils.deleteUser(username_field.getText());
            student_combobox.getItems().remove(username_field.getText());
            reloadUserTable();
            clearUserForm();
        }
    }

    @FXML
    private void updateUser() {
        String username = username_field.getText(),
                password = password_field.getText(),
                secQuestion = secQuestion_field.getText(),
                secAnswer = secAnswer_field.getText();
        
        // checks if user being edited is the current user
        if (username.equals(currentUser.username())) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "User Edit Failure",
                    "Cannot Edit Current User",
                    "Please edit a user that is not currently logged in on this device"
            );
            return;
        }

        // checks if username exists in table
        ObservableList<User> users = users_table.getItems();
        for (User user : users) {
            if (user.username().equals(username)) {
                User newUser = new User(
                        username,
                        password,
                        secQuestion,
                        secAnswer,
                        -1, // not used
                        Utils.createStatus(admin_radio.isSelected())
                );
                SQLUtils.updateUser(newUser);
                reloadUserTable();
                clearUserForm();
                return;
            }
        }

        // user not found
        Utils.normalAlert(
                Alert.AlertType.ERROR,
                "User Edit Failure",
                "No User Found With Username",
                "Please edit a user with a username that already exists in the table"
        );
    }

    @FXML
    private void clearUserForm() {
        username_field.clear();
        password_field.clear();
        secQuestion_field.clear();
        secAnswer_field.clear();
        student_radio.setSelected(true);
    }

    /**
     * Fills in the user form when the user table is clicked.
     * <p>If a table item is clicked it fills in the correct information, if a blank spot is clicked, nothing happens.</p>
     */
    @FXML
    private void userTableSelected() {
        User user = users_table.getSelectionModel().getSelectedItem();
        if (user == null) return;

        username_field.setText(user.username());
        password_field.setText(user.password());
        secQuestion_field.setText(user.securityQuestion());
        secAnswer_field.setText(user.securityAnswer());

        // no need to set other to false because they are in the same group
        if (user.status().isAdmin())
            admin_radio.setSelected(true);
        else
            student_radio.setSelected(true);
    }

    @FXML
    private void deleteAppointment() {
        Optional<ButtonType> optionSelected = Utils.confirmAlert(
                Alert.AlertType.CONFIRMATION,
                "Confirm Delete Appointment",
                "Are You Sure You Want To Delete This Appointment?",
                "Delete appointment: " + service_label.getText() + " for " + customer_label.getText(),
                "Yes, Delete " + service_label.getText(),
                "Cancel"
        );

        if (optionSelected.isPresent() && !optionSelected.get().getText().equals("Cancel")) {
            SQLUtils.deleteAppointment(student_label.getText(), service_label.getText());
            reloadAppointmentTables();
        }
    }
    
    @FXML
    private void updateAppointment() {
        String customer = updateCustomerName_field.getText(),
                service = updateServices_combobox.getValue();
        
        if (invalidService(service)) return;
        
        User user = SQLUtils.getUser(updateStudent_combobox.getValue());
        if (user == null) return;
        
        int hour = updateHour_spinner.getValue(),
                minute = updateMinute_spinner.getValue(),
                studentID = user.userID(),
                cost = servicesAndCost.get(service)[0],
                duration = servicesAndCost.get(service)[1];
        
        if (invalidAppointmentTime(minute, hour, duration)) return;
        
        String note = updateNote_area.getText();
        LocalDate date = updateDate_picker.getValue();
        Color color = updateColor_picker.getValue();
        
        if(!updateAm_radio.isSelected())
            hour += 12;
        
        if (studentUnavailable(studentID, date, hour, minute, duration)) return;
        
        Appointment newAppointment = new Appointment(
                customer,
                user,
                color.toString(),
                service,
                cost,
                Date.valueOf(date),
                hour,
                minute,
                duration,
                note
        ), oldAppointment = adminUpdateAppointment_table.getSelectionModel().getSelectedItem();
        
        SQLUtils.updateAppointment(newAppointment, oldAppointment);
        resetUpdateAppointment();
        reloadAppointmentTables();
    }

    /**
     * Fills in the appointment data in either the Delete Appointment tab or the Update Appointment tab when the appointment table is clicked.
     * <p>If a table item is clicked it fills in the correct information, if a blank spot is clicked, nothing happens.</p>
     */
    @FXML
    private void appointmentTablesSelected(MouseEvent event) {
        if(event.getSource().equals(adminDeleteAppointment_table)) {
            Appointment a = adminDeleteAppointment_table.getSelectionModel().getSelectedItem();
            if(a == null) return;
            
            date_label.setText(a.date().toString());
            customer_label.setText(a.customer());
            cost_label.setText("$" + a.cost());
            service_label.setText(a.service());
            student_label.setText(a.student().username());
            duration_label.setText(a.duration() + " minutes");
        } else if (event.getSource().equals(adminUpdateAppointment_table)) {
            Appointment a = adminUpdateAppointment_table.getSelectionModel().getSelectedItem();
            if(a == null) return;
            
            updateDate_picker.setValue(a.date().toLocalDate());
            updateCustomerName_field.setText(a.customer());
            updateServices_combobox.setValue(a.service());
            updateStudent_combobox.setValue(a.student().username());
            
            if(8 <= a.hour() && a.hour() <= 12) { // appointment is in the morning
                updateHour_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 12, a.hour(), 1));
                updateAm_radio.setSelected(true);
            } else {
                updateHour_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2, a.hour(), 1));
                updatePm_radio.setSelected(true);
            }
            
            updateMinute_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, a.minute(), 15));
            updateColor_picker.setValue(Color.valueOf(a.color()));
            updateNote_area.setText(a.note());
        }
    }

    /**
     * Hamburger button toggles the size of the side nav bar to be big
     * with text on each button or small with no text and only buttons
     * <p>The text for each of the buttons is stored in the individual button's 'user data'.</p>
     * <p>Information is stored in user data to dynamically save the button
     * text information without needing variables or an array.</p>
     */
    @FXML
    private void toggleMenu() {
        animateWidth(isCollapsed ? 200 : 50);
        for (Node node : sideMenu.getChildren()) {
            if (node instanceof Button button) {
                if (isCollapsed) // un-collapsing menu
                    button.setText((String) button.getUserData());
                else { // collapsing menu
                    button.setUserData(button.getText());
                    button.setText("");
                }
            }
        }

        isCollapsed = !isCollapsed;
    }

    @FXML
    private void logOut() {
        changeScene("start", null);
    }
    // endregion

    // region Helper Methods
    /**
     * Initializes all services in a LinkedHashMap
     * <p>LinkedHashMap is used instead of a HashMap because it keeps its order</p>
     */
    private void initHashMap() {
        Object[][] entries = {
                {"--- Cuts ---", -1, -1},
                {"Haircut (No Blow Dry)", 5, 45},
                {"Haircut (Blow Dry)", 8, 60},
                
                {"--- Color ---", -1, -1},
                {"Hair Color Retouch", 30, 90},
                {"Full Color Application", 35, 120},
                {"Cap Highlight", 25, 90},
                {"'Sunshine' Highlight", 25, 90},
                {"Partial Foil", 35, 120},
                {"Full Head Foil", 50, 150},
                {"Special Effects Highlight/Color", 45, 120},
                
                {"--- Styling ---", -1, -1},
                {"Formal Up Styles", 15, 45},
                {"Braids or Twists", 10, 30},
                {"Wash & Blow Dry", 8, 30},
                
                {"--- Smoothing & Conditioning ---", -1, -1},
                {"Conditioning Treatment & Blow Dry", 15, 45},
                {"Keratin Complex Express Blow Out", 60, 120},
                {"Keratin Complex Treatment", 90, 120},
                {"Perms", 35, 90},
                
                {"--- Nails ---", -1, -1},
                {"Manicure", 10, 30},
                {"Manicure With Paraffin Wax", 15, 45},
                {"Hot Oil Manicure", 12, 45},
                {"Paraffin Wax Drip", 5, 15},
                {"Pedicure", 15, 30},
                {"Gel Nail Polish & Manicure", 15, 45},
                {"Gel Polish Removal", 8, 30},
                
                {"--- Makeup & Facials ---", -1, -1},
                {"Makeup Application", 10, 30},
                {"Facial Application & Mask", 10, 45},
                
                {"--- Hair Shaping & Removal ---", -1, -1},
                {"Facial Hair Tweezing", 3, 15},
                {"Single Area Waxing", 5, 15},
                {"Two Area Waxing", 8, 30}
        };
        
        for(Object[] entry : entries)
            servicesAndCost.put((String) entry[0], new int[] {(int) entry[1], (int) entry[2]});
    }

    private void initForm() {
        services_combobox.setItems(FXCollections.observableArrayList(servicesAndCost.keySet()));

        ObservableList<User> allUsers = SQLUtils.getAllUsers();
        if (allUsers == null) return;

        allUsers.forEach(user -> student_combobox.getItems().add(user.username()));

        // sets calendar to only be able to schedule days up to a year from now and weekdays
        dateSelect_picker.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate today = LocalDate.now();
                LocalDate oneYearFromToday = today.plusYears(1);
                if (date.isBefore(today) || date.isAfter(oneYearFromToday) ||
                        date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                    setStyle("-fx-background-color: lightgrey;");
                }
            }
        });
        
        updateDate_picker.setDayCellFactory(dateSelect_picker.getDayCellFactory());
        updateServices_combobox.setItems(services_combobox.getItems());
        updateStudent_combobox.setItems(student_combobox.getItems());

        resetForm();
        resetUpdateAppointment();
    }

    /**
     * Removes the tabs for previous days in the week
     * <p>Ex: everything is visible on Saturday-Monday, only Friday is visible on Friday</p>
     * <p>Array is traversed backwards to avoid index skipping</p>
     */
    private void initTabDays() {
        ObservableList<Tab> tabs = weekSchedules_tabPane.getTabs();

        int i = switch (LocalDate.now().getDayOfWeek()) {
            case SATURDAY, SUNDAY, MONDAY -> -1; // remove nothing from Saturday-Monday
            case TUESDAY -> 0;
            case WEDNESDAY -> 1;
            case THURSDAY -> 2;
            case FRIDAY -> 3;
        };

        for (; i >= 0; i--)
            tabs.remove(i);
    }
    
    /**
     * Initializes all tables and each of their columns
     * <p>Only initializes admin tables if the current user is of admin status</p>
     */
    private void initTables() {
        ObservableList<Appointment> allAppointments = SQLUtils.getAllAppointments(-1);
        
        // home page
        homeTime_col.setCellValueFactory(cellData -> {
            Appointment a = cellData.getValue();
            LocalTime time = LocalTime.of(a.hour(), a.minute());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm");
            return new SimpleStringProperty(formatter.format(time));
        });
        homeName_col.setCellValueFactory(new PropertyValueFactory<>("customer"));
        homeService_col.setCellValueFactory(new PropertyValueFactory<>("service"));

        dailySchedule_table.setItems(SQLUtils.getTodayAppointments(currentUser.userID()));

        // schedule page
        date_col.setCellValueFactory(cellData -> {
            Appointment a = cellData.getValue();
            LocalTime time = LocalTime.of(a.hour(), a.minute());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm");
            return new SimpleObjectProperty<>(a.date() + " @ " + formatter.format(time));
        });
        cost_col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().cost()));
        custName_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().customer()));
        service_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().service()));
        student_col.setCellValueFactory(cellData -> { // get rid of later
            User user = cellData.getValue().student();
            String username = (user != null) ? user.username() : "Unknown";
            return new SimpleStringProperty(username);
        });
        duration_col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().duration()));

        schedule_table.setItems(allAppointments);

        // init admin tables
        if (currentUser.status().isAdmin()) {
            // users table
            usersUsername_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().username()));
            usersStatus_col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().status()));

            users_table.setItems(SQLUtils.getAllUsers());
            
            // update appointment table
            adminUpdateDate_col.setCellValueFactory(cellData -> {
                Appointment a = cellData.getValue();
                LocalTime time = LocalTime.of(a.hour(), a.minute());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm");
                return new SimpleObjectProperty<>(a.date() + " @ " + formatter.format(time));
            });
            adminUpdateCust_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().customer()));
            adminUpdateService_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().service()));
            
            adminUpdateAppointment_table.setItems(allAppointments);

            // delete appointment table
            adminDeleteDate_col.setCellValueFactory(adminUpdateDate_col.getCellValueFactory());
            adminDeleteCust_col.setCellValueFactory(adminUpdateCust_col.getCellValueFactory());
            adminDeleteService_col.setCellValueFactory(adminUpdateService_col.getCellValueFactory());
            adminDeleteStudent_col.setCellValueFactory(cellData -> {
                User user = cellData.getValue().student();
                String username = (user != null) ? user.username() : "Unknown";
                return new SimpleStringProperty(username);
            });
            
            adminDeleteAppointment_table.setItems(allAppointments);
        }
    }

    /**
     * Gets all appointments that the currentUser has scheduled and adds styled StackPanes to the neat view GridPane
     */
    private void initNeatView() {
        ObservableList<Appointment> appointments = SQLUtils.getAllAppointments(currentUser.userID());
        if (appointments == null) return;

        for (Appointment appointment : appointments) {
            int remainingMinutes = appointment.duration(),
                    currentMinute = appointment.minute(),
                    rowOffset = 0,
                    rowIndex = appointment.hour() % 8;

            while (remainingMinutes > 0) {
                int minutesThisHour = Math.min(60 - currentMinute, remainingMinutes);

                StackPane pane = createAppointmentPane(appointment, rowOffset);
                GridPane.setRowIndex(pane, rowIndex + rowOffset);
                GridPane.setColumnIndex(pane, currentMinute / 15 + 1);
                GridPane.setColumnSpan(pane, minutesThisHour / 15);

                GridPane day = getDay(appointment.date().toLocalDate());
                if (day == null) break;

                day.getChildren().add(pane);

                remainingMinutes -= minutesThisHour;
                currentMinute = 0;
                rowOffset++;
            }
        }
    }

    /**
     * Creates a StackPane and styles it based on the appointment
     * <p>Stack-panes are used instead of panes because they format their children, panes don't</p>
     * @param appointment the appointment that's data is used to style and add information to the StackPane
     * @param offset information is only added to the first panel, if the appointment should go to another line, other panels are styled but left blank
     * @return returns the styled panel as {@code StackPane}
     */
    private StackPane createAppointmentPane(Appointment appointment, int offset) {
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: " + appointment.color());

        if (offset == 0) { // only putting label on first appointment pane
            Label label = new Label(appointment.customer() + ": " + appointment.service() + " " + formatTime(appointment));
            if (appointment.note() != null && !appointment.note().isEmpty())
                label.setText(label.getText() + " (" + appointment.note() + ")");

            label.setWrapText(true);
            label.setAlignment(Pos.CENTER_LEFT);
            label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            StackPane.setAlignment(label, Pos.CENTER_LEFT);
            pane.getChildren().add(label);
        }

        return pane;
    }

    /**
     * Determines which grid-pane to be used based on a local-date parameter
     * @param localDate the LocalDate information of an appointment, used to find the day of the week of the appointment
     * @return a neat view grid pane based on the day of the week the appointment is booked on
     */
    private GridPane getDay(LocalDate localDate) {
        return switch (localDate.getDayOfWeek()) {
            case MONDAY -> monday_gridPane;
            case TUESDAY -> tuesday_gridPane;
            case WEDNESDAY -> wednesday_gridPane;
            case THURSDAY -> thursday_gridPane;
            case FRIDAY -> friday_gridPane;
            case SATURDAY, SUNDAY -> null; // no pane to add weekend appointments to
        };
    }
    
    /**
     * Fills in all the information on the account info page
     */
    private void initAccountInfo() {
        id_label.setText(String.valueOf(currentUser.userID()));
        username_label.setText(currentUser.username());
        secQuestion_label.setText(currentUser.securityQuestion() + ":");
        secAnswer_label.setText(currentUser.securityAnswer());
        status_label.setText(String.valueOf(currentUser.status()));
    }
    
    /**
     * Formats appointment time as HH:MM
     * @param a appointment data, used to get hour and minute
     * @return formatted time as a string
     */
    private String formatTime(Appointment a) {
        LocalTime start = LocalTime.of(a.hour(), a.minute()),
                end = start.plusMinutes(a.duration());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm");

        return formatter.format(start) + "-" + formatter.format(end);
    }

    private void reloadAppointmentTables() {
        dailySchedule_table.setItems(SQLUtils.getTodayAppointments(currentUser.userID()));
        ObservableList<Appointment> appointments = SQLUtils.getAllAppointments(-1);
        schedule_table.setItems(appointments);
        adminDeleteAppointment_table.setItems(appointments);
        adminUpdateAppointment_table.setItems(appointments);
        initNeatView();
    }

    private void reloadUserTable() {
        users_table.setItems(SQLUtils.getAllUsers());
    }

    /**
     * Animates sideMenu animation
     * @param endWidth the desired width of the side menu when the animation is finished
     */
    private void animateWidth(double endWidth) {
        KeyValue widthValue = new KeyValue(sideMenu.prefWidthProperty(), endWidth, Interpolator.LINEAR);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(350), widthValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    /**
     * Checks if the service select is invalid.
     * <p>An invalid service would be one of the divider items containing ---Text---</p>
     * <p>Dividers were put there to organize the dropdown the same way the Locks pamphlet was organized</p>
     * @param service the service item that was selected
     * @return returns {@code false} if the service IS valid (not an invalid service)
     * and throws an alert and returns {@code true} otherwise (is an invalid service)
     */
    private boolean invalidService(String service) {
        if (service.contains("---")) { // divider, not a valid service
            Utils.normalAlert(
                    Alert.AlertType.INFORMATION,
                    "Divider Selected",
                    "Cannot Select A Divider For A Service",
                    "Please select a valid service that is not a divider item."
            );
            return true;
        }
        return false;
    }

    /**
     * Checks if the appointment duration will go after school
     * @param minute the user selected start minute of the service
     * @param duration the constant duration of the service
     * @param hour the user selected start hour of the service
     * @return returns {@code false} if the appointment IS valid (not an invalid appointment)
     * or throws an alert and returns {@code true} otherwise (appointment goes after school day).
     * Base case returns {@code false} if appointment is scheduled in the morning because
     * no services are long enough to last from morning until end of the day
     */
    private boolean invalidAppointmentTime(int minute, int hour, int duration) {
        if(am_radio.isSelected()) return false;

        hour += 12;

        LocalTime requestedStartTime = LocalTime.of(hour, minute),
                requestedEndTime = requestedStartTime.plusMinutes(duration),
                schoolDayEndTime = LocalTime.of(14, 20);

        if(requestedEndTime.isAfter(schoolDayEndTime)) {
            Utils.normalAlert(
                    Alert.AlertType.INFORMATION,
                    "Duration Error",
                    "Appointment Duration Exceeds School Day",
                    "Please enter a different service that will take less time or schedule it for a different time"
            );
            return true;
        }
        
        return false;
    }

    /**
     * Checks if the student to complete service is unavailable
     * (other appointment is already scheduled during user selected appointment)
     * @param studentID id of student to service the appointment
     * @param date user selected date of appointment
     * @param hour user selected start hour of appointment
     * @param minute user selected start minute of appointment
     * @param duration duration of service
     * @return returns {@code false} if user IS available (user is not unavailable)
     * or throws an alert and returns {@code true} otherwise (user is busy at user selected time).
     * Base case returns {@code true} (user is unavailable) if appointments cannot be fetched
     */
    private boolean studentUnavailable(int studentID, LocalDate date, int hour, int minute, int duration) {
        ObservableList<Appointment> appointments = SQLUtils.getAllAppointments(studentID);
        if(appointments == null) return true;

        for(Appointment a : appointments) {
            if(a.date().toLocalDate().equals(date)) {
                LocalTime requestedStartTime = LocalTime.of(hour, minute),
                        requestedEndTime = requestedStartTime.plusMinutes(duration),
                        appStartTime = LocalTime.of(a.hour(), a.minute()),
                        appEndTime = appStartTime.plusMinutes(a.duration());

                if(requestedStartTime == appStartTime || requestedEndTime == appEndTime ||
                        requestedStartTime.isBefore(appEndTime) && requestedEndTime.isAfter(appStartTime)) {
                    Utils.normalAlert(
                            Alert.AlertType.INFORMATION,
                            "Appointment Time Error",
                            "Student Already Has Appointment At That Time",
                            "Student is doing: " + a.customer() + " - " + a.service()
                    );
                    return true;
                }
            }
        }
        return false;
    }
    // endregion
}
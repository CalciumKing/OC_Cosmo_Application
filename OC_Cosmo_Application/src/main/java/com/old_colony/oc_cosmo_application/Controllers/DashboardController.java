package com.old_colony.oc_cosmo_application.Controllers;

import com.old_colony.oc_cosmo_application.DataClasses.Appointment;
import com.old_colony.oc_cosmo_application.DataClasses.Status;
import com.old_colony.oc_cosmo_application.DataClasses.User;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

public final class DashboardController extends AbstractController implements Initializable {
    // region FXML Variables
    @FXML
    private Button home_btn, schedule_btn,
            account_btn, admin_btn, logOut_btn;

    @FXML
    private ColorPicker color_picker;

    @FXML
    private Label welcome_lbl, costDur_lbl, windowTitle_lbl,
            id_label, username_label, secQuestion_label,
            secAnswer_label, status_label, date_label, customer_label,
            cost_label, service_label, student_label, duration_label;

    @FXML
    private TextArea note_area;

    @FXML
    private AnchorPane home_pane, schedule_pane,
            account_pane, admin_pane;

    @FXML
    private GridPane monday_gridPane, tuesday_gridPane,
            wednesday_gridPane, thursday_gridPane, friday_gridPane;

    @FXML
    private TextField customerName_field, username_field, password_field,
            secQuestion_field, secAnswer_field;

    // region Tables
    @FXML
    private TableView<Appointment> dailySchedule_table,
            schedule_table, adminAppointment_table;

    @FXML
    private TableView<User> users_table;

    @FXML
    private TableColumn<Appointment, Double> cost_col;

    @FXML
    private TableColumn<Appointment, String> date_col, custName_col, service_col,
            student_col, time_col, homeName_col, homeService_col, adminDate_col,
            adminCust_col, adminService_col, adminStudent_col;

    @FXML
    private TableColumn<Appointment, Integer> duration_col;

    @FXML
    private TableColumn<User, String> usersUsername_col;

    @FXML
    private TableColumn<User, Status> usersStatus_col;
    // endregion

    @FXML
    private DatePicker dateSelect_picker;

    @FXML
    private Spinner<Integer> hour_spinner, minute_spinner;

    @FXML
    private ComboBox<String> services_combobox, student_combobox;

    @FXML
    private RadioButton am_radio, admin_radio, student_radio;

    @FXML
    private TabPane weekSchedules_tabPane;

    @FXML
    private VBox sideMenu;
    // endregion

    // region Private Variables
    private User currentUser;
    private boolean isCollapsed;
    private final HashMap<String, ArrayList<Integer>> servicesAndCost = new HashMap<>();
    // endregion

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = null;
        initHashMap();
        initForm();
        initTabDays();
    }

    @Override
    protected void init(User user, boolean isDarkMode, boolean isMaximized) {
        welcome_lbl.setText("Welcome, " + user.getUsername());
        windowTitle_lbl.setText("Cosmetology Application | " + user.getUsername());
        currentUser = user;

        // default is light mode and not maximized
        // toggles to the previous applications settings if needed
        if (isDarkMode) toggleDarkMode();
        if (isMaximized) toggleMaximize();

        initTables();
        initSchedules();
        initAccountInfo();
        admin_btn.setVisible(currentUser.getStatus().isAdmin());
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
    private void showPage(ActionEvent event) {
        AnchorPane[] panes = new AnchorPane[]{
                home_pane, schedule_pane,
                account_pane, admin_pane
        };
        for (AnchorPane pane : panes)
            pane.setVisible(false);

        Button[] buttons = new Button[]{
                home_btn, schedule_btn,
                account_btn, admin_btn, logOut_btn
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
        hour_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 12, 1, 1));
        minute_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15));
        customerName_field.clear();
        services_combobox.setValue("Services");
        student_combobox.setValue("Select Student");
        color_picker.setValue(Color.GREY);
        note_area.clear();
        setAMPM();
        costDur_lbl.setText("Cost: $0.00 Duration: 0.00");
    }

    @FXML
    private void submitForm() {
        String customer = customerName_field.getText(),
                service = services_combobox.getValue();

        if (service.contains("---")) { // divider, not a valid service
            Utils.normalAlert(
                    Alert.AlertType.INFORMATION,
                    "Divider Selected",
                    "Cannot Select A Divider For A Service",
                    "Please select a valid service that is not a divider item."
            );
            return;
        }

        User user = SQLUtils.getUser(student_combobox.getValue());
        if (user == null) return;

        int hour = hour_spinner.getValue(),
                minute = minute_spinner.getValue(),
                studentID = user.getUserID(),
                cost = servicesAndCost.get(service).getFirst(),
                duration = servicesAndCost.get(service).getLast();

        String note = note_area.getText();
        LocalDate date = dateSelect_picker.getValue();
        Color color = color_picker.getValue();

        SQLUtils.createAppointment(hour, minute, duration, studentID, customer, service, cost, date, color, note);
        resetForm();
        reloadAppointmentTables();
    }

    @FXML
    private void addUser() {
        String username = username_field.getText(),
                password = password_field.getText(),
                secQuestion = secQuestion_field.getText(),
                secAnswer = secAnswer_field.getText();

        boolean isAdmin = admin_radio.isSelected();

        // check if a user with that name is already in database
        ObservableList<User> users = users_table.getItems();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                Utils.normalAlert(
                        Alert.AlertType.ERROR,
                        "User Add Failure",
                        "User Already In Database",
                        "Please enter a username for a user that does not already exist"
                );
                return;
            }
        }

        SQLUtils.createUser(username, password, secQuestion, secAnswer, isAdmin);
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

        boolean isAdmin = admin_radio.isSelected();

        // checks if user being edited is the current user
        if (username.equals(currentUser.getUsername())) {
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
            if (user.getUsername().equals(username)) {
                SQLUtils.updateUser(username, password, secQuestion, secAnswer, isAdmin);
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

    @FXML
    private void userTableSelected() {
        User user = users_table.getSelectionModel().getSelectedItem();
        if (user == null) return;

        username_field.setText(user.getUsername());
        password_field.setText(user.getPassword());
        secQuestion_field.setText(user.getSecurityQuestion());
        secAnswer_field.setText(user.getSecurityAnswer());

        // no need to set other to false because they are in the same group
        if (user.getStatus().isAdmin())
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
    private void appointmentTableSelected() {
        Appointment a = adminAppointment_table.getSelectionModel().getSelectedItem();
        if (a == null) return;

        date_label.setText(a.getDate().toString());
        service_label.setText(a.getCustomer());
        cost_label.setText("$" + a.getCost());
        service_label.setText(a.getService());
        User student = a.getStudent();
        student_label.setText((student == null) ? "Error" : student.getUsername());
        duration_label.setText(a.getDuration() + " minutes");
    }

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
        changeScene("start.fxml", null);
        main_pane.getScene().getWindow().hide();
    }
    // endregion

    // region Helper Methods
    private void initHashMap() {
        addService("--- Cuts ---", -1, -1);
        addService("Haircut (No Blow Dry)", 5, 45);
        addService("Haircut (Blow Dry)", 8, 60);
        addService("--- Color ---", -1, -1);
        addService("Hair Color Retouch", 30, 90);
        addService("Full Color Application", 35, 120);
        addService("Cap Highlight", 25, 90);
        addService("'Sunshine' Highlight", 25, 90);
        addService("Partial Foil", 35, 120);
        addService("Full Head Foil", 50, 150);
        addService("Special Effects Highlight/Color", 45, 120);
        addService("--- Styling ---", -1, -1);
        addService("Formal Up Styles", 15, 45);
        addService("Braids or Twists", 10, 30);
        addService("Wash & Blow Dry", 8, 30);
        addService("--- Smoothing & Conditioning ---", -1, -1);
        addService("Conditioning Treatment & Blow Dry", 15, 45);
        addService("Keratin Complex Express Blow Out", 60, 120);
        addService("Keratin Complex Treatment", 90, 120);
        addService("Perms", 35, 90);
        addService("--- Nails ---", -1, -1);
        addService("Manicure", 10, 30);
        addService("Manicure With Paraffin Wax", 15, 45);
        addService("Hot Oil Manicure", 12, 45);
        addService("Paraffin Wax Drip", 5, 15);
        addService("Pedicure", 15, 30);
        addService("Gel Nail Polish & Manicure", 15, 45);
        addService("Gel Polish Removal", 8, 30);
        addService("--- Makeup & Facials ---", -1, -1);
        addService("Makeup Application", 10, 30);
        addService("Facial Application & Mask", 10, 45);
        addService("--- Hair Shaping & Removal ---", -1, -1);
        addService("Facial Hair Tweezing", 3, 15);
        addService("Single Area Waxing", 5, 15);
        addService("Two Area Waxing", 8, 30);
    }

    private void addService(String name, int cost, int duration) {
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(cost);
        temp.add(duration);
        servicesAndCost.put(name, temp);
    }

    private void initForm() {
        services_combobox.setItems(FXCollections.observableArrayList(servicesAndCost.keySet()));

        // make only certain users available
        ObservableList<User> allUsers = SQLUtils.getAllUsers();
        if (allUsers == null) return;

        allUsers.forEach(user -> student_combobox.getItems().add(user.getUsername()));

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

        resetForm();
    }

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

    private void initTables() {
        time_col.setCellValueFactory(cellData -> {
            Appointment a = cellData.getValue();
            return new SimpleStringProperty(a.getHour() + ":" + a.getMinute());
        });
        homeName_col.setCellValueFactory(new PropertyValueFactory<>("customer"));
        homeService_col.setCellValueFactory(new PropertyValueFactory<>("service"));

        dailySchedule_table.setItems(SQLUtils.getTodayAppointments(currentUser.getUserID()));

        date_col.setCellValueFactory(cellData -> {
            Appointment a = cellData.getValue();
            return new SimpleObjectProperty<>(a.getDate() + " @ " + a.getHour() + ":" + a.getMinute());
        });
        cost_col.setCellValueFactory(new PropertyValueFactory<>("cost"));
        custName_col.setCellValueFactory(new PropertyValueFactory<>("customer"));
        service_col.setCellValueFactory(new PropertyValueFactory<>("service"));
        student_col.setCellValueFactory(cellData -> { // get rid of later
            User user = cellData.getValue().getStudent();
            String username = (user != null) ? user.getUsername() : "Unknown";
            return new SimpleStringProperty(username);
        });
        duration_col.setCellValueFactory(new PropertyValueFactory<>("duration"));

        schedule_table.setItems(SQLUtils.getAllAppointments(-1));

        if (currentUser.getStatus().isAdmin()) { // init admin tables
            usersUsername_col.setCellValueFactory(new PropertyValueFactory<>("username"));
            usersStatus_col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus())); // new PropertyValueFactory<>("status")

            users_table.setItems(SQLUtils.getAllUsers());

            adminDate_col.setCellValueFactory(cellData -> {
                Appointment a = cellData.getValue();
                return new SimpleObjectProperty<>(a.getDate() + " @ " + a.getHour() + ":" + a.getMinute());
            });
            adminCust_col.setCellValueFactory(new PropertyValueFactory<>("customer"));
            adminService_col.setCellValueFactory(new PropertyValueFactory<>("service"));
            adminStudent_col.setCellValueFactory(cellData -> { // get rid of later
                User user = cellData.getValue().getStudent();
                String username = (user != null) ? user.getUsername() : "Unknown";
                return new SimpleStringProperty(username);
            });

            adminAppointment_table.setItems(SQLUtils.getAllAppointments(-1));
        }
    }

    private void initSchedules() {
        ObservableList<Appointment> appointments = SQLUtils.getAllAppointments(currentUser.getUserID());
        if (appointments == null) return;

        for (Appointment appointment : appointments) {
            int remainingMinutes = appointment.getDuration(),
                    currentMinute = appointment.getMinute(),
                    rowOffset = 0,
                    rowIndex = appointment.getHour() % 8;

            while (remainingMinutes > 0) {
                int minutesThisHour = Math.min(60 - currentMinute, remainingMinutes);

                Pane pane = createAppointmentPane(appointment, rowOffset);
                GridPane.setRowIndex(pane, rowIndex + rowOffset);
                GridPane.setColumnIndex(pane, currentMinute / 15 + 1);
                GridPane.setColumnSpan(pane, minutesThisHour / 15);

                GridPane day = getDay(appointment);
                if (day == null) break;

                day.getChildren().add(pane);

                remainingMinutes -= minutesThisHour;
                currentMinute = 0;
                rowOffset++;
            }
        }
    }

    private GridPane getDay(Appointment appointment) {
        return switch (appointment.getDate().toLocalDate().getDayOfWeek()) {
            case MONDAY -> monday_gridPane;
            case TUESDAY -> tuesday_gridPane;
            case WEDNESDAY -> wednesday_gridPane;
            case THURSDAY -> thursday_gridPane;
            case FRIDAY -> friday_gridPane;
            case SATURDAY, SUNDAY -> null; // no pane to add weekend appointments to
        };
    }

    private void initAccountInfo() {
        id_label.setText(String.valueOf(currentUser.getUserID()));
        username_label.setText(currentUser.getUsername());
        secQuestion_label.setText(currentUser.getSecurityQuestion() + ":");
        secAnswer_label.setText(currentUser.getSecurityAnswer());
        status_label.setText(String.valueOf(currentUser.getStatus()));
    }

    private Pane createAppointmentPane(Appointment appointment, int offset) {
        // StackPanes instead of panes because they format their children
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: " + appointment.getColor()); // must be one line

        if (offset == 0) { // only putting label on first appointment pane
            Label label = new Label(appointment.getCustomer() + ": " + appointment.getService() + " " + formatTime(appointment));
            if (appointment.getNote() != null && !appointment.getNote().isEmpty())
                label.setText(label.getText() + "(" + appointment.getNote() + ")");

            label.setWrapText(true);
            label.setAlignment(Pos.CENTER_LEFT);
            label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            StackPane.setAlignment(label, Pos.CENTER_LEFT);
            pane.getChildren().add(label);
        }

        return pane;
    }

    private String formatTime(Appointment a) {
        int hour = a.getHour(),
                min = a.getMinute(),
                dur = a.getDuration(),
                endHour = hour,
                endMinute = min + dur;

        while (endMinute >= 60) {
            endHour++;
            endMinute -= 60;
        }

        hour -= hour > 12 ? 12 : 0;
        endHour -= endHour > 12 ? 12 : 0;

        return hour + ":" + ((min == 0) ? "00" : min) + "-" + endHour + ":" + ((endMinute == 0) ? "00" : endMinute);
    }

    private void reloadAppointmentTables() {
        dailySchedule_table.setItems(SQLUtils.getTodayAppointments(currentUser.getUserID()));
        ObservableList<Appointment> appointments = SQLUtils.getAllAppointments(-1);
        schedule_table.setItems(appointments);
        adminAppointment_table.setItems(appointments);
        initSchedules();
    }

    private void reloadUserTable() {
        users_table.setItems(SQLUtils.getAllUsers());
    }

    private void animateWidth(double endWidth) {
        KeyValue widthValue = new KeyValue(sideMenu.prefWidthProperty(), endWidth, Interpolator.LINEAR);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(350), widthValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }
    // endregion
}
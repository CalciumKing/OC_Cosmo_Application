package com.old_colony.oc_cosmo_application.Controllers;

import com.old_colony.oc_cosmo_application.Data.Appointment;
import com.old_colony.oc_cosmo_application.Data.User;
import com.old_colony.oc_cosmo_application.Misc.PDFGenerator;
import com.old_colony.oc_cosmo_application.Misc.SQLUtils;
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
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Controller for analytics.fxml (appointment and user data view/management page)
 */

public class AnalyticsController extends AbstractController {
    // region FXML Variables
    @FXML
    private AnchorPane home_pane, summary_pane,
            charts_pane, tables_pane, pdf_pane;

    @FXML
    private Label totalAppointments_lbl, mostActive_lbl,
            mostProfitable_lbl, totalRev_lbl,
            avgDuration_lbl, mostBookedDay_lbl,
            saveFolder_lbl;

    @FXML
    private LineChart<String, Integer> appointments_lineChart;

    @FXML
    private BarChart<String, Double> revenue_barChart;

    @FXML
    private PieChart services_pieChart;

    @FXML
    private TableView<Appointment> appointments_table, service_table;

    @FXML
    private TableView<User> students_table;

    @FXML
    private TableColumn<Appointment, String> date_col, custName_col,
            service_col, student_col, serviceName_col;

    @FXML
    private TableColumn<Appointment, Double> cost_col,
            totalRev_col, serviceAvgDuration_col;

    @FXML
    private TableColumn<Appointment, Integer> duration_col, times_col;

    @FXML
    private TableColumn<User, String> name_col;

    @FXML
    private TableColumn<User, Integer> appointment_col;

    @FXML
    private TableColumn<User, Double> earnings_col, studentAvgDuration_col;

    @FXML
    private Button home_btn, summary_btn, charts_btn, tables_btn, all, weekly, daily, pdf_btn;

    @FXML
    private VBox sideMenu;
    // endregion

    // region Non-FXML Variables
    private boolean isCollapsed;

    private User currentUser;

    @SuppressWarnings("FieldMayBeFinal")
    private ObservableList<Appointment> allAppointments = SQLUtils.getAllAppointments(-1);

    private String savePath;
    // endregion

    // region Override Methods
    /**
     * {@inheritDoc}
     * <br>
     * Sets information around the application like dark mode and maximized
     * <p>Nothing in this controller uses user data, but user data is stored for returning to {@link DashboardController}</p>
     *
     * @see DashboardController
     */
    @Override
    protected void init(User user, boolean isDarkMode, boolean isMaximized) {
        // default is light mode and not maximized
        // toggles to the previous applications settings if needed
        if (isDarkMode) toggleDarkMode();
        if (isMaximized) toggleMaximize();

        currentUser = user;

        initCharts();
        initSummary();
        initTables();
        handleShortcuts();
    }

    /**
     * {@inheritDoc}
     * <br>
     * Keys 1-5 switch user to corresponding page, S toggles the side menu.
     */
    @Override
    protected void handleShortcuts() {
        main_pane.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DIGIT1 -> home_btn.fire();
                case DIGIT2 -> summary_btn.fire();
                case DIGIT3 -> charts_btn.fire();
                case DIGIT4 -> tables_btn.fire();
                case DIGIT5 -> pdf_btn.fire();
                case S -> toggleMenu();
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
    @FXML
    private void exit() {
        changeScene("dashboard", currentUser);
    }

    /**
     * Sets all panes to not visible
     * <p>Finds what button the ActionEvent was triggered by</p>
     * <p>The list of buttons and list of panes have correlating indexes (both home pane and button are 0, both admin pane and button are 3)</p>
     * <p>When adding pages to dashboard, just add the pane to the list and its button to activate it at the same index in the buttons list</p>
     *
     * @param event ActionEvent that caused the method to be called, should only be triggered by buttons
     */
    @FXML
    private void showPage(ActionEvent event) {
        AnchorPane[] panes = new AnchorPane[] {
                home_pane, summary_pane,
                charts_pane, tables_pane,
                pdf_pane
        };
        for (AnchorPane pane : panes)
            pane.setVisible(false);

        Button[] buttons = new Button[] {
                home_btn, summary_btn,
                charts_btn, tables_btn,
                pdf_btn
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

    /**
     * Hamburger button toggles the size of the side nav bar to be big
     * with text on each button or small with no text and only buttons
     * <p>The text for each of the buttons is stored in the individual button's 'user data'.</p>
     * <p>Information is stored in user data to dynamically save the button
     * text information without needing variables or an array.</p>
     */
    @FXML
    private void toggleMenu() {
        animateWidth(isCollapsed ? 160 : 50);
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

    /**
     * Determines which PDF to generate based on the button pressed
     *
     * @param event source button
     */
    @FXML
    private void getPushedPDFButton(ActionEvent event) {
        if (event.getSource() == all)
            PDFGenerator.createPDF("all", savePath);
        else if (event.getSource() == weekly)
            PDFGenerator.createPDF("weekly", savePath);
        else if (event.getSource() == daily)
            PDFGenerator.createPDF("daily", savePath);
    }

    @FXML
    private void folderSelect() {
        DirectoryChooser open = new DirectoryChooser();
        open.setTitle("Open Folder To Download PDFs");
        open.setInitialDirectory(new File("src/main/resources/PDFs"));

        Stage stage = (Stage) main_pane.getScene().getWindow();

        File selectedDirectory = open.showDialog(stage);

        if (selectedDirectory != null) {
            savePath = selectedDirectory.getAbsolutePath();
            saveFolder_lbl.setText(savePath);
        }
    }
    // endregion

    // region Helper Methods
    /**
     * Animates sideMenu animation
     *
     * @param endWidth the desired width of the side menu when the animation is finished
     */
    private void animateWidth(double endWidth) {
        KeyValue widthValue = new KeyValue(sideMenu.prefWidthProperty(), endWidth, Interpolator.LINEAR);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(350), widthValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    private void initSummary() {
        totalAppointments_lbl.setText("" + allAppointments.size());
        mostActive_lbl.setText(getMostActive(getActiveStudents()));
        mostProfitable_lbl.setText(getMostProfitable());
        totalRev_lbl.setText(getTotalProfits());
        avgDuration_lbl.setText(getAvgDuration());
        mostBookedDay_lbl.setText(getMostBookedDay());
    }

    private void initTables() {
        HashMap<Integer, ArrayList<Appointment>> appointmentsByUser = new HashMap<>();
        for (Appointment a : allAppointments) {
            if (a.student() != null) {
                int id = a.student().userID();
                if (!appointmentsByUser.containsKey(id))
                    appointmentsByUser.put(id, new ArrayList<>());
                appointmentsByUser.get(id).add(a);
            }
        }

        // all appointments tab
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm");
        date_col.setCellValueFactory(cellData -> {
            Appointment a = cellData.getValue();
            return new SimpleStringProperty(a.date() + " @ " + formatter.format(LocalTime.of(a.hour(), a.minute())));
        });
        cost_col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().cost()));
        custName_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().customer()));
        service_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().service()));
        student_col.setCellValueFactory(cellData -> {
            User user = cellData.getValue().student();
            String username = (user != null) ? user.username() : "Unknown";
            return new SimpleStringProperty(username);
        });
        duration_col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().duration()));

        appointments_table.setItems(allAppointments);

        // student summary tab
        name_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().username()));
        appointment_col.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new SimpleObjectProperty<>(appointmentsByUser.get(user.userID()).size());
        });
        earnings_col.setCellValueFactory(cellData -> {
            User user = cellData.getValue();

            double total = 0;
            for (Appointment a : appointmentsByUser.get(user.userID()))
                if (a.date().before(new Date()))
                    total += a.cost();

            return new SimpleObjectProperty<>(total);
        });
        studentAvgDuration_col.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            ArrayList<Appointment> appointments = appointmentsByUser.get(user.userID());

            double avg = 0;
            for (Appointment a : appointments)
                if (a.date().before(new Date()))
                    avg += a.duration();

            avg /= appointments.size();

            return new SimpleObjectProperty<>(Double.valueOf(String.format("%.2f", avg)));
        });

        students_table.setItems(SQLUtils.getAllUsers());

        // service summary tab
        HashMap<String, double[]> appointments = new HashMap<>();
        for (Appointment a : allAppointments) {
            String service = a.service();
            if (appointments.containsKey(service)) {
                double[] cur = appointments.get(service);
                appointments.put(a.service(), new double[] {
                        cur[0] + 1,
                        cur[1] + a.cost(),
                        cur[2]
                });
            } else
                appointments.put(service, new double[] { 1, a.cost(), a.duration() });
        }
        HashSet<String> seen = new HashSet<>();
        ObservableList<Appointment> uniqueAppointments = allAppointments.filtered(appointment -> {
            String service = appointment.service();
            if (seen.contains(service))
                return false;
            else {
                seen.add(service);
                return true;
            }
        });

        serviceName_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().service()));
        times_col.setCellValueFactory(cellData -> new SimpleObjectProperty<>((int) appointments.get(cellData.getValue().service())[0]));
        totalRev_col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(appointments.get(cellData.getValue().service())[1]));
        serviceAvgDuration_col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(appointments.get(cellData.getValue().service())[2]));

        service_table.setItems(uniqueAppointments);
    }

    private void initCharts() {
        HashMap<String, Double> services = new HashMap<>();
        for (Appointment a : allAppointments) {
            String name = a.service();
            if (!services.containsKey(name))
                services.put(name, 0.0);
            services.put(name, services.get(name) + a.cost());
        }

        weeklyAppointmentsLineGraph();
        revenueByServiceBarChart(services);
        pieChart(services);
    }

    private void pieChart(HashMap<String, Double> services) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : services.entrySet())
            data.add(new PieChart.Data(entry.getKey(), entry.getValue()));

        services_pieChart.setData(data);
    }

    private void revenueByServiceBarChart(HashMap<String, Double> services) {
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        series.setName("Services");
        for (Map.Entry<String, Double> entry : services.entrySet())
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));

        revenue_barChart.getData().add(series);
    }

    private void weeklyAppointmentsLineGraph() {
        LinkedHashMap<DayOfWeek, Integer> appointmentsPerDay = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        appointmentsPerDay.put(DayOfWeek.MONDAY, 0);
        appointmentsPerDay.put(DayOfWeek.TUESDAY, 0);
        appointmentsPerDay.put(DayOfWeek.WEDNESDAY, 0);
        appointmentsPerDay.put(DayOfWeek.THURSDAY, 0);
        appointmentsPerDay.put(DayOfWeek.FRIDAY, 0);

        for (Appointment a : allAppointments) { // getting only this week's appointments
            LocalDate date = a.date().toLocalDate(),
                    monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                    friday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

            if (!date.isBefore(monday) && !date.isAfter(friday)) {
                DayOfWeek day = date.getDayOfWeek();
                appointmentsPerDay.put(day, appointmentsPerDay.get(day) + 1);
            }
        }

        // adding the valid days of the week and the values to the series
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Services Per Day");
        for (Map.Entry<DayOfWeek, Integer> entry : appointmentsPerDay.entrySet())
            series.getData().add(new XYChart.Data<>(entry.getKey().name(), entry.getValue()));

        appointments_lineChart.getData().add(series);
    }

    private String getMostBookedDay() {
        HashMap<DayOfWeek, Integer> days = new HashMap<>();
        for (Appointment a : allAppointments) {
            DayOfWeek day = a.date().toLocalDate().getDayOfWeek();
            if (!days.containsKey(day))
                days.put(day, 0);
            days.put(day, days.get(day) + 1);
        }

        DayOfWeek day = DayOfWeek.SATURDAY;
        int most = 0;
        for (Map.Entry<DayOfWeek, Integer> entry : days.entrySet()) {
            if (entry.getValue() > most) {
                day = entry.getKey();
                most = entry.getValue();
            }
        }

        return day + ": " + most;
    }

    private String getAvgDuration() {
        double avg = 0;
        for (Appointment a : allAppointments)
            avg += a.duration();
        avg /= allAppointments.size();
        return String.format("%.2f", avg) + " minutes";
    }

    private String getTotalProfits() {
        double sum = 0;
        for (Appointment a : allAppointments)
            if (a.date().before(new Date()))
                sum += a.cost();
        return "$" + sum;
    }

    private String getMostProfitable() {
        HashMap<String, Double> students = new HashMap<>();
        for (Appointment a : allAppointments) {
            String username = a.student().username();
            if (!students.containsKey(username))
                students.put(username, 0.0);
            students.put(username, students.get(username) + a.cost());
        }

        String student = "";
        double highest = 0;
        for (Map.Entry<String, Double> entry : students.entrySet()) {
            if (entry.getValue() > highest) {
                student = entry.getKey();
                highest = entry.getValue();
            }
        }

        return student + ": " + highest;
    }

    private String getMostActive(HashMap<String, Integer> activeStudents) {
        String student = "";
        int highest = 0;
        for (Map.Entry<String, Integer> entry : activeStudents.entrySet()) {
            if (entry.getValue() > highest) {
                student = entry.getKey();
                highest = entry.getValue();
            }
        }
        return student;
    }

    private HashMap<String, Integer> getActiveStudents() {
        HashMap<String, Integer> students = new HashMap<>();
        for (Appointment a : allAppointments) {
            String username = a.student().username();
            if (!students.containsKey(username))
                students.put(username, 0);
            students.put(username, students.get(username) + 1);
        }
        return students;
    }
    // endregion
}
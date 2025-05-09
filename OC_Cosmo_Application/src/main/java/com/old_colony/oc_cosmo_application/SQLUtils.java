package com.old_colony.oc_cosmo_application;

import com.old_colony.oc_cosmo_application.Data.Appointment;
import com.old_colony.oc_cosmo_application.Data.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.sql.*;

/**
 * This class is for any utilities that use SQL
 */
@SuppressWarnings({"CallToPrintStackTrace", "SpellCheckingInspection"})
public class SQLUtils {
    /**
     * Makes a connection to the cosmo database
     * @return a connection of type {@code Connection} or null if no connection was made
     */
    public static Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/cosmo", "root", "password");
        } catch (SQLException e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Connection Error",
                    "Error Connecting To Cosmo Database",
                    "Database could not be connected to, please try again."
            );
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Converts a string color name to a usable hex color
     * @param name string color name (ex: red, pink, blue, etc.)
     * @return a color in hex format as a String
     */
    public static String getColor(String name) {
        try {
            Color color = Color.web(name.toUpperCase());
            int r = (int) (color.getRed() * 255),
                    g = (int) (color.getGreen() * 255),
                    b = (int) (color.getBlue() * 255),
                    a = (int) (color.getOpacity() * 255);
            return String.format("#%02X%02X%02X%02X", r, g, b, a); // hex formatting
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in getColor",
                    "Error Converting Color To Hex",
                    "There was an error converting: " + name + " to hex code, please try again."
            );
            e.printStackTrace();
            return "#808080"; // grey fallback color
        }
    }

    // region Start
    /**
     * Checks if the login information entered is valid
     * @param username login username
     * @param password login password
     * @param securityAnswer answer to security question
     * @return true if login information is correct, false otherwise
     */
    public static boolean logInCheck(String username, String password, String securityAnswer) {
        try (Connection connect = connectDB()) {
            if (connect == null) return false;

            String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND securityAnswer = ? limit 1;";

            PreparedStatement prepare = connect.prepareStatement(sql);

            prepare.setString(1, username);
            prepare.setString(2, password);
            prepare.setString(3, securityAnswer);

            ResultSet resultSet = prepare.executeQuery();
            if (resultSet.next())
                return resultSet.getString("password").equals(password)
                        && resultSet.getString("username").equals(username)
                        && resultSet.getString("securityAnswer").equals(securityAnswer);
        } catch (SQLException e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in logInCheck",
                    "Error Checking User Logging In",
                    "There was an error allowing the user to log in, please try again."
            );
            e.printStackTrace();
        }

        return false;
    }
    
    /**
     * Changes the specified username's password
     * @param username specified username used to find the user
     * @param newPassword new password to set
     */
    public static void changePassword(String username, String newPassword) {
        try (Connection connect = connectDB()) {
            if (connect == null) return;

            String sql = "UPDATE users SET password = ? WHERE username = ?";

            PreparedStatement prepare = connect.prepareStatement(sql);
            prepare.setString(1, newPassword);
            prepare.setString(2, username);

            prepare.executeUpdate();
        } catch (SQLException e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in changePassword",
                    "Error Changing Password Using Database",
                    "There was an error changing password, please try again."
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the security question based on the user's username
     * @param username username used to find a specific user
     * @return the security question for that specified user
     */
    public static String getSecurityQuestion(String username) {
        try (Connection connect = connectDB()) {
            if (connect == null) return null;

            String sql = "SELECT securityQuestion FROM users WHERE username = ? limit 1;";

            PreparedStatement prepare = connect.prepareStatement(sql);
            prepare.setString(1, username);
            ResultSet resultSet = prepare.executeQuery();

            if (resultSet.next())
                return resultSet.getString("securityQuestion");
        } catch (SQLException e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in getSecurityQuestion",
                    "Error Getting Security Question From Database",
                    "There was an error getting the security question from the database, please try again."
            );
            e.printStackTrace();
        }
        return null;
    }
    // endregion

    // region Dashboard
    /**
     * Gets all users from database
     * <p>Runs "{@code select * from users;}"</p>
     * @return a list of users as {@code ObservableList<User>}
     */
    public static ObservableList<User> getAllUsers() {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;

            String sql = "select * from users;";

            ObservableList<User> data = FXCollections.observableArrayList();
            PreparedStatement prepared = connection.prepareStatement(sql);
            ResultSet result = prepared.executeQuery();

            while (result.next())
                data.add(new User(
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("securityQuestion"),
                        result.getString("securityAnswer"),
                        result.getInt("userID"),
                        Utils.createStatus(result.getInt("status"))
                ));

            return data;
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in getAllUsers",
                    "Error Getting All Users From Database",
                    "There was an error getting all users from the database, please try again."
            );
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Adds a new appointment containing the specified information to the database
     * <p>Information is of type {@code Appointment} to avoid many parameters</p>
     * @param a appointment data to be created
     */
    public static void createAppointment(Appointment a) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            
            String sql = "INSERT INTO appointments (startHour, startMinute, duration, userID, custName, service, cost, appDate, color, note) VALUES (?,?,?,?,?,?,?,?,?,?);";

            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setInt(1, a.hour());
            prepared.setInt(2, a.minute());
            prepared.setInt(3, a.duration());
            prepared.setInt(4, a.student().userID());
            prepared.setString(5, a.customer());
            prepared.setString(6, a.service());
            prepared.setDouble(7, a.cost());
            prepared.setDate(8, a.date());
            prepared.setString(9, a.color());
            prepared.setString(10, a.note());

            prepared.executeUpdate();
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in createAppointment",
                    "Error Creating Appointment",
                    "There was an error creating the appointment, please try again."
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Updates a specific appointment from the database with the parameterized appointment information
     * <p>Information is of type {@code Appointment} to avoid many parameters</p>
     * @param newAppointment new appointment information, what the old appointment will be set to
     * @param oldAppointment old appointment information, used to find the appointment in the database
     */
    public static void updateAppointment(Appointment newAppointment, Appointment oldAppointment) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            
            String sql = "update appointments set startHour = ?, startMinute = ?, " +
                    "duration = ?, userID = ?, custName = ? , service = ?, cost = ?, " +
                    "appDate = ?, color = ?, note = ? where id like (" +
                    "select id where startHour = ? and startMinute = ? and duration = ? " +
                    "and userID = ? and custName = ? and service = ? and cost = ? and " +
                    "appDate = ? limit 1);";
            
            PreparedStatement prepared = connection.prepareStatement(sql);
            
            // new appointment details
            prepared.setInt(1, newAppointment.hour());
            prepared.setInt(2, newAppointment.minute());
            prepared.setInt(3, newAppointment.duration());
            prepared.setInt(4, newAppointment.student().userID());
            prepared.setString(5, newAppointment.customer());
            prepared.setString(6, newAppointment.service());
            prepared.setDouble(7, newAppointment.cost());
            prepared.setDate(8, newAppointment.date());
            prepared.setString(9, newAppointment.color());
            prepared.setString(10, newAppointment.note());
            
            // old appointment details
            prepared.setInt(11, oldAppointment.hour());
            prepared.setInt(12, oldAppointment.minute());
            prepared.setInt(13, oldAppointment.duration());
            prepared.setInt(14, oldAppointment.student().userID());
            prepared.setString(15, oldAppointment.customer());
            prepared.setString(16, oldAppointment.service());
            prepared.setDouble(17, oldAppointment.cost());
            prepared.setDate(18, oldAppointment.date());
            
            System.out.println(prepared);
            
            prepared.executeUpdate();
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in updateAppointment",
                    "Error Updating Appointment",
                    "There was an error updating the appointment, please try again."
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Deletes the appointment using the student username and the service to be completed
     * @param username student username
     * @param service appointment service
     */
    public static void deleteAppointment(String username, String service) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            
            String sql = "DELETE FROM appointments WHERE userID = ? AND service = ?";

            PreparedStatement prepared = connection.prepareStatement(sql);
            User user = getUser(username);
            if (user == null) throw new IllegalArgumentException("User Not Found");

            prepared.setInt(1, user.userID());
            prepared.setString(2, service);

            prepared.executeUpdate();

        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in createAppointment",
                    "Error Deleting Appointment",
                    "There was an error deleting the appointment, please try again."
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Gets all appointments from cosmo database
     * <p>Runs "{@code select * from appointments;}" if id is -1</p>
     * <p>Runs "{@code select * from appointments where userID = ?;}" if id is not -1</p>
     * @param id student id
     * @return all appointments as {@code ObservableList<Appointment>}
     */
    public static ObservableList<Appointment> getAllAppointments(int id) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;

            String sql = "select * from appointments";
            boolean filterByID = id != -1;

            if (filterByID)
                sql += " where userID = ?;";

            PreparedStatement prepared = connection.prepareStatement(sql);

            if (filterByID)
                prepared.setInt(1, id);

            ObservableList<Appointment> data = FXCollections.observableArrayList();
            ResultSet result = prepared.executeQuery();

            while (result.next())
                data.add(new Appointment(
                        result.getString("custName"),
                        getUser(result.getInt("userID")),
                        getColor(result.getString("color")),
                        result.getString("service"),
                        result.getDouble("cost"),
                        result.getDate("appDate"),
                        result.getInt("startHour"),
                        result.getInt("startMinute"),
                        result.getInt("duration"),
                        result.getString("note")
                ));

            return data;
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in getAllAppointments",
                    "Error Getting All Appointments From Database",
                    "There was an error getting all appointments from the database, please try again."
            );
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Creates a user with the specific information
     * <p>Information is of type {@code User} to avoid many parameters</p>
     * @param user new user data to be added to the database
     */
    public static void createUser(User user) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            
            String sql = "INSERT INTO users (username, password, securityQuestion, securityAnswer, status) VALUES (?,?,?,?,?)";

            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setString(1, user.username());
            prepared.setString(2, user.password());
            prepared.setString(3, user.securityQuestion());
            prepared.setString(4, user.securityAnswer());
            prepared.setInt(5, Utils.createStatus(user.status()));

            prepared.executeUpdate();

        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in createUser",
                    "Error Creating User",
                    "There was an error creating the user, please try again."
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Deletes user with unique specified username
     * <p>Runs "{@code DELETE FROM users WHERE username = ?;}"</p>
     * @param username unique username used to find student
     */
    public static void deleteUser(String username) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;

            String sql = "DELETE FROM users WHERE username = ?;";

            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setString(1, username);

            prepared.executeUpdate();

        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in deleteUser",
                    "Error Deleting User",
                    "There was an error deleting the user, please try again."
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Updates the user with the unique username with the specified information
     * <p>Information is of type {@code User} to avoid many parameters</p>
     * @param user new user data, username is used to find already existing user
     */
    public static void updateUser(User user) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;

            String sql = "update users set password = ?, securityQuestion = ?, " +
                    "securityAnswer = ?, status = ? where username = ?;";

            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setString(1, user.password());
            prepared.setString(2, user.securityQuestion());
            prepared.setString(3, user.securityAnswer());
            prepared.setInt(4, Utils.createStatus(user.status()));
            prepared.setString(5, user.username());

            prepared.executeUpdate();

        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in updateUser",
                    "Error Updating A User",
                    "There was an error updating a user, please try again."
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Gets a used based on unique username
     * <p>Runs {@code select * from users where username = ? limit 1;}</p>
     * @param username unique username
     * @return a user as type {@code User}
     */
    public static User getUser(String username) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;

            String sql = "select * from users where username = ? limit 1;";

            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setString(1, username);
            ResultSet result = prepared.executeQuery();

            if (result.next())
                return new User(
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("securityQuestion"),
                        result.getString("securityAnswer"),
                        result.getInt("userID"),
                        Utils.createStatus(result.getInt("status"))
                );
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in getUser",
                    "Error Getting User From Database",
                    "There was an error getting a user from the database, please try again."
            );
            e.printStackTrace();

        }
        return null;
    }
    
    /**
     * Gets all appointments happening during the current day for a specific user
     * @param id unique user id
     * @return a list of appointments as {@code ObservableList<Appointment>}
     */
    public static ObservableList<Appointment> getTodayAppointments(int id) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;

            String sql = "select * from appointments where date(appDate) = curdate() and userID = ?;";

            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setInt(1, id);

            ObservableList<Appointment> data = FXCollections.observableArrayList();
            ResultSet result = prepared.executeQuery();

            while (result.next())
                data.add(new Appointment(
                        result.getString("custName"),
                        getUser(result.getInt("userID")),
                        getColor(result.getString("color")),
                        result.getString("service"),
                        result.getDouble("cost"),
                        result.getDate("appDate"),
                        result.getInt("startHour"),
                        result.getInt("startMinute"),
                        result.getInt("duration"),
                        result.getString("note")
                ));

            return data;
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in getTodayAppointments",
                    "Error Getting Today's Appointments From Database",
                    "There was an error getting today's appointments from the database, please try again."
            );
            e.printStackTrace();
            return null;
        }
    }
    // endregion

    /**
     * Gets a user based on a unique id
     * <p>Runs "{@code select * from users where userID = ? limit 1;}"</p>
     * @param id unique user id
     * @return a user as type {@code User}
     */
    private static User getUser(int id) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;
            
            String sql = "select * from users where userID = ? limit 1;";
            
            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setInt(1, id);
            ResultSet result = prepared.executeQuery();
            
            if (result.next())
                return new User(
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("securityQuestion"),
                        result.getString("securityAnswer"),
                        result.getInt("userID"),
                        Utils.createStatus(result.getInt("status"))
                );
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error in getUser",
                    "Error Getting User From Database",
                    "There was an error getting a user from the database, please try again."
            );
            e.printStackTrace();
        }
        return null;
    }
}
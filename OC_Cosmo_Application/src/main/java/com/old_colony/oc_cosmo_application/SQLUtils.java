package com.old_colony.oc_cosmo_application;

import com.old_colony.oc_cosmo_application.Data.Appointment;
import com.old_colony.oc_cosmo_application.Data.Status;
import com.old_colony.oc_cosmo_application.Data.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.sql.*;
import java.time.LocalDate;

/**
 * This file is for any utilities that use SQL and SQL helper methods
 */
@SuppressWarnings("CallToPrintStackTrace")
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
                        createStatus(result.getInt("status"))
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
     * @param hour starting hour
     * @param minute starting minute
     * @param duration duration of appointment
     * @param studentID student ID of student completing the appointment
     * @param customer customer receiving the service
     * @param service the service to be completed
     * @param cost cost of the service
     * @param date date of appointment
     * @param color student generated color of appointment to be used in the neat-view calendar
     * @param note a custom note containing any information that might be useful to the appointment
     */
    public static void createAppointment(int hour, int minute,
                                         int duration, int studentID,
                                         String customer, String service,
                                         int cost, LocalDate date,
                                         Color color, String note) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            String sql = "INSERT INTO appointments VALUES (?,?,?,?,?,?,?,?,?,?);";

            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setInt(1, hour);
            prepared.setInt(2, minute);
            prepared.setInt(3, duration);
            prepared.setInt(4, studentID);
            prepared.setString(5, customer);
            prepared.setString(6, service);
            prepared.setInt(7, cost);
            prepared.setDate(8, Date.valueOf(date));
            prepared.setString(9, color.toString());
            prepared.setString(10, note);

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
     * Updates a specific appointment from the database with the parameterized information
     * @param hour starting hour
     * @param minute starting minute
     * @param duration duration of appointment
     * @param studentID student ID of student completing the appointment
     * @param customer customer receiving the service
     * @param service the service to be completed
     * @param cost cost of the service
     * @param date date of appointment
     * @param color student generated color of appointment to be used in the neat-view calendar
     * @param note a custom note containing any information that might be useful to the appointment
     */
    public static void updateAppointment(int hour, int minute,
                                         int duration, int studentID,
                                         String customer, String service,
                                         int cost, LocalDate date,
                                         Color color, String note) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            String sql = "update appointments set startHour = ?, startMinute = ?, " +
                    "duration = ?, userID = ?, custName = ?, service = ?, cost = ?, " +
                    "appDate = ?, color = ?, note = ? where SOMETHING HERE;";
            
            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setInt(1, hour);
            prepared.setInt(2, minute);
            prepared.setInt(3, duration);
            prepared.setInt(4, studentID);
            prepared.setString(5, customer);
            prepared.setString(6, service);
            prepared.setInt(7, cost);
            prepared.setDate(8, Date.valueOf(date));
            prepared.setString(9, color.toString());
            prepared.setString(10, note);
            
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
     * @param username unique username for new student
     * @param password student password
     * @param secQuestion student security question, used for login
     * @param secAnswer student security answer, used for login
     * @param isAdmin status of student, used to initialize and show extra tables and pages if student is an admin
     */
    public static void createUser(String username, String password,
                                  String secQuestion, String secAnswer,
                                  boolean isAdmin) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            String sql = "INSERT INTO users (username, password, securityQuestion, securityAnswer, status) VALUES (?,?,?,?,?)";

            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setString(1, username);
            prepared.setString(2, password);
            prepared.setString(3, secQuestion);
            prepared.setString(4, secAnswer);
            prepared.setInt(5, isAdmin ? 1 : 0);

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
     * @param username unique username, used to find user
     * @param password user password
     * @param secQuestion security question
     * @param secAnswer security answer
     * @param isAdmin admin status
     */
    public static void updateUser(String username, String password,
                                  String secQuestion, String secAnswer,
                                  boolean isAdmin) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;

            String sql = "update users set password = ?, securityQuestion = ?, " +
                    "securityAnswer = ?, status = ? where username = ?;";

            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setString(1, password);
            prepared.setString(2, secQuestion);
            prepared.setString(3, secAnswer);
            prepared.setInt(4, isAdmin ? 1 : 0);
            prepared.setString(5, username);

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
                        createStatus(result.getInt("status"))
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

    // region Helper Methods
    /**
     * Determines the user's status based on the SQL integer status
     * <table>
     *     <tr>
     *         <th>SQL Status Code</th>
     *         <th>Java Enum Status</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>STUDENT</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>ADMIN</td>
     *     </tr>
     *     <tr>
     *         <td>Other</td>
     *         <td>ERROR</td>
     *     </tr>
     * </table>
     * @param status SQL int status
     * @return Java status as enum type {@code Status}
     */
    private static Status createStatus(int status) {
        return switch (status) {
            case 0 -> Status.STUDENT;
            case 1 -> Status.ADMIN;
            default -> Status.ERROR;
        };
    }
    
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
                        createStatus(result.getInt("status"))
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
    // endregion
}
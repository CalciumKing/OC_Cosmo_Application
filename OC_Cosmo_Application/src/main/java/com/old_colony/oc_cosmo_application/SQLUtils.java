package com.old_colony.oc_cosmo_application;

import com.old_colony.oc_cosmo_application.DataClasses.Appointment;
import com.old_colony.oc_cosmo_application.DataClasses.Status;
import com.old_colony.oc_cosmo_application.DataClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;

public class SQLUtils {
    public static Connection connectDB(){
        try{
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/cosmo", "root", "password");
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    
    // region Start
    public static boolean logInCheck(String username, String password, String securityAnswer) {

        try (Connection connect = connectDB()) {
            if (connect == null) return false;

            String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND securityAnswer = ?";

            PreparedStatement prepare = connect.prepareStatement(sql);

            prepare.setString(1, username);
            prepare.setString(2, password);
            prepare.setString(3, securityAnswer);

            ResultSet resultSet = prepare.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("password").equals(password)
                        && resultSet.getString("username").equals(username)
                        && resultSet.getString("securityAnswer").equals(securityAnswer);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public static void changePassword(String username, String newPassword){
        try (Connection connect = connectDB()) {
            if (connect == null) return;

            String sql = "UPDATE users SET password = ? WHERE username = ?";

            PreparedStatement prepare = connect.prepareStatement(sql);
            prepare.setString(1, newPassword);
            prepare.setString(2, username);

            prepare.executeUpdate();
        }
        catch(SQLException e){e.printStackTrace();}
    }

    public static String getSecurityQuestion(String username){
        try (Connection connect = connectDB()) {
            if (connect == null) return null;

            String sql = "SELECT securityQuestion FROM users WHERE username = ?";

            PreparedStatement prepare = connect.prepareStatement(sql);

            prepare.setString(1, username);

            ResultSet resultSet = prepare.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("securityQuestion");
            }
        }
        catch(SQLException e){e.printStackTrace();}
        return null;
    }

    public static boolean checkTextBoxes(String thingToCheck, String currentBox){
        try (Connection connect = connectDB()) {
            if (connect == null) return false;

            String sql = "";

            if(currentBox.equals("username"))
                sql = "SELECT * FROM users WHERE username = ?";
            else if(currentBox.equals("password"))
                sql = "SELECT * FROM users WHERE password = ?";
            else if (currentBox.equals("securityAnswer")) {
                sql = "SELECT * FROM users WHERE securityAnswer = ?";
            }

            PreparedStatement prepare = connect.prepareStatement(sql);

            prepare.setString(1, thingToCheck);

            ResultSet resultSet = prepare.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("password").equals(thingToCheck)
                        || resultSet.getString("securityAnswer").equals(thingToCheck)
                        || resultSet.getString("username").equals(thingToCheck);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    // endregion
    
    // region Dashboard
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
                    "Error in getUser",
                    "Error Getting User From Database",
                    "There was an error getting a user from the database, please try again."
            );
            e.printStackTrace();
        }
        return null;
    }

    private static Status createStatus(int status) {
        if(status == 0) return Status.STUDENT;
        else if (status == 1) return Status.ADMIN;
        return null;
    }

    
    public static ObservableList<Appointment> getAllAppointments(int id) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;
            String sql;

            PreparedStatement prepared;

            if(id == -1){
                sql = "select * from appointments";
                prepared = connection.prepareStatement(sql);
            }else{
                sql = "select * from appointments where userID = ?;";
                prepared = connection.prepareStatement(sql);
                prepared.setInt(1, id);
            }

            ObservableList<Appointment> data = FXCollections.observableArrayList();
            ResultSet result = prepared.executeQuery();
            
            while (result.next())
                data.add(new Appointment(
                        result.getString("custName"),
                        getUser(result.getInt("userID")),
                        Utils.getColor(result.getString("color")),
                        result.getString("service"),
                        result.getDouble("cost"),
                        result.getDate("appDate"),
                        result.getInt("startHour"),
                        result.getInt("startMinute"),
                        result.getInt("duration")
                ));
            
            return data;
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

    public static ObservableList<Appointment> getTodaysAppointments(int id) {
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
                        Utils.getColor(result.getString("color")),
                        result.getString("service"),
                        result.getDouble("cost"),
                        result.getDate("appDate"),
                        result.getInt("startHour"),
                        result.getInt("startMinute"),
                        result.getInt("duration")
                ));

            return data;
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
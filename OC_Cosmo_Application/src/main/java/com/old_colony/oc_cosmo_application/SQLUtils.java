package com.old_colony.oc_cosmo_application;

import javafx.scene.control.Alert;

import java.sql.*;

public class SQLUtils {
    public static Connection connectDB(){
        try{
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/Cosmo", "root", "password");
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void showAlert(Alert.AlertType type, String title, String content){
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean logInCheck(String username, String password, String securityAnswer) {
        Connection connect = connectDB();

        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND securityAnswer = ?";

        try{
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
        catch(SQLException e){e.printStackTrace();}

        return false;
    }

    public static String getSecurityQuestion(String username){
        Connection connect = connectDB();

        String sql = "SELECT securityQuestion FROM users WHERE username = ?";
        try{
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

}
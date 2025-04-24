package com.old_colony.oc_cosmo_application.DataClasses;

/**
 * class doesn't need setters
 * <p>current user's values are never updated</p>
 * <p>other users are only updated in database</p>
 * */

public class User {
    private final String username, securityQuestion;
    private final transient String password, securityAnswer;
    private final int userID;
    private final Status status;

    public User(String username, String password,
                String securityQuestion, String securityAnswer,
                int userID, Status status) {
        this.username = username;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.userID = userID;
        this.status = status;
    }

    public User(String username, String password,
                String securityAnswer) { // maybe delete this and convert to record
        this.username = username;
        this.password = password;
        securityQuestion = null;
        this.securityAnswer = securityAnswer;
        this.userID = -1;
        status = null;
    }

    // region Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public int getUserID() {
        return userID;
    }

    public Status getStatus() {
        return status;
    }
    // endregion
}
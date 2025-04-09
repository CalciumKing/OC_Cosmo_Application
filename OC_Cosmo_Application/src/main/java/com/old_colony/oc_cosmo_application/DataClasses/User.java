package com.old_colony.oc_cosmo_application.DataClasses;

public class User {
    private String username, securityAnswer;
    private transient String password, securityQuestion;
    private final int userID;
    private Status status;
    
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
    
    // region Getters/Setters
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getSecurityQuestion() {
        return securityQuestion;
    }
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }
    public String getSecurityAnswer() {
        return securityAnswer;
    }
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
    public int getUserID() {
        return userID;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    // endregion
}
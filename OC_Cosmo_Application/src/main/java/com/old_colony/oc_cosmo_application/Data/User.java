package com.old_colony.oc_cosmo_application.Data;

/**
 * Record to hold and access user data
 * <p>User class doesn't need setters because user values are never updated locally, only in the database.</p>
 * <p>Currently logged in user can never and should never be modified</p>
 *
 * @param username         user's login name
 * @param password         user's plain text password
 * @param securityQuestion security question used to log in and account recovery
 * @param securityAnswer   answer to security question
 * @param userID           unique identifier for the user in the database
 * @param status           account status (ex: STUDENT, ADMIN, ERROR)
 */

public record User(
        String username,
        String password,
        String securityQuestion,
        String securityAnswer,
        int userID,
        Status status) {
}
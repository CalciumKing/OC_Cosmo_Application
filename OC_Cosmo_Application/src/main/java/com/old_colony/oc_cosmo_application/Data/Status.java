package com.old_colony.oc_cosmo_application.Data;

/**
 * STUDENT is sql status 0
 * <p>ADMIN is sql status 1</p>
 * <p>ERROR is given to anything with a status number that wasn't listed</p>
 * <p>Add more values here if this project expands in the future.</p>
 */

public enum Status {
    ERROR,
    STUDENT,
    ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
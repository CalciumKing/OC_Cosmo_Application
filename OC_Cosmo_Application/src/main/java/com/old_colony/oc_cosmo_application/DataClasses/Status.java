package com.old_colony.oc_cosmo_application.DataClasses;

public enum Status {
    ERROR,
    STUDENT,
    ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
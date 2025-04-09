package com.old_colony.oc_cosmo_application.DataClasses;
public enum Status {
    ADMIN,
    STUDENT;
    
    public boolean isAdmin() {
        return this == ADMIN;
    }
}
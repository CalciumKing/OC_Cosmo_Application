package com.old_colony.oc_cosmo_application.DataClasses;

import java.util.Date;

public class Appointment {
    private String customer, student, service;
    private double cost;
    private Date date;
    private int hour, minute, duration;
    
    public Appointment(String customer, String student,
                       String service, double cost, Date date,
                       int hour, int minute, int duration) {
        this.customer = customer;
        this.student = student;
        this.service = service;
        this.cost = cost;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
    }
    
    // region Getters/Setters
    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public String getStudent() {
        return student;
    }
    public void setStudent(String student) {
        this.student = student;
    }
    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public int getMinute() {
        return minute;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    // endregion
}
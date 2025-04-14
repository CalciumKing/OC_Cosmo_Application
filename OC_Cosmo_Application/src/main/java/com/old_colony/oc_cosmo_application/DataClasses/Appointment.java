package com.old_colony.oc_cosmo_application.DataClasses;

import java.util.Date;

public class Appointment {
    private User student;
    private Date date;
    private String customer, service, color, note;
    private double cost;
    private int hour, minute, duration;
    
    public Appointment(String customer, User student, String color,
                       String service, double cost, Date date,
                       int hour, int minute, int duration, String note) {
        this.customer = customer;
        this.student = student;
        this.color = color;
        this.service = service;
        this.cost = cost;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
        this.note = note;
    }
    
    // region Getters/Setters
    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public User getStudent() {
        return student;
    }
    public void setStudent(User student) {
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
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    // endregion
}
package com.old_colony.oc_cosmo_application.DataClasses;

import javafx.scene.paint.Color;

import java.util.Date;

public class Appointment {
    private String customer, service;
    private double cost;
    private Date date;
    private User student;
    private Color color;
    private int hour, minute, duration;
    
    public Appointment(String customer, User student, Color color,
                       String service, double cost, Date date,
                       int hour, int minute, int duration) {
        this.customer = customer;
        this.student = student;
        this.color = color;
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
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    // endregion
}
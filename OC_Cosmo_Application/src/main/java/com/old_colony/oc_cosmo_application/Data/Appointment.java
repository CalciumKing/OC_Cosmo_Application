package com.old_colony.oc_cosmo_application.Data;

import java.sql.Date;

/**
 * Record to hold and access appointment data
 * <p>Appointment values are never updated locally, only in the database.</p>
 * <p>See {@link User} for more information on 'student' parameter</p>
 *
 * @param customer The full name of the customer who booked the appointment
 * @param student  The {@code User} object representing the student performing the service
 * @param color    A color code or name used for visually identifying the appointment on the neat-view calendar
 * @param service  The type of service being performed
 * @param cost     The cost of the appointment in dollars. This reflects the price of the service provided.
 * @param date     The calendar date of the appointment. This does not include time, only the day. Formatted as YYYY-MM-DD
 * @param hour     The starting hour of the appointment, 24-hour format
 * @param minute   The starting minute of the appointment, usually 0, 15, 30, or 45.
 * @param duration The total length of the appointment in minutes
 * @param note     Optional additional notes about the appointment, such as special requests or service details.
 */

public record Appointment(
        String customer,
        User student,
        String color,
        String service,
        double cost,
        Date date,
        int hour,
        int minute,
        int duration,
        String note) {
}
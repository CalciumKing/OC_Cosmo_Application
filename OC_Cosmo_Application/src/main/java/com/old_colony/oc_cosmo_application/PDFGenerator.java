package com.old_colony.oc_cosmo_application;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.old_colony.oc_cosmo_application.Data.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class PDFGenerator {
    public static void main(String[] args) {
        ObservableList<Appointment> appointmentObservableList = SQLUtils.getAllAppointments(-1);

        //dailyAppointments(appointmentObservableList);
        //allAppointments(appointmentObservableList);
        weeklyAppointments();
    }

    public static void weeklyAppointments() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String imgSource = "src/main/resources/images/OCLogo.png";
        try {
            PdfWriter writer = new PdfWriter("weeklyAppointmentsPDF.pdf");
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            ImageData data = ImageDataFactory.create(imgSource);
            Image icon = new Image(data);

            Paragraph title = new Paragraph("Daily Appointments")
                    .setFontSize(25)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE);

            Table headerTable = new Table(2);
            Table secondTable = new Table(6);
            headerTable.setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().add(icon).setBorder(Border.NO_BORDER));
            headerTable.addCell(
                    new Cell().add(title).setTextAlignment(TextAlignment.CENTER)
                            .setBorder(Border.NO_BORDER)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("Old Colony RVTHS"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("(508) 763-8011"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("oldcolony@oldcolony.us"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("Cosmetology Shop"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("5/8/2025"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
            );

            secondTable.setWidth(UnitValue.createPercentValue(100));
            secondTable.addCell(
                    new Cell().add(new Paragraph("Student"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Service"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Cost"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Date"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Duration"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Customer"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );

            if(SQLUtils.selectAppointmentsByDate(startOfWeek, endOfWeek) != null) {
                for (Appointment appointment : SQLUtils.selectAppointmentsByDate(startOfWeek, endOfWeek)) {
                    System.out.println(appointment.customer());
                    createTable(secondTable, appointment);
                }
            }


            document.add(headerTable);
            document.add(new Paragraph("\n"));
            document.add(secondTable);
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTable(Table secondTable, Appointment appointment) {
        String username = "N/A";
        if (appointment != null && appointment.student() != null && appointment.student().username() != null) {
            username = appointment.student().username();
        }

        secondTable.addCell(
                new Cell().add(new Paragraph(username))
                        .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
        );

        secondTable.addCell(
                new Cell().add(new Paragraph(String.valueOf(appointment.service())))
                        .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
        );

        secondTable.addCell(
                new Cell().add(new Paragraph(String.valueOf(appointment.cost())))
                        .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
        );

        secondTable.addCell(
                new Cell().add(new Paragraph(String.valueOf(appointment.date())))
                        .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
        );

        secondTable.addCell(
                new Cell().add(new Paragraph(String.valueOf(appointment.duration())))
                        .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
        );

        secondTable.addCell(
                new Cell().add(new Paragraph(String.valueOf(appointment.customer())))
                        .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
        );
    }

    public static void dailyAppointments(ObservableList<Appointment> appointmentObservableList) {
        ObservableList<Appointment> todayAppointments = FXCollections.observableArrayList();
        for (int i = 0; i < appointmentObservableList.size(); i++) {
            int today = LocalDate.now().getDayOfMonth();
            int month = LocalDate.now().getMonthValue();
            int year = LocalDate.now().getYear();

            if(appointmentObservableList.get(i).date().getDate() == today
                    && appointmentObservableList.get(i).date().getMonth() == month - 1
                    && appointmentObservableList.get(i).date().getYear() == year - 1900) {
                todayAppointments.add(appointmentObservableList.get(i));
            }
        }

        String imgSource = "src/main/resources/images/OCLogo.png";
        try {
            PdfWriter writer = new PdfWriter("weeklyAppointmentsPDF.pdf");
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            ImageData data = ImageDataFactory.create(imgSource);
            Image icon = new Image(data);

            Paragraph title = new Paragraph("Daily Appointments")
                    .setFontSize(25)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE);

            Table headerTable = new Table(2);
            Table secondTable = new Table(6);
            headerTable.setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().add(icon).setBorder(Border.NO_BORDER));
            headerTable.addCell(
                    new Cell().add(title).setTextAlignment(TextAlignment.CENTER)
                            .setBorder(Border.NO_BORDER)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("Old Colony RVTHS"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("(508) 763-8011"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("oldcolony@oldcolony.us"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("Cosmetology Shop"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("5/8/2025"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
            );

            secondTable.setWidth(UnitValue.createPercentValue(100));
            secondTable.addCell(
                    new Cell().add(new Paragraph("Student"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Service"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Cost"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Date"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Duration"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Customer"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );

            for (Appointment appointment : todayAppointments) {
                createTable(secondTable, appointment);
            }


            document.add(headerTable);
            document.add(new Paragraph("\n"));
            document.add(secondTable);
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void allAppointments(ObservableList<Appointment> appointmentObservableList) {
        String imgSource = "src/main/resources/images/OCLogo.png";
        try {
            PdfWriter writer = new PdfWriter("weeklyAppointmentsPDF.pdf");
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            ImageData data = ImageDataFactory.create(imgSource);
            Image icon = new Image(data);

            Paragraph title = new Paragraph("Daily Appointments")
                    .setFontSize(25)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE);

            Table headerTable = new Table(2);
            Table secondTable = new Table(6);
            headerTable.setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().add(icon).setBorder(Border.NO_BORDER));
            headerTable.addCell(
                    new Cell().add(title).setTextAlignment(TextAlignment.CENTER)
                            .setBorder(Border.NO_BORDER)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("Old Colony RVTHS"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("(508) 763-8011"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("oldcolony@oldcolony.us"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("Cosmetology Shop"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("5/8/2025"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .add(new Paragraph("\n"))
                            .setTextAlignment(TextAlignment.RIGHT)
            );

            secondTable.setWidth(UnitValue.createPercentValue(100));
            secondTable.addCell(
                    new Cell().add(new Paragraph("Student"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Service"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Cost"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Date"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Duration"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );
            secondTable.addCell(
                    new Cell().add(new Paragraph("Customer"))
                            .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
            );

            if(appointmentObservableList != null) {
                for (Appointment appointment : appointmentObservableList) {

                    createTable(secondTable, appointment);
                }
            }


            document.add(headerTable);
            document.add(new Paragraph("\n"));
            document.add(secondTable);
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}

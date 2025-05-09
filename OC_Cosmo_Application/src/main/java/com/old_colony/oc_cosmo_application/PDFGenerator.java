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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@SuppressWarnings({"CallToPrintStackTrace", "SpellCheckingInspection"})
public class PDFGenerator {
    private static final String ocLogo = "src/main/resources/images/OCLogo.png";
    
    public static void main(String[] args) {
        ObservableList<Appointment> appointmentObservableList = SQLUtils.getAllAppointments(-1);
        if(appointmentObservableList == null) return;
        
        dailyAppointments(appointmentObservableList);
        allAppointments(appointmentObservableList);
        weeklyAppointments();
    }

    public static void weeklyAppointments() {
        LocalDate today = LocalDate.now(),
                startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        try {
            PdfWriter writer = new PdfWriter("weeklyAppointmentsPDF.pdf");
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            ImageData data = ImageDataFactory.create(ocLogo);
            Image icon = new Image(data);

            Paragraph title = new Paragraph("Daily Appointments")
                    .setFontSize(25)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE);

            Table headerTable = new Table(2),
                    secondTable = new Table(6);
            
            headerTable.setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().add(icon).setBorder(Border.NO_BORDER));
            createTopCell(headerTable, title);
            createDataTable(secondTable);
            
            if(SQLUtils.selectAppointmentsByDate(startOfWeek, endOfWeek) != null) {
                ObservableList<Appointment> appointments = SQLUtils.selectAppointmentsByDate(startOfWeek, endOfWeek);
                if(appointments == null) return;
                
                for (Appointment appointment : appointments)
                    createTable(secondTable, appointment);
            }

            document.add(headerTable);
            document.add(new Paragraph("\n"));
            document.add(secondTable);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void createTable(Table secondTable, Appointment appointment) {
        if (appointment == null) return;
        
        String username = appointment.student().username();

        secondTable.addCell(
                new Cell().add(new Paragraph(username))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );

        secondTable.addCell(
                new Cell().add(new Paragraph(String.valueOf(appointment.service())))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );

        secondTable.addCell(
                new Cell().add(new Paragraph(String.valueOf(appointment.cost())))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );

        secondTable.addCell(
                new Cell().add(new Paragraph(String.valueOf(appointment.date())))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );

        secondTable.addCell(
                new Cell().add(new Paragraph(String.valueOf(appointment.duration())))
                        .setTextAlignment(TextAlignment.CENTER
                        ).setBorder(Border.NO_BORDER)
        );

        secondTable.addCell(
                new Cell().add(new Paragraph(String.valueOf(appointment.customer())))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );
    }

    public static void dailyAppointments(ObservableList<Appointment> appointmentObservableList) {
        ObservableList<Appointment> todayAppointments = FXCollections.observableArrayList();
        for (Appointment value : appointmentObservableList) {
            LocalDate now = LocalDate.now();
            
            if (value.date().getDate() == now.getDayOfMonth() &&
                    value.date().getMonth() == now.getMonthValue() - 1 &&
                    value.date().getYear() == now.getYear() - 1900)
                todayAppointments.add(value);
        }
        
        try {
            PdfWriter writer = new PdfWriter("weeklyAppointmentsPDF.pdf");
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            ImageData data = ImageDataFactory.create(ocLogo);
            Image icon = new Image(data);

            Paragraph title = new Paragraph("Daily Appointments")
                    .setFontSize(25)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE);

            Table headerTable = new Table(2),
                    secondTable = new Table(6);
            
            headerTable.setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().add(icon).setBorder(Border.NO_BORDER));
            createTopCell(headerTable, title);
            createDataTable(secondTable);
            
            for (Appointment appointment : todayAppointments)
                createTable(secondTable, appointment);

            document.add(headerTable);
            document.add(new Paragraph("\n"));
            document.add(secondTable);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void allAppointments(ObservableList<Appointment> appointmentObservableList) {
        try {
            PdfWriter writer = new PdfWriter("weeklyAppointmentsPDF.pdf");
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            ImageData data = ImageDataFactory.create(ocLogo);
            Image icon = new Image(data);

            Paragraph title = new Paragraph("Daily Appointments")
                    .setFontSize(25)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE);

            Table headerTable = new Table(2),
                    secondTable = new Table(6);
            
            headerTable.setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().add(icon).setBorder(Border.NO_BORDER));
            createTopCell(headerTable, title);
            createDataTable(secondTable);
            
            for (Appointment appointment : appointmentObservableList)
                createTable(secondTable, appointment);

            document.add(headerTable);
            document.add(new Paragraph("\n"));
            document.add(secondTable);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void createTopCell(Table headerTable, Paragraph title) {
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
    }
    
    private static void createDataTable(Table secondTable) {
        secondTable.setWidth(UnitValue.createPercentValue(100));
        secondTable.addCell(
                new Cell().add(new Paragraph("Student"))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );
        secondTable.addCell(
                new Cell().add(new Paragraph("Service"))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );
        secondTable.addCell(
                new Cell().add(new Paragraph("Cost"))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );
        secondTable.addCell(
                new Cell().add(new Paragraph("Date"))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );
        secondTable.addCell(
                new Cell().add(new Paragraph("Duration"))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );
        secondTable.addCell(
                new Cell().add(new Paragraph("Customer"))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
        );
    }
}
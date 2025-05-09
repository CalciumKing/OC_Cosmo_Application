package com.old_colony.oc_cosmo_application.Misc;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
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

/**
 * This file handles the generation of all pdfs
 * <p>The contents of the pdfs varies based on the input of {@link #createPDF(String) createPDF()}</p>
 */
@SuppressWarnings({"CallToPrintStackTrace", "SpellCheckingInspection"})
public class PDFGenerator {
    /**
     * Handles which method creates a pdf based on the input
     * @param type the type of pdf to be created, either "daily", "weekly", or "all"
     * @see #dailyAppointments(Document, ImageData) dailyAppointments()
     * @see #weeklyAppointments(Document, ImageData) weeklyAppointments()
     * @see #allAppointments(Document, ImageData) allAppointments()
     */
    public static void createPDF(String type) {
        try {
            ImageData data = ImageDataFactory.create("src/main/resources/images/OCLogo.png");
            Document document;

            switch (type) {
                case "daily":
                    document = new Document(new PdfDocument(new PdfWriter("src/main/resources/PDFs/dailyAppointments.pdf")));
                    dailyAppointments(document, data);
                    break;
                case "weekly":
                    document = new Document(new PdfDocument(new PdfWriter("src/main/resources/PDFs/weeklyAppointments.pdf")));
                    weeklyAppointments(document, data);
                    break;
                case "all":
                    document = new Document(new PdfDocument(new PdfWriter("src/main/resources/PDFs/allAppointments.pdf")));
                    allAppointments(document, data);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // region Main Methods
    /**
     * Creates a pdf to hold all appointments that are happening in the current week
     * @param document document to add tables data to
     * @param data image data to be used in {@link #createHeader(String, ImageData) createHeader()}
     */
    private static void weeklyAppointments(Document document, ImageData data) {
        LocalDate today = LocalDate.now(),
                now = today.minusMonths(1).minusDays(5),
                startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        ObservableList<Appointment> appointments = SQLUtils.selectAppointmentsByDate(startOfWeek, endOfWeek);
        if (appointments == null) return;

        Table secondTable = createDataTable();

        for (Appointment a : appointments) {
            if (a.student() == null) continue; // delete later, shouldn't exist after table drop

            for (String s : new String[]{
                    a.student().username(),
                    a.service(),
                    String.valueOf(a.cost()),
                    String.valueOf(a.date()),
                    String.valueOf(a.duration()),
                    a.customer()
            })
                secondTable.addCell(createTableCell(s));
        }

        document.add(createHeader("Weekly Appointments", data));
        document.add(secondTable);
        document.close();
    }

    /**
     * Creates a pdf to hold all appointments that are happening in the current day
     * @param document document to add tables data to
     * @param data image data to be used in {@link #createHeader(String, ImageData) createHeader()}
     */
    private static void dailyAppointments(Document document, ImageData data) {
        ObservableList<Appointment> allAppointments = SQLUtils.getAllAppointments(-1),
                todayAppointments = FXCollections.observableArrayList();
        if(allAppointments == null) return;

        for (Appointment value : allAppointments)
            if (LocalDate.now().equals(value.date().toLocalDate()))
                todayAppointments.add(value);

        Table secondTable = createDataTable();

        for (Appointment a : todayAppointments) {
            if (a.student() == null) continue; // delete later, shouldn't exist after table drop

            for (String s : new String[]{
                    a.student().username(),
                    a.service(),
                    String.valueOf(a.cost()),
                    String.valueOf(a.date()),
                    String.valueOf(a.duration()),
                    a.customer()
            })
                secondTable.addCell(createTableCell(s));
        }

        document.add(createHeader("Daily Appointments", data));
        document.add(secondTable);
        document.close();
    }

    /**
     * Creates a pdf to hold all appointment data
     * @param document document to add tables data to
     * @param data image data to be used in {@link #createHeader(String, ImageData) createHeader()}
     */
    private static void allAppointments(Document document, ImageData data) {
        ObservableList<Appointment> allAppointments = SQLUtils.getAllAppointments(-1);
        if(allAppointments == null) return;

        Table secondTable = createDataTable();

        for (Appointment a : allAppointments) {
            if (a.student() == null) continue; // delete later, shouldn't exist after table drop

            for (String s : new String[]{
                    a.student().username(),
                    a.service(),
                    String.valueOf(a.cost()),
                    String.valueOf(a.date()),
                    String.valueOf(a.duration()),
                    a.customer()
            })
                secondTable.addCell(createTableCell(s));
        }

        document.add(createHeader("All Appointments", data));
        document.add(secondTable);
        document.close();
    }
    // endregion

    // region Helper Methods
    /**
     * Creates the header for the pdf, all pdfs have the same header
     * @param title text to be displayed at the top of the pdf, either "daily", "weekly", or "all"
     * @param data image data to be turned into an image
     * @return table to be put at the top of every pdf
     */
    private static Table createHeader(String title, ImageData data) {
        return new Table(2)
                .setWidth(UnitValue.createPercentValue(100))
                .addCell(
                        new Cell().add(
                                new Image(data)
                        ).setBorder(Border.NO_BORDER))
                .addCell(
                        new Cell()
                                .add(
                                        new Paragraph(title + "\n\n")
                                                .setFontSize(25)
                                                .setBold()
                                )
                                .add(new Paragraph("Old Colony RVTHS\n\n"))
                                .add (new Paragraph("(508) 763-8011\n\n"))
                                .add(new Paragraph("oldcolony@oldcolony.us\n\n"))
                                .add(new Paragraph("Cosmetology Shop\n\n"))
                                .add(new Paragraph(LocalDate.now() + "\n\n"))
                                .setBorder(Border.NO_BORDER)
                                .setTextAlignment(TextAlignment.RIGHT)
                );
    }

    /**
     * Creates the first row of the data table on the pdf
     * @return Table to be added to by other methods and put on pdf
     */
    private static Table createDataTable() {
        Table table = new Table(6).setWidth(UnitValue.createPercentValue(100));
        for (String s : new String[]{"Student", "Service", "Cost", "Date", "Duration", "Customer"})
            table.addCell(createTableCell(s));
        return table;
    }

    /**
     * Creates a basic cell with a paragraph that displays specified text
     * <p>Text is center aligned.</p>
     * @param text specified text to be displayed
     * @return Cell to be put in table
     */
    private static Cell createTableCell(String text) {
        return new Cell().add(
                new Paragraph(text)
        ).setTextAlignment(TextAlignment.CENTER);
    }
    // endregion
}
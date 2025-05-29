package com.old_colony.oc_cosmo_application.Misc;

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
import javafx.scene.control.Alert;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * This file handles the generation of all pdfs
 * <p>The contents of the pdfs varies based on the input of {@link #createPDF(String, String) createPDF()}</p>
 */
@SuppressWarnings({ "CallToPrintStackTrace", "SpellCheckingInspection" })
public class PDFGenerator {
    // region Main Methods
    /**
     * Creates a PDF and manages which method adds data to it based on the type parameter
     *
     * @param type the type of pdf to be created (daily, weekly, all)
     * @param path file path location to generate the pdf
     * @see #dailyAppointments(Document) dailyAppointments()
     * @see #weeklyAppointments(Document) weeklyAppointments()
     * @see #allAppointments(Document) allAppointments()
     */
    public static void createPDF(String type, String path) {
        try {
            HashMap<String, Consumer<Document>> handlers = new HashMap<>();
            handlers.put("daily", PDFGenerator::dailyAppointments);
            handlers.put("weekly", PDFGenerator::weeklyAppointments);
            handlers.put("all", PDFGenerator::allAppointments);

            HashMap<String, String> fileNames = new HashMap<>();
            fileNames.put("daily", "dailyAppointments.pdf");
            fileNames.put("weekly", "weeklyAppointments.pdf");
            fileNames.put("all", "allAppointments.pdf");

            if (!handlers.containsKey(type))
                throw new IllegalArgumentException("Unknown PDF Type: " + type);

            if (path == null) path = "src/main/resources/PDFs";

            Document doc = new Document(new PdfDocument(new PdfWriter(path + "/" + fileNames.get(type))));

            handlers.get(type).accept(doc);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "PDF Error",
                    "Error in createPDF",
                    "An error occurred when creating a pdf, please try again."
            );
        }
    }

    /**
     * Adds appointments that are happening in the current week to the pdf
     *
     * @param document document to add tables data to
     * @see #createFileData(ObservableList, Document, String) createFileData()
     */
    private static void weeklyAppointments(Document document) {
        LocalDate today = LocalDate.now(),
                startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        ObservableList<Appointment> appointments = SQLUtils.selectAppointmentsByDate(startOfWeek, endOfWeek);
        if (appointments == null) return;

        createFileData(appointments, document, "Weekly Appointments");
    }

    /**
     * Adds appointments that are happening in the current day to the pdf
     *
     * @param document document to add tables data to
     * @see #createFileData(ObservableList, Document, String) createFileData()
     */
    private static void dailyAppointments(Document document) {
        ObservableList<Appointment> allAppointments = SQLUtils.getAllAppointments(-1),
                todayAppointments = FXCollections.observableArrayList();
        if (allAppointments == null) return;

        for (Appointment value : allAppointments)
            if (LocalDate.now().equals(value.date().toLocalDate()))
                todayAppointments.add(value);

        createFileData(todayAppointments, document, "Daily Appointments");
    }

    /**
     * Adds all appointments that are happening to the pdf
     *
     * @param document document to add tables data to
     * @see #createFileData(ObservableList, Document, String) createFileData()
     */
    private static void allAppointments(Document document) {
        ObservableList<Appointment> allAppointments = SQLUtils.getAllAppointments(-1);
        if (allAppointments == null) return;

        createFileData(allAppointments, document, "All Appointments");
    }
    // endregion

    // region Helper Methods

    /**
     * Creates the header for the pdf, all pdfs have the same header
     *
     * @param title text to be displayed at the top of the pdf, either "daily", "weekly", or "all"
     * @return table to be put at the top of every pdf
     */
    private static Table createHeader(String title) {
        try {
            return new Table(2)
                    .setWidth(UnitValue.createPercentValue(100))
                    .addCell(
                            new Cell().add(
                                    new Image(ImageDataFactory.create("src/main/resources/images/OCLogo.png"))
                            ).setBorder(Border.NO_BORDER))
                    .addCell(
                            new Cell()
                                    .add(
                                            new Paragraph(title + "\n\n")
                                                    .setFontSize(25)
                                                    .setBold()
                                    )
                                    .add(new Paragraph("Old Colony RVTHS\n\n"))
                                    .add(new Paragraph("(508) 763-8011\n\n"))
                                    .add(new Paragraph("oldcolony@oldcolony.us\n\n"))
                                    .add(new Paragraph("Cosmetology Shop\n\n"))
                                    .add(new Paragraph(LocalDate.now() + "\n\n"))
                                    .setBorder(Border.NO_BORDER)
                                    .setTextAlignment(TextAlignment.RIGHT)
                    );
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a basic cell with a paragraph that displays specified text
     * <p>Text is center aligned.</p>
     *
     * @param text specified text to be displayed
     * @return Cell to be put in table
     */
    private static Cell createTableCell(String text) {
        return new Cell().add(
                new Paragraph(text)
        ).setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * Puts all the data onto the pdf document
     * <p>Uses other methods to do repedative PDF data creation</p>
     *
     * @param appointments data to be put on the PDF
     * @param doc          PDF document that data will be put on
     * @param title        title or type of data on PDF (daily, weekly, all)
     * @see #createHeader(String) createHeader()
     * @see #createTableCell(String) createTableCell()
     */
    private static void createFileData(ObservableList<Appointment> appointments, Document doc, String title) {
        Table table = new Table(6).setWidth(UnitValue.createPercentValue(100));
        for (String s : new String[] { "Student", "Service", "Cost", "Date", "Duration", "Customer" })
            table.addCell(createTableCell(s));

        for (Appointment a : appointments) {
            for (String s : new String[] {
                    a.student().username(),
                    a.service(),
                    String.valueOf(a.cost()),
                    String.valueOf(a.date()),
                    String.valueOf(a.duration()),
                    a.customer()
            })
                table.addCell(createTableCell(s));
        }

        doc.add(createHeader(title));
        doc.add(table);
        doc.close();
    }
    // endregion
}
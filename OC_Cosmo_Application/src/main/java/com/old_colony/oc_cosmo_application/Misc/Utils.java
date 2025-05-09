package com.old_colony.oc_cosmo_application.Misc;

import com.old_colony.oc_cosmo_application.Data.Status;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;

/**
 * This file is for any utility methods that don't use SQL and aren't local SQL helper methods
 */
public class Utils {
    // region Alert Methods
    /**
     * Creates a regular alert with the contents of the parameters
     * @param type alert type of type {@code Alert.AlertType} (Ex: information, confirmation, error, none, warning, etc.)
     * @param title window title text
     * @param headerText main error header text
     * @param contentText error description or future instructions
     */
    public static void normalAlert(Alert.AlertType type, String title,
                                   String headerText, String contentText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        alert.showAndWait();
    }

    /**
     * Creates a confirmation alert with the contents of the parameters
     * @param type alert type of type {@code Alert.AlertType} (Ex: information, confirmation, error, none, warning, etc.)
     * @param title window title text
     * @param headerText main error header text
     * @param contentText error description or future instructions
     * @param aText the confirm button text, can be used to confirm the user's choice of the alert
     * @param bText the cancel button text, can be used to confirm the users choice of the alert
     * @return returns {@code Optional<ButtonType>} to be used with {@code isPresent()} and {@code getText()} to confirm the user's choice of the alert
     */
    public static Optional<ButtonType> confirmAlert(Alert.AlertType type, String title,
                                                    String headerText, String contentText,
                                                    String aText, String bText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        ButtonType yes = new ButtonType(aText, ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType(bText, ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no);
        return alert.showAndWait();
    }

    /**
     * Does the actual work of creating a regular alert.
     * <p>Used by {@code normalAlert()} and {@code confirmAlert()}</p>
     * @param type alert type of type {@code Alert.AlertType} (Ex: information, confirmation, error, none, warning, etc.)
     * @param title window title text
     * @param headerText main error header text
     * @param contentText error description or future instructions
     * @return returns {@code Alert} to be used elsewhere
     */
    private static Alert createAlert(Alert.AlertType type, String title,
                                     String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.initOwner(getCurrentStage());
        return alert;
    }
    // endregion Alert Methods

    /**
     * Gets the current stage based on the available windows.
     * <p>Used only if no stage data is given and the stage is needed.</p>
     * @return the currently open application's stage
     */
    private static Stage getCurrentStage() {
        for (Window window : Window.getWindows())
            if (window instanceof Stage)
                return (Stage) window;
        return null;
    }
    
    /**
     * Determines the user's status based on the SQL integer status
     * <table>
     *     <tr>
     *         <th>SQL Status Code</th>
     *         <th>Java Enum Status</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>STUDENT</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>ADMIN</td>
     *     </tr>
     *     <tr>
     *         <td>Other</td>
     *         <td>ERROR</td>
     *     </tr>
     * </table>
     * @param status SQL int status
     * @return Java status as enum type {@code Status}
     * @see Status
     */
    public static Status createStatus(int status) {
        return switch (status) {
            case 0 -> Status.STUDENT;
            case 1 -> Status.ADMIN;
            default -> Status.ERROR;
        };
    }
    
    /**
     * Determines the SQL status code based on the java enum status
     * <table>
     *     <tr>
     *         <th>SQL Status Code</th>
     *         <th>Java Enum Status</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>STUDENT</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>ADMIN</td>
     *     </tr>
     *     <tr>
     *         <td>-1</td>
     *         <td>ERROR</td>
     *     </tr>
     * </table>
     * @param status java enum status
     * @return SQL int status
     * @see Status
     */
    public static int createStatus(Status status) {
        return switch (status) {
            case ERROR -> -1;
            case STUDENT -> 0;
            case ADMIN -> 1;
        };
    }
    
    /**
     * Determines if the user is an admin based on a boolean
     * @param isAdmin boolean used to determine if the user is an admin or a student
     * @return admin or student status, (either {@code Status.ADMIN} or {@code Status.STUDENT})
     * @see Status
     * @see Status#isAdmin() isAdmin()
     */
    public static Status createStatus(boolean isAdmin) {
        return isAdmin ? Status.ADMIN : Status.STUDENT;
    }
}
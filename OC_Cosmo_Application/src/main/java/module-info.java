module com.old_colony.oc_cosmo_application {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.old_colony.oc_cosmo_application to javafx.fxml;
    exports com.old_colony.oc_cosmo_application;
}
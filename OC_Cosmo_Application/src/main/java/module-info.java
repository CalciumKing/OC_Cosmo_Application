module com.old_colony.oc_cosmo_application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires fontawesomefx;
    
    opens com.old_colony.oc_cosmo_application.DataClasses to javafx.base;
    opens com.old_colony.oc_cosmo_application to javafx.fxml;
    exports com.old_colony.oc_cosmo_application;
}
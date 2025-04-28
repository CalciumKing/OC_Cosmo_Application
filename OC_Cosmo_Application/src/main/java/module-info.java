module com.old_colony.oc_cosmo_application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires fontawesomefx;
    
    opens com.old_colony.oc_cosmo_application.Data to javafx.base;
    opens com.old_colony.oc_cosmo_application to javafx.fxml;
    exports com.old_colony.oc_cosmo_application;
    exports com.old_colony.oc_cosmo_application.Data;
    exports com.old_colony.oc_cosmo_application.Controllers;
    opens com.old_colony.oc_cosmo_application.Controllers to javafx.base, javafx.fxml;
}
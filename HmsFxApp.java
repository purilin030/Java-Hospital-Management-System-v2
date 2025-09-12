import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HmsFxApp extends Application {

    private Initialization init;

    private final ObservableList<Doctor>   doctorItems   = FXCollections.observableArrayList();
    private final ObservableList<Patient>  patientItems  = FXCollections.observableArrayList();
    private final ObservableList<Staff>    staffItems    = FXCollections.observableArrayList();
    private final ObservableList<Medicine> medicineItems = FXCollections.observableArrayList();
    private final ObservableList<Lab>      labItems      = FXCollections.observableArrayList();
    private final ObservableList<facility> facilityItems = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        init = new Initialization();

        // Sync once from arrays
        refreshDoctorsFromArray();
        refreshPatientsFromArray();
        refreshStaffFromArray();
        refreshMedicinesFromArray();
        refreshLabsFromArray();
        refreshFacilitiesFromArray();

        HeaderBar header = new HeaderBar("Welcome to the Hospital Management System");

        TabPane tabs = new TabPane();

        java.util.function.Consumer<String> info  = this::info;
        java.util.function.Consumer<String> error = this::error;

        // Add all tabs
        Tab doctorsTab = new DoctorsTab(init, doctorItems, this::refreshDoctorsFromArray, info, error).build();
        Tab patientsTab = new PatientsTab(init, patientItems, this::refreshPatientsFromArray, info, error).build();
        Tab staffTab    = new StaffTab(init, staffItems, this::refreshStaffFromArray, info, error).build();
        Tab medsTab     = new MedicinesTab(init, medicineItems, this::refreshMedicinesFromArray, info, error).build();
        Tab labsTab     = new LabsTab(init, labItems, this::refreshLabsFromArray, info, error).build();
        Tab facTab      = new FacilitiesTab(init, facilityItems, this::refreshFacilitiesFromArray, info, error).build();

        tabs.getTabs().addAll(doctorsTab, patientsTab, staffTab, medsTab, labsTab, facTab);

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(tabs);
        root.setPadding(new Insets(16));

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Hospital Management System");
        stage.setScene(scene);
        stage.show();

        header.startClock();
        stage.setOnCloseRequest(e -> header.stopClock());
    }

    // ===== Array -> ObservableList sync =====
    private void refreshDoctorsFromArray() {
        doctorItems.clear();
        for (int i = 0; i < init.docCount; i++) if (init.doctors[i] != null) doctorItems.add(init.doctors[i]);
    }
    private void refreshPatientsFromArray() {
        patientItems.clear();
        for (int i = 0; i < init.patCount; i++) if (init.patients[i] != null) patientItems.add(init.patients[i]);
    }
    private void refreshStaffFromArray() {
        staffItems.clear();
        for (int i = 0; i < init.staffCount; i++) if (init.staff[i] != null) staffItems.add(init.staff[i]);
    }
    private void refreshMedicinesFromArray() {
        medicineItems.clear();
        for (int i = 0; i < init.medCount; i++) if (init.medicines[i] != null) medicineItems.add(init.medicines[i]);
    }
    private void refreshLabsFromArray() {
        labItems.clear();
        for (int i = 0; i < init.labCount; i++) if (init.labs[i] != null) labItems.add(init.labs[i]);
    }
    private void refreshFacilitiesFromArray() {
        facilityItems.clear();
        for (int i = 0; i < init.facCount; i++) if (init.facilities[i] != null) facilityItems.add(init.facilities[i]);
    }

    // ===== Alerts =====
    private void info(String msg) {
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, msg, javafx.scene.control.ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
    private void error(String msg) {
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, msg, javafx.scene.control.ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}



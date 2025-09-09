package Ass1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HmsFxApp extends Application {

    private Initialization init;

    // Observable lists for each entity
    private final ObservableList<Doctor>   doctorItems   = FXCollections.observableArrayList();
    private final ObservableList<Patient>  patientItems  = FXCollections.observableArrayList();
    private final ObservableList<Staff>    staffItems    = FXCollections.observableArrayList();
    private final ObservableList<Medicine> medicineItems = FXCollections.observableArrayList();
    private final ObservableList<Lab>      labItems      = FXCollections.observableArrayList();
    private final ObservableList<facility> facilityItems = FXCollections.observableArrayList();

    private Label clockLabel;

    @Override
    public void start(Stage stage) {
        init = new Initialization();

        refreshDoctorsFromArray();
        refreshPatientsFromArray();
        refreshStaffFromArray();
        refreshMedicinesFromArray();
        refreshLabsFromArray();
        refreshFacilitiesFromArray();

        HBox header = buildHeader();

        TabPane tabs = new TabPane();
        tabs.getTabs().add(buildDoctorsTab());
        tabs.getTabs().add(buildPatientsTab());
        tabs.getTabs().add(buildStaffTab());
        tabs.getTabs().add(buildMedicinesTab());
        tabs.getTabs().add(buildLabsTab());
        tabs.getTabs().add(buildFacilitiesTab());

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(tabs);
        root.setPadding(new Insets(16));

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Hospital Management System (JavaFX)");
        stage.setScene(scene);
        stage.show();

        startClock();
    }

    private HBox buildHeader() {
        Text title = new Text("Welcome to the Hospital Management System");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));

        clockLabel = new Label();
        clockLabel.setStyle("-fx-font-size: 14px; -fx-opacity: 0.85;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(12, title, spacer, new Label("Current Date & Time:"), clockLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10));
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #f6f8fa, #eef3ff);" +
                "-fx-border-color: #cfd8ff; -fx-border-width: 0 0 1 0;"
        );
        return header;
    }

    private void startClock() {
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Thread t = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    while (true) {
                        final String now = LocalDateTime.now().format(fmt);
                        Platform.runLater(new Runnable() {
                            @Override public void run() { clockLabel.setText(now); }
                        });
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ignored) {}
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // ===== Doctors Tab =====
    private Tab buildDoctorsTab() {
        TableView<Doctor> table = new TableView<Doctor>(doctorItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Doctor, String> cId   = new TableColumn<Doctor, String>("ID");
        TableColumn<Doctor, String> cName = new TableColumn<Doctor, String>("Name");
        TableColumn<Doctor, String> cSpec = new TableColumn<Doctor, String>("Specialist");
        TableColumn<Doctor, String> cWork = new TableColumn<Doctor, String>("Work Time");
        TableColumn<Doctor, String> cQual = new TableColumn<Doctor, String>("Qualification");
        TableColumn<Doctor, String> cRoom = new TableColumn<Doctor, String>("Room No.");

        cId.setCellValueFactory(new PropertyValueFactory<Doctor, String>("id"));
        cName.setCellValueFactory(new PropertyValueFactory<Doctor, String>("name"));
        cSpec.setCellValueFactory(new PropertyValueFactory<Doctor, String>("specialist"));
        cWork.setCellValueFactory(new PropertyValueFactory<Doctor, String>("worktime"));
        cQual.setCellValueFactory(new PropertyValueFactory<Doctor, String>("qualification"));
        cRoom.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getRoom())));

        table.getColumns().addAll(cId, cName, cSpec, cWork, cQual, cRoom);

        final TextField searchField = new TextField();
        searchField.setPromptText("Search by Doctor ID...");
        final Button searchBtn  = new Button("Search");
        final Button addBtn     = new Button("Add");
        final Button deleteBtn  = new Button("Delete");
        final Button refreshBtn = new Button("Refresh");

        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));

        // search by ID (exact)
        searchBtn.setOnAction(e -> {
            String id = searchField.getText() == null ? "" : searchField.getText().trim();
            if (id.isEmpty()) { info("Enter Doctor ID."); return; }
            Doctor match = null;
            for (Doctor d : doctorItems) if (d != null && d.getId().equalsIgnoreCase(id)) { match = d; break; }
            if (match == null) info("Not found."); else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });

        addBtn.setOnAction(e -> {
        	Optional<Doctor> maybe = DoctorFormDialog.show();
            if (maybe.isPresent()) {
                Doctor d = maybe.get();
                if (init.docCount >= init.doctors.length) { error("Doctor list full."); return; }
                for (int i = 0; i < init.docCount; i++) {
                    if (init.doctors[i].getId().equalsIgnoreCase(d.getId())) { error("Duplicate ID."); return; }
                }
                init.doctors[init.docCount++] = d;
                doctorItems.add(d);
                info("Added.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Doctor sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info("Select a row."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete doctor [" + sel.getId() + " - " + sel.getName() + "] ?", ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deleteDoctorById(init.doctors, new int[]{init.docCount}, sel.getId());
                    if (ok) { init.docCount--; refreshDoctorsFromArray(); info("Deleted."); } else error("Delete failed.");
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshDoctorsFromArray());

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Doctors", content);
        tab.setClosable(false);
        return tab;
    }

    // ===== Patients Tab =====
    private Tab buildPatientsTab() {
        TableView<Patient> table = new TableView<Patient>(patientItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Patient, String> cId    = new TableColumn<Patient, String>("ID");
        TableColumn<Patient, String> cName  = new TableColumn<Patient, String>("Name");
        TableColumn<Patient, String> cDis   = new TableColumn<Patient, String>("Disease");
        TableColumn<Patient, String> cSex   = new TableColumn<Patient, String>("Sex");
        TableColumn<Patient, String> cAdm   = new TableColumn<Patient, String>("Admission");
        TableColumn<Patient, String> cAge   = new TableColumn<Patient, String>("Age");

        cId.setCellValueFactory(new PropertyValueFactory<Patient, String>("id"));
        cName.setCellValueFactory(new PropertyValueFactory<Patient, String>("name"));
        cDis.setCellValueFactory(new PropertyValueFactory<Patient, String>("disease"));
        cSex.setCellValueFactory(new PropertyValueFactory<Patient, String>("sex"));
        cAdm.setCellValueFactory(new PropertyValueFactory<Patient, String>("adminStatus"));
        cAge.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getAge())));

        table.getColumns().addAll(cId, cName, cDis, cSex, cAdm, cAge);

        final TextField searchField = new TextField();
        searchField.setPromptText("Search by Patient ID...");
        final Button searchBtn  = new Button("Search");
        final Button addBtn     = new Button("Add");
        final Button deleteBtn  = new Button("Delete");
        final Button refreshBtn = new Button("Refresh");

        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));

        searchBtn.setOnAction(e -> {
            String id = searchField.getText() == null ? "" : searchField.getText().trim();
            if (id.isEmpty()) { info("Enter Patient ID."); return; }
            Patient match = null;
            for (Patient p : patientItems) if (p != null && p.getId().equalsIgnoreCase(id)) { match = p; break; }
            if (match == null) info("Not found."); else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });

        addBtn.setOnAction(e -> {
        	Optional<Patient> maybe = PatientFormDialog.show();
            if (maybe.isPresent()) {
                Patient p = maybe.get();
                if (init.patCount >= init.patients.length) { error("Patient list full."); return; }
                for (int i = 0; i < init.patCount; i++) {
                    if (init.patients[i].getId().equalsIgnoreCase(p.getId())) { error("Duplicate ID."); return; }
                }
                init.patients[init.patCount++] = p;
                patientItems.add(p);
                info("Added.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info("Select a row."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete patient [" + sel.getId() + " - " + sel.getName() + "] ?", ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deletePatientById(init.patients, new int[]{init.patCount}, sel.getId());
                    if (ok) { init.patCount--; refreshPatientsFromArray(); info("Deleted."); } else error("Delete failed.");
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshPatientsFromArray());

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Patients", content);
        tab.setClosable(false);
        return tab;
    }

    // ===== Staff Tab =====
    private Tab buildStaffTab() {
        TableView<Staff> table = new TableView<Staff>(staffItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Staff, String> cId    = new TableColumn<Staff, String>("ID");
        TableColumn<Staff, String> cName  = new TableColumn<Staff, String>("Name");
        TableColumn<Staff, String> cDes   = new TableColumn<Staff, String>("Designation");
        TableColumn<Staff, String> cSex   = new TableColumn<Staff, String>("Sex");
        TableColumn<Staff, String> cSal   = new TableColumn<Staff, String>("Salary");

        cId.setCellValueFactory(new PropertyValueFactory<Staff, String>("id"));
        cName.setCellValueFactory(new PropertyValueFactory<Staff, String>("name"));
        cDes.setCellValueFactory(new PropertyValueFactory<Staff, String>("designation"));
        cSex.setCellValueFactory(new PropertyValueFactory<Staff, String>("sex"));
        cSal.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getSalary())));

        table.getColumns().addAll(cId, cName, cDes, cSex, cSal);

        final TextField searchField = new TextField();
        searchField.setPromptText("Search by Staff ID...");
        final Button searchBtn  = new Button("Search");
        final Button addBtn     = new Button("Add");
        final Button deleteBtn  = new Button("Delete");
        final Button refreshBtn = new Button("Refresh");

        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));

        searchBtn.setOnAction(e -> {
            String id = searchField.getText() == null ? "" : searchField.getText().trim();
            if (id.isEmpty()) { info("Enter Staff ID."); return; }
            Staff match = null;
            for (Staff s : staffItems) if (s != null && s.getId().equalsIgnoreCase(id)) { match = s; break; }
            if (match == null) info("Not found."); else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });

        addBtn.setOnAction(e -> {
        	Optional<Staff> maybe = StaffFormDialog.show();
            if (maybe.isPresent()) {
                Staff s = maybe.get();
                if (init.staffCount >= init.staff.length) { error("Staff list full."); return; }
                for (int i = 0; i < init.staffCount; i++) {
                    if (init.staff[i].getId().equalsIgnoreCase(s.getId())) { error("Duplicate ID."); return; }
                }
                init.staff[init.staffCount++] = s;
                staffItems.add(s);
                info("Added.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Staff sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info("Select a row."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete staff [" + sel.getId() + " - " + sel.getName() + "] ?", ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deleteStaffById(init.staff, new int[]{init.staffCount}, sel.getId());
                    if (ok) { init.staffCount--; refreshStaffFromArray(); info("Deleted."); } else error("Delete failed.");
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshStaffFromArray());

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Staff", content);
        tab.setClosable(false);
        return tab;
    }

    // ===== Medicines Tab =====
    private Tab buildMedicinesTab() {
        TableView<Medicine> table = new TableView<Medicine>(medicineItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Medicine, String> cName  = new TableColumn<Medicine, String>("Name");
        TableColumn<Medicine, String> cManu  = new TableColumn<Medicine, String>("Manufacturer");
        TableColumn<Medicine, String> cExp   = new TableColumn<Medicine, String>("Expiry");
        TableColumn<Medicine, String> cCost  = new TableColumn<Medicine, String>("Cost");
        TableColumn<Medicine, String> cCount = new TableColumn<Medicine, String>("Count");

        cName.setCellValueFactory(new PropertyValueFactory<Medicine, String>("name"));
        cManu.setCellValueFactory(new PropertyValueFactory<Medicine, String>("manufacturer"));
        cExp.setCellValueFactory(new PropertyValueFactory<Medicine, String>("expiryDate"));
        cCost.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getCost())));
        cCount.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getCount())));

        table.getColumns().addAll(cName, cManu, cExp, cCost, cCount);

        final TextField searchField = new TextField();
        searchField.setPromptText("Search by Medicine Name...");
        final Button searchBtn  = new Button("Search");
        final Button addBtn     = new Button("Add");
        final Button deleteBtn  = new Button("Delete");
        final Button refreshBtn = new Button("Refresh");

        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));

        searchBtn.setOnAction(e -> {
            String name = searchField.getText() == null ? "" : searchField.getText().trim();
            if (name.isEmpty()) { info("Enter Medicine Name."); return; }
            Medicine match = null;
            for (Medicine m : medicineItems) if (m != null && m.getName().equalsIgnoreCase(name)) { match = m; break; }
            if (match == null) info("Not found."); else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });

        addBtn.setOnAction(e -> {
        	Optional<Medicine> maybe = MedicineFormDialog.show();
            if (maybe.isPresent()) {
                Medicine m = maybe.get();
                if (init.medCount >= init.medicines.length) { error("Medicine list full."); return; }
                for (int i = 0; i < init.medCount; i++) {
                    if (init.medicines[i].getName().equalsIgnoreCase(m.getName())) { error("Duplicate name."); return; }
                }
                init.medicines[init.medCount++] = m;
                medicineItems.add(m);
                info("Added.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Medicine sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info("Select a row."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete medicine [" + sel.getName() + "] ?", ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deleteMedicineByName(init.medicines, new int[]{init.medCount}, sel.getName());
                    if (ok) { init.medCount--; refreshMedicinesFromArray(); info("Deleted."); } else error("Delete failed.");
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshMedicinesFromArray());

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Medicines", content);
        tab.setClosable(false);
        return tab;
    }

    // ===== Labs Tab =====
    private Tab buildLabsTab() {
        TableView<Lab> table = new TableView<Lab>(labItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Lab, String> cLab  = new TableColumn<Lab, String>("Lab");
        TableColumn<Lab, String> cCost = new TableColumn<Lab, String>("Cost");

        cLab.setCellValueFactory(new PropertyValueFactory<Lab, String>("lab"));
        cCost.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getCost())));
        table.getColumns().addAll(cLab, cCost);

        final TextField searchField = new TextField();
        searchField.setPromptText("Search by Lab Name...");
        final Button searchBtn  = new Button("Search");
        final Button addBtn     = new Button("Add");
        final Button deleteBtn  = new Button("Delete");
        final Button refreshBtn = new Button("Refresh");

        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));

        searchBtn.setOnAction(e -> {
            String name = searchField.getText() == null ? "" : searchField.getText().trim();
            if (name.isEmpty()) { info("Enter Lab Name."); return; }
            Lab match = null;
            for (Lab l : labItems) if (l != null && l.getLab().equalsIgnoreCase(name)) { match = l; break; }
            if (match == null) info("Not found."); else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });

        addBtn.setOnAction(e -> {
        	Optional<Lab> maybe = LabFormDialog.show();
            if (maybe.isPresent()) {
                Lab l = maybe.get();
                if (init.labCount >= init.labs.length) { error("Lab list full."); return; }
                for (int i = 0; i < init.labCount; i++) {
                    if (init.labs[i].getLab().equalsIgnoreCase(l.getLab())) { error("Duplicate name."); return; }
                }
                init.labs[init.labCount++] = l;
                labItems.add(l);
                info("Added.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Lab sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info("Select a row."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete lab [" + sel.getLab() + "] ?", ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deleteLabByName(init.labs, new int[]{init.labCount}, sel.getLab());
                    if (ok) { init.labCount--; refreshLabsFromArray(); info("Deleted."); } else error("Delete failed.");
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshLabsFromArray());

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Labs", content);
        tab.setClosable(false);
        return tab;
    }

    // ===== Facilities Tab =====
    private Tab buildFacilitiesTab() {
        TableView<facility> table = new TableView<facility>(facilityItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<facility, String> cFac = new TableColumn<facility, String>("Facility");
        cFac.setCellValueFactory(new PropertyValueFactory<facility, String>("facility"));
        table.getColumns().addAll(cFac);

        final TextField searchField = new TextField();
        searchField.setPromptText("Search by Facility Name...");
        final Button searchBtn  = new Button("Search");
        final Button addBtn     = new Button("Add");
        final Button deleteBtn  = new Button("Delete");
        final Button refreshBtn = new Button("Refresh");

        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));

        searchBtn.setOnAction(e -> {
            String name = searchField.getText() == null ? "" : searchField.getText().trim();
            if (name.isEmpty()) { info("Enter Facility Name."); return; }
            facility match = null;
            for (facility f : facilityItems) if (f != null && f.getFacility().equalsIgnoreCase(name)) { match = f; break; }
            if (match == null) info("Not found."); else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });

        addBtn.setOnAction(e -> {
        	Optional<facility> maybe = FacilityFormDialog.show();
            if (maybe.isPresent()) {
                facility f = maybe.get();
                if (init.facCount >= init.facilities.length) { error("Facility list full."); return; }
                for (int i = 0; i < init.facCount; i++) {
                    if (init.facilities[i].getFacility().equalsIgnoreCase(f.getFacility())) { error("Duplicate name."); return; }
                }
                init.facilities[init.facCount++] = f;
                facilityItems.add(f);
                info("Added.");
            }
        });

        deleteBtn.setOnAction(e -> {
            facility sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info("Select a row."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete facility [" + sel.getFacility() + "] ?", ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deleteFacilityByName(init.facilities, new int[]{init.facCount}, sel.getFacility());
                    if (ok) { init.facCount--; refreshFacilitiesFromArray(); info("Deleted."); } else error("Delete failed.");
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshFacilitiesFromArray());

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Facilities", content);
        tab.setClosable(false);
        return tab;
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
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
    private void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    public static void main(String[] args) { 
    	launch(args); 
    }
}

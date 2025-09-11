package Ass1;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.Consumer;

public class PatientsTab {
    private final Initialization init;
    private final ObservableList<Patient> items;
    private final Runnable refreshFromArray;
    private final Consumer<String> info;
    private final Consumer<String> error;

    public PatientsTab(Initialization init,
                       ObservableList<Patient> items,
                       Runnable refreshFromArray,
                       Consumer<String> info,
                       Consumer<String> error) {
        this.init = init;
        this.items = items;
        this.refreshFromArray = refreshFromArray;
        this.info = info;
        this.error = error;
    }

    public Tab build() {
    	//table and columns
        TableView<Patient> table = new TableView<>(items);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);//Use CONSTRAINED to automatically distribute the remaining width evenly among columns

        TableColumn<Patient, String> cId   = new TableColumn<>("ID");
        TableColumn<Patient, String> cName = new TableColumn<>("Name");
        TableColumn<Patient, String> cDis  = new TableColumn<>("Disease");
        TableColumn<Patient, String> cSex  = new TableColumn<>("Sex");
        TableColumn<Patient, String> cAdm  = new TableColumn<>("Admission");
        TableColumn<Patient, String> cAge  = new TableColumn<>("Age");

        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cDis.setCellValueFactory(new PropertyValueFactory<>("disease"));
        cSex.setCellValueFactory(new PropertyValueFactory<>("sex"));
        cAdm.setCellValueFactory(new PropertyValueFactory<>("adminStatus"));
        cAge.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getAge())));

        table.getColumns().addAll(cId, cName, cDis, cSex, cAdm, cAge);

        //search box
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Patient ID...");
        //buttons
        Button searchBtn  = new Button("Search");
        Button addBtn     = new Button("Add");
        Button deleteBtn  = new Button("Delete");
        Button refreshBtn = new Button("Refresh");

        //header of tab
        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));
        
        //searching
        searchBtn.setOnAction(e -> {
            String id = searchField.getText() == null ? "" : searchField.getText().trim();
          //if search box is empty, if will pop up a message
            if (id.isEmpty()) { info.accept("Enter Patient ID."); return; }
          //if no found the id
            Patient match = null;
            for (Patient p : items) if (p != null && p.getId().equalsIgnoreCase(id)) { match = p; break; }
            if (match == null) info.accept("Not found.");
            else { table.getSelectionModel().select(match); table.scrollTo(match); }//highlight the row you want to find
        });
        
        searchField.setOnAction(e -> searchBtn.fire());//support press enter to search

        //adding
        addBtn.setOnAction(e -> {
            Optional<Patient> maybe = PatientFormDialog.show();
            if (maybe.isPresent()) {
                Patient p = maybe.get();
                //if the number of patient is out of the array length which is declared in initialization 
                if (init.patCount >= init.patients.length) { error.accept("Patient list full."); return; }
              //validation for adding duplicate id
                for (int i = 0; i < init.patCount; i++) {
                    if (init.patients[i].getId().equalsIgnoreCase(p.getId())) { error.accept("Duplicate ID."); return; }
                }
                init.patients[init.patCount++] = p;
                items.add(p);
                info.accept("Added.");
            }
        });

        //deleting
        deleteBtn.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            
          //confirmation for delete
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete patient [" + sel.getId() + " - " + sel.getName() + "] ?",
                    ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deletePatientById(init.patients, new int[]{init.patCount}, sel.getId());
                    if (ok) { init.patCount--; refreshFromArray.run(); info.accept("Deleted."); } else error.accept("Delete failed.");
                }
            });
        });
        
      //if user no select any row, delete button cannot use
        deleteBtn.disableProperty().bind(
        	    table.getSelectionModel().selectedItemProperty().isNull()
        );

      //refreshing
        refreshBtn.setOnAction(e -> refreshFromArray.run());//ensure UI consistency with the actual source

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Patients", content);
        tab.setClosable(false);
        return tab;
    }
}

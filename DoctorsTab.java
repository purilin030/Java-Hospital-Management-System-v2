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

public class DoctorsTab {
    private final Initialization init;
    private final ObservableList<Doctor> items;
    private final Runnable refreshFromArray;
    private final Consumer<String> info;
    private final Consumer<String> error;

    public DoctorsTab(Initialization init,
                      ObservableList<Doctor> items,
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
        TableView<Doctor> table = new TableView<>(items);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Doctor, String> cId   = new TableColumn<>("ID");
        TableColumn<Doctor, String> cName = new TableColumn<>("Name");
        TableColumn<Doctor, String> cSpec = new TableColumn<>("Specialist");
        TableColumn<Doctor, String> cWork = new TableColumn<>("Work Time");
        TableColumn<Doctor, String> cQual = new TableColumn<>("Qualification");
        TableColumn<Doctor, String> cRoom = new TableColumn<>("Room No.");

        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cSpec.setCellValueFactory(new PropertyValueFactory<>("specialist"));
        cWork.setCellValueFactory(new PropertyValueFactory<>("worktime"));
        cQual.setCellValueFactory(new PropertyValueFactory<>("qualification"));
        cRoom.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getRoom())));

        table.getColumns().addAll(cId, cName, cSpec, cWork, cQual, cRoom);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Doctor ID...");
        Button searchBtn  = new Button("Search");
        Button addBtn     = new Button("Add");
        Button deleteBtn  = new Button("Delete");
        Button refreshBtn = new Button("Refresh");

        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));

        searchBtn.setOnAction(e -> {
            String id = searchField.getText() == null ? "" : searchField.getText().trim();
            if (id.isEmpty()) { info.accept("Enter Doctor ID."); return; }
            Doctor match = null;
            for (Doctor d : items) if (d != null && d.getId().equalsIgnoreCase(id)) { match = d; break; }
            if (match == null) info.accept("Not found.");
            else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });

        addBtn.setOnAction(e -> {
            Optional<Doctor> maybe = DoctorFormDialog.show();
            if (maybe.isPresent()) {
                Doctor d = maybe.get();
                if (init.docCount >= init.doctors.length) { error.accept("Doctor list full."); return; }
                for (int i = 0; i < init.docCount; i++) {
                    if (init.doctors[i].getId().equalsIgnoreCase(d.getId())) { error.accept("Duplicate ID."); return; }
                }
                init.doctors[init.docCount++] = d;
                items.add(d);
                info.accept("Added.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Doctor sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info.accept("Select a row."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete doctor [" + sel.getId() + " - " + sel.getName() + "] ?",
                    ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deleteDoctorById(init.doctors, new int[]{init.docCount}, sel.getId());
                    if (ok) { init.docCount--; refreshFromArray.run(); info.accept("Deleted."); } else error.accept("Delete failed.");
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshFromArray.run());

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Doctors", content);
        tab.setClosable(false);
        return tab;
    }
}

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

public class StaffTab {
    private final Initialization init;
    private final ObservableList<Staff> items;
    private final Runnable refreshFromArray;
    private final Consumer<String> info;
    private final Consumer<String> error;

    public StaffTab(Initialization init,
                    ObservableList<Staff> items,
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
        TableView<Staff> table = new TableView<>(items);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Staff, String> cId   = new TableColumn<>("ID");
        TableColumn<Staff, String> cName = new TableColumn<>("Name");
        TableColumn<Staff, String> cDes  = new TableColumn<>("Designation");
        TableColumn<Staff, String> cSex  = new TableColumn<>("Sex");
        TableColumn<Staff, String> cSal  = new TableColumn<>("Salary");

        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cDes.setCellValueFactory(new PropertyValueFactory<>("designation"));
        cSex.setCellValueFactory(new PropertyValueFactory<>("sex"));
        cSal.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getSalary())));

        table.getColumns().addAll(cId, cName, cDes, cSex, cSal);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Staff ID...");
        Button searchBtn  = new Button("Search");
        Button addBtn     = new Button("Add");
        Button deleteBtn  = new Button("Delete");
        Button refreshBtn = new Button("Refresh");

        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));

        searchBtn.setOnAction(e -> {
            String id = searchField.getText() == null ? "" : searchField.getText().trim();
            if (id.isEmpty()) { info.accept("Enter Staff ID."); return; }
            Staff match = null;
            for (Staff s : items) if (s != null && s.getId().equalsIgnoreCase(id)) { match = s; break; }
            if (match == null) info.accept("Not found.");
            else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });

        addBtn.setOnAction(e -> {
            Optional<Staff> maybe = StaffFormDialog.show();
            if (maybe.isPresent()) {
                Staff s = maybe.get();
                if (init.staffCount >= init.staff.length) { error.accept("Staff list full."); return; }
                for (int i = 0; i < init.staffCount; i++) {
                    if (init.staff[i].getId().equalsIgnoreCase(s.getId())) { error.accept("Duplicate ID."); return; }
                }
                init.staff[init.staffCount++] = s;
                items.add(s);
                info.accept("Added.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Staff sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info.accept("Select a row."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete staff [" + sel.getId() + " - " + sel.getName() + "] ?",
                    ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deleteStaffById(init.staff, new int[]{init.staffCount}, sel.getId());
                    if (ok) { init.staffCount--; refreshFromArray.run(); info.accept("Deleted."); } else error.accept("Delete failed.");
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshFromArray.run());

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Staff", content);
        tab.setClosable(false);
        return tab;
    }
}

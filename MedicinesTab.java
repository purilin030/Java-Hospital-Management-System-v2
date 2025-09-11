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

public class MedicinesTab {
    private final Initialization init;
    private final ObservableList<Medicine> items;
    private final Runnable refreshFromArray;
    private final Consumer<String> info;
    private final Consumer<String> error;

    public MedicinesTab(Initialization init,
                        ObservableList<Medicine> items,
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
        TableView<Medicine> table = new TableView<>(items);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Medicine, String> cName  = new TableColumn<>("Name");
        TableColumn<Medicine, String> cManu  = new TableColumn<>("Manufacturer");
        TableColumn<Medicine, String> cExp   = new TableColumn<>("Expiry");
        TableColumn<Medicine, String> cCost  = new TableColumn<>("Cost");
        TableColumn<Medicine, String> cCount = new TableColumn<>("Count");

        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cManu.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        cExp.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        cCost.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getCost())));
        cCount.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getCount())));

        table.getColumns().addAll(cName, cManu, cExp, cCost, cCount);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Medicine Name...");
        Button searchBtn  = new Button("Search");
        Button addBtn     = new Button("Add");
        Button deleteBtn  = new Button("Delete");
        Button refreshBtn = new Button("Refresh");

        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));

        searchBtn.setOnAction(e -> {
            String name = searchField.getText() == null ? "" : searchField.getText().trim();
            if (name.isEmpty()) { info.accept("Enter Medicine Name."); return; }
            Medicine match = null;
            for (Medicine m : items) if (m != null && m.getName().equalsIgnoreCase(name)) { match = m; break; }
            if (match == null) info.accept("Not found.");
            else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });

        addBtn.setOnAction(e -> {
            Optional<Medicine> maybe = MedicineFormDialog.show();
            if (maybe.isPresent()) {
                Medicine m = maybe.get();
                if (init.medCount >= init.medicines.length) { error.accept("Medicine list full."); return; }
                for (int i = 0; i < init.medCount; i++) {
                    if (init.medicines[i].getName().equalsIgnoreCase(m.getName())) { error.accept("Duplicate name."); return; }
                }
                init.medicines[init.medCount++] = m;
                items.add(m);
                info.accept("Added.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Medicine sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info.accept("Select a row."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete medicine [" + sel.getName() + "] ?",
                    ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deleteMedicineByName(init.medicines, new int[]{init.medCount}, sel.getName());
                    if (ok) { init.medCount--; refreshFromArray.run(); info.accept("Deleted."); } else error.accept("Delete failed.");
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshFromArray.run());

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Medicines", content);
        tab.setClosable(false);
        return tab;
    }
}

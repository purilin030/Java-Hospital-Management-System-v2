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

public class LabsTab {
    private final Initialization init;
    private final ObservableList<Lab> items;
    private final Runnable refreshFromArray;
    private final Consumer<String> info;
    private final Consumer<String> error;

    public LabsTab(Initialization init,
                   ObservableList<Lab> items,
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
        TableView<Lab> table = new TableView<>(items);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Lab, String> cLab  = new TableColumn<>("Lab");
        TableColumn<Lab, String> cCost = new TableColumn<>("Cost");

        cLab.setCellValueFactory(new PropertyValueFactory<>("lab"));
        cCost.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getCost())));

        table.getColumns().addAll(cLab, cCost);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Lab Name...");
        Button searchBtn  = new Button("Search");
        Button addBtn     = new Button("Add");
        Button deleteBtn  = new Button("Delete");
        Button refreshBtn = new Button("Refresh");

        HBox actions = new HBox(8, searchField, searchBtn, addBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 8, 0));

        searchBtn.setOnAction(e -> {
            String name = searchField.getText() == null ? "" : searchField.getText().trim();
            if (name.isEmpty()) { info.accept("Enter Lab Name."); return; }
            Lab match = null;
            for (Lab l : items) if (l != null && l.getLab().equalsIgnoreCase(name)) { match = l; break; }
            if (match == null) info.accept("Not found.");
            else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });

        addBtn.setOnAction(e -> {
            Optional<Lab> maybe = LabFormDialog.show();
            if (maybe.isPresent()) {
                Lab l = maybe.get();
                if (init.labCount >= init.labs.length) { error.accept("Lab list full."); return; }
                for (int i = 0; i < init.labCount; i++) {
                    if (init.labs[i].getLab().equalsIgnoreCase(l.getLab())) { error.accept("Duplicate name."); return; }
                }
                init.labs[init.labCount++] = l;
                items.add(l);
                info.accept("Added.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Lab sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info.accept("Select a row."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete lab [" + sel.getLab() + "] ?",
                    ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deleteLabByName(init.labs, new int[]{init.labCount}, sel.getLab());
                    if (ok) { init.labCount--; refreshFromArray.run(); info.accept("Deleted."); } else error.accept("Delete failed.");
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshFromArray.run());

        VBox content = new VBox(actions, table);
        content.setSpacing(6);

        Tab tab = new Tab("Labs", content);
        tab.setClosable(false);
        return tab;
    }
}

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.Consumer;

public class FacilitiesTab {
    private final Initialization init;
    private final ObservableList<facility> items;
    private final Runnable refreshFromArray;
    private final Consumer<String> info;
    private final Consumer<String> error;

    public FacilitiesTab(Initialization init,
                         ObservableList<facility> items,
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
        TableView<facility> table = new TableView<>(items);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);//Use CONSTRAINED to automatically distribute the remaining width evenly among columns

        TableColumn<facility, String> cFac = new TableColumn<>("Facility");
        cFac.setCellValueFactory(new PropertyValueFactory<>("facility"));
        table.getColumns().addAll(cFac);

        //search box
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Facility Name...");
        //button
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
            String name = searchField.getText() == null ? "" : searchField.getText().trim();
          //if search box is empty, if will pop up a message
            if (name.isEmpty()) { info.accept("Enter Facility Name."); return; }
          //if no found the name
            facility match = null;
            for (facility f : items) if (f != null && f.getFacility().equalsIgnoreCase(name)) { match = f; break; }
            if (match == null) info.accept("Not found.");
            else { table.getSelectionModel().select(match); table.scrollTo(match); }
        });
        
        searchField.setOnAction(e -> searchBtn.fire());//support press enter to search

        //adding
        addBtn.setOnAction(e -> {
            Optional<facility> maybe = FacilityFormDialog.show();
            if (maybe.isPresent()) {
                facility f = maybe.get();
              //if the number of facility is out of the array length which is declared in initialization
                if (init.facCount >= init.facilities.length) { error.accept("Facility list full."); return; }
              //validation for adding duplicate name
                for (int i = 0; i < init.facCount; i++) {
                    if (init.facilities[i].getFacility().equalsIgnoreCase(f.getFacility())) { error.accept("Duplicate name."); return; }
                }
                init.facilities[init.facCount++] = f;
                items.add(f);
                info.accept("Added.");
            }
        });

        //deleting
        deleteBtn.setOnAction(e -> {
            facility sel = table.getSelectionModel().getSelectedItem();
            
          //confirmation for delete
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete facility [" + sel.getFacility() + "] ?",
                    ButtonType.OK, ButtonType.CANCEL);
            c.setHeaderText("Confirm Deletion");
            c.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean ok = Deleting.deleteFacilityByName(init.facilities, new int[]{init.facCount}, sel.getFacility());
                    if (ok) { init.facCount--; refreshFromArray.run(); info.accept("Deleted."); } else error.accept("Delete failed.");
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

        Tab tab = new Tab("Facilities", content);
        tab.setClosable(false);
        return tab;
    }
}


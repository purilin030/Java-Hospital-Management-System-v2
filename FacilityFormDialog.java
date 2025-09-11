package Ass1;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class FacilityFormDialog {
    
    public static Optional<facility> show() {

        // <facilitiy> means return the doctor type
        Dialog<facility> dialog = new Dialog<facility>();
        dialog.setTitle("Add New Facility");
        dialog.setHeaderText("Fill in the details");


        //Add the "Add " button and confirm the function "ButtonBar.ButtonData.OK_DONE" 
        ButtonType okType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        //addAll to "Add" button that had created before and JavaFx's cancel button
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        //Add textfield and hint to prompt user to enter data
        final TextField name = new TextField();
        name.setPromptText("e.g., ICU");

        // Create a new label, display the error text when user prompt wrong data
        final Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #d00000; -fx-font-size: 12px;");

        // Create a new GridPane and configure the horizontal and vertical
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10); gp.setPadding(new Insets(10));

        //Using addRow to combine the Label and TextField
        gp.addRow(0, new Label("Facility Name:"), name);
        gp.add(errorLabel, 0, 1, 2, 1);

        //Make okType to Node addBtn element and disable addButton disable first
        Node addBtn = dialog.getDialogPane().lookupButton(okType);
        addBtn.setDisable(true);

        // Validation (using validate function )
        name.textProperty().addListener((o, ov, nv) -> validate(name, errorLabel, addBtn));

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {

            //last validate check
            if (btn == okType) {
                if (!validate(name, errorLabel, addBtn)) return null;
                //return data
                return new facility(name.getText().trim());
            }
            return null;
        });

        //return dialog
        return dialog.showAndWait();
    }

    //Functions (validate): 
    private static boolean validate(TextField name, Label errorLabel, Node addBtn) {
        name.setStyle(null);
        String err = null;

        if (name.getText().trim().isEmpty()) { err = "Facility name is required."; name.setStyle("-fx-border-color: #d00000;"); }

        errorLabel.setText(err == null ? "" : err);
        addBtn.setDisable(err != null);
        return err == null;
    }
}



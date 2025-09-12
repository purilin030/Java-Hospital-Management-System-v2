import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class MedicineFormDialog {

    public static Optional<Medicine> show() {

         // <Medicine> means return the medicine type 
        Dialog<Medicine> dialog = new Dialog<Medicine>();
        dialog.setTitle("Add New Medicine");
        dialog.setHeaderText("Fill in the details");

        //Add the "Add " button and confirm the function "ButtonBar.ButtonData.OK_DONE" 
        ButtonType okType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        //addAll to "Add" button that had created before and JavaFx's cancel button
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        //Add textfield to prompt user to enter data
        final TextField name = new TextField();
        final TextField manufacturer = new TextField();
        final TextField expiry = new TextField();
        final TextField cost = new TextField();
        final TextField count = new TextField();

        //Add hint text
        name.setPromptText("e.g., Paracetamol");
        manufacturer.setPromptText("Manufacturer");
        expiry.setPromptText("YYYY-MM-DD");
        cost.setPromptText("e.g., 1");
        count.setPromptText("e.g., 100");

        // Create a new label, display the error text when user prompt wrong data
        final Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #d00000; -fx-font-size: 12px;");

        // Create a new GridPane and configure the horizontal and vertical
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10); gp.setPadding(new Insets(10));

        //Using addRow to combine the Label and TextField
        gp.addRow(0, new Label("Name:"), name);
        gp.addRow(1, new Label("Manufacturer:"), manufacturer);
        gp.addRow(2, new Label("Expiry Date:"), expiry);
        gp.addRow(3, new Label("Cost per unit:"), cost);
        gp.addRow(4, new Label("Count:"), count);
        gp.add(errorLabel, 0, 5, 2, 1);

        //Make okType to Node addBtn element and disable addButton disable first
        Node addBtn = dialog.getDialogPane().lookupButton(okType);
        addBtn.setDisable(true);

        // Validation (using validate function )
        name.textProperty().addListener((o, ov, nv) -> validate(name, expiry, cost, count, errorLabel, addBtn));
        expiry.textProperty().addListener((o, ov, nv) -> validate(name, expiry, cost, count, errorLabel, addBtn));
        cost.textProperty().addListener((o, ov, nv) -> validate(name, expiry, cost, count, errorLabel, addBtn));
        count.textProperty().addListener((o, ov, nv) -> validate(name, expiry, cost, count, errorLabel, addBtn));

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                //Last validate check 
                if (!validate(name, expiry, cost, count, errorLabel, addBtn)) return null;
                try {
                    // parse & check future/any date
                    LocalDate.parse(expiry.getText().trim());
                    int costVal = Integer.parseInt(cost.getText().trim());
                    int countVal = Integer.parseInt(count.getText().trim());
                    if (costVal <= 0) { alert("Cost must be a positive integer"); return null; }
                    if (countVal <= 0) { alert("Count must be a positive integer"); return null; }

                    //return data 
                    return new Medicine(
                            name.getText().trim(),
                            manufacturer.getText().trim(),
                            expiry.getText().trim(),
                            costVal,
                            countVal
                    );
                } catch (DateTimeParseException ex) { alert("Expiry must be in yyyy-MM-dd format"); return null; }
                catch (NumberFormatException ex) { alert("Cost/Count must be numbers"); return null; }
            }
            return null;
        });

        //return dialog
        return dialog.showAndWait();
    }
    
    //Functions (validate): 
    private static boolean validate(TextField name, TextField expiry, TextField cost, TextField count,
                                    Label errorLabel, Node addBtn) {
        clear(name, expiry, cost, count);
        String err = null;

        if (name.getText().trim().isEmpty()) { err = "Name is required."; mark(name); }
        else if (expiry.getText().trim().isEmpty()) { err = "Expiry date is required."; mark(expiry); }
        else {
            try { LocalDate.parse(expiry.getText().trim()); }
            catch (DateTimeParseException e) { err = "Expiry must be yyyy-MM-dd."; mark(expiry); }
            if (err == null) {
                if (cost.getText().trim().isEmpty()) { err = "Cost is required."; mark(cost); }
                else if (count.getText().trim().isEmpty()) { err = "Count is required."; mark(count); }
                else {
                    try {
                        int c = Integer.parseInt(cost.getText().trim());
                        int n = Integer.parseInt(count.getText().trim());
                        if (c <= 0) { err = "Cost must be a positive integer."; mark(cost); }
                        else if (n <= 0) { err = "Count must be a positive integer."; mark(count); }
                    } catch (NumberFormatException e) {
                        err = "Cost/Count must be numbers."; mark(cost); mark(count);
                    }
                }
            }
        }

        errorLabel.setText(err == null ? "" : err);
        addBtn.setDisable(err != null);
        return err == null;
    }

    private static void alert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
    private static void clear(TextField... tfs) { for (TextField tf : tfs) tf.setStyle(null); }
    private static void mark(TextField tf) { tf.setStyle("-fx-border-color: #d00000; -fx-border-width: 1.2;"); }
}




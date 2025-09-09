package Ass1;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class MedicineFormDialog {

    public static Optional<Medicine> show() {
        Dialog<Medicine> dialog = new Dialog<Medicine>();
        dialog.setTitle("Add New Medicine");
        dialog.setHeaderText("Fill in the details");

        ButtonType okType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        final TextField name = new TextField();
        final TextField manufacturer = new TextField();
        final TextField expiry = new TextField();
        final TextField cost = new TextField();
        final TextField count = new TextField();

        name.setPromptText("e.g., Paracetamol");
        manufacturer.setPromptText("Manufacturer");
        expiry.setPromptText("YYYY-MM-DD");
        cost.setPromptText("e.g., 1");
        count.setPromptText("e.g., 100");

        final Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #d00000; -fx-font-size: 12px;");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10); gp.setPadding(new Insets(10));
        gp.addRow(0, new Label("Name:"), name);
        gp.addRow(1, new Label("Manufacturer:"), manufacturer);
        gp.addRow(2, new Label("Expiry Date:"), expiry);
        gp.addRow(3, new Label("Cost per unit:"), cost);
        gp.addRow(4, new Label("Count:"), count);
        gp.add(errorLabel, 0, 5, 2, 1);

        Node addBtn = dialog.getDialogPane().lookupButton(okType);
        addBtn.setDisable(true);

        name.textProperty().addListener((o, ov, nv) -> validate(name, expiry, cost, count, errorLabel, addBtn));
        expiry.textProperty().addListener((o, ov, nv) -> validate(name, expiry, cost, count, errorLabel, addBtn));
        cost.textProperty().addListener((o, ov, nv) -> validate(name, expiry, cost, count, errorLabel, addBtn));
        count.textProperty().addListener((o, ov, nv) -> validate(name, expiry, cost, count, errorLabel, addBtn));

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                if (!validate(name, expiry, cost, count, errorLabel, addBtn)) return null;
                try {
                    // parse & check future/any date（如要限制必须未来，可加 isBefore 逻辑）
                    LocalDate.parse(expiry.getText().trim());
                    int costVal = Integer.parseInt(cost.getText().trim());
                    int countVal = Integer.parseInt(count.getText().trim());
                    if (costVal <= 0) { alert("Cost must be a positive integer"); return null; }
                    if (countVal <= 0) { alert("Count must be a positive integer"); return null; }
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

        return dialog.showAndWait();
    }

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

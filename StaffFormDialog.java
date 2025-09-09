package Ass1;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class StaffFormDialog {

    public static Optional<Staff> show() {
        Dialog<Staff> dialog = new Dialog<Staff>();
        dialog.setTitle("Add New Staff");
        dialog.setHeaderText("Fill in the details");

        ButtonType okType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        final TextField id = new TextField();
        final TextField name = new TextField();
        final TextField designation = new TextField();
        final TextField sex = new TextField();
        final TextField salary = new TextField();

        id.setPromptText("e.g., S2001");
        name.setPromptText("Name");
        designation.setPromptText("Designation");
        sex.setPromptText("M/F");
        salary.setPromptText("e.g., 3500");

        final Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #d00000; -fx-font-size: 12px;");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10); gp.setPadding(new Insets(10));
        gp.addRow(0, new Label("ID:"), id);
        gp.addRow(1, new Label("Name:"), name);
        gp.addRow(2, new Label("Designation:"), designation);
        gp.addRow(3, new Label("Sex:"), sex);
        gp.addRow(4, new Label("Salary:"), salary);
        gp.add(errorLabel, 0, 5, 2, 1);

        Node addBtn = dialog.getDialogPane().lookupButton(okType);
        addBtn.setDisable(true);

        id.textProperty().addListener((o, ov, nv) -> validate(id, name, salary, errorLabel, addBtn));
        name.textProperty().addListener((o, ov, nv) -> validate(id, name, salary, errorLabel, addBtn));
        salary.textProperty().addListener((o, ov, nv) -> validate(id, name, salary, errorLabel, addBtn));

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                if (!validate(id, name, salary, errorLabel, addBtn)) return null;
                int sal;
                try {
                    sal = Integer.parseInt(salary.getText().trim());
                    if (sal <= 0) {
                        new Alert(Alert.AlertType.ERROR, "Salary must be a positive integer", ButtonType.OK).showAndWait();
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Salary must be a number", ButtonType.OK).showAndWait();
                    return null;
                }
                return new Staff(
                        id.getText().trim(),
                        name.getText().trim(),
                        designation.getText().trim(),
                        sex.getText().trim(),
                        sal
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static boolean validate(TextField id, TextField name, TextField salary,
                                    Label errorLabel, Node addBtn) {
        clearError(id); clearError(name); clearError(salary);
        String err = null;

        if (id.getText().trim().isEmpty()) { err = "ID is required."; setError(id); }
        else if (name.getText().trim().isEmpty()) { err = "Name is required."; setError(name); }
        else if (salary.getText().trim().isEmpty()) { err = "Salary is required."; setError(salary); }
        else {
            try {
                int sal = Integer.parseInt(salary.getText().trim());
                if (sal <= 0) { err = "Salary must be a positive integer."; setError(salary); }
            } catch (NumberFormatException e) { err = "Salary must be a number."; setError(salary); }
        }

        errorLabel.setText(err == null ? "" : err);
        addBtn.setDisable(err != null);
        return err == null;
    }

    private static void setError(TextField tf) {
        tf.setStyle("-fx-border-color: #d00000; -fx-border-width: 1.2;");
    }
    private static void clearError(TextField tf) { tf.setStyle(null); }
}

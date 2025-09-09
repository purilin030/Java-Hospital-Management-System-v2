package Ass1;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class PatientFormDialog {

    public static Optional<Patient> show() {
        Dialog<Patient> dialog = new Dialog<Patient>();
        dialog.setTitle("Add New Patient");
        dialog.setHeaderText("Fill in the details");

        ButtonType okType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        final TextField id = new TextField();
        final TextField name = new TextField();
        final TextField disease = new TextField();
        final ComboBox<String> sex = new ComboBox<String>();
        final ComboBox<String> admin = new ComboBox<String>();
        final TextField age = new TextField();

        id.setPromptText("e.g., P1001");
        name.setPromptText("Name");
        disease.setPromptText("Disease");
        sex.getItems().addAll("M", "F");
        sex.setPromptText("Select");
        admin.getItems().addAll("Admitted", "Discharged");
        admin.setPromptText("Select");
        age.setPromptText("e.g., 30");

        final Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #d00000; -fx-font-size: 12px;");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10); gp.setPadding(new Insets(10));
        gp.addRow(0, new Label("ID:"), id);
        gp.addRow(1, new Label("Name:"), name);
        gp.addRow(2, new Label("Disease:"), disease);
        gp.addRow(3, new Label("Sex:"), sex);
        gp.addRow(4, new Label("Admission Status:"), admin);
        gp.addRow(5, new Label("Age:"), age);
        gp.add(errorLabel, 0, 6, 2, 1);

        Node addBtn = dialog.getDialogPane().lookupButton(okType);
        addBtn.setDisable(true);

        // 实时校验
        id.textProperty().addListener((o, ov, nv) -> validate(id, name, age, sex, admin, errorLabel, addBtn));
        name.textProperty().addListener((o, ov, nv) -> validate(id, name, age, sex, admin, errorLabel, addBtn));
        age.textProperty().addListener((o, ov, nv) -> validate(id, name, age, sex, admin, errorLabel, addBtn));
        sex.valueProperty().addListener((o, ov, nv) -> validate(id, name, age, sex, admin, errorLabel, addBtn));
        admin.valueProperty().addListener((o, ov, nv) -> validate(id, name, age, sex, admin, errorLabel, addBtn));

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                if (!validate(id, name, age, sex, admin, errorLabel, addBtn)) return null;
                int ageVal;
                try {
                    ageVal = Integer.parseInt(age.getText().trim());
                    if (ageVal <= 0) {
                        new Alert(Alert.AlertType.ERROR, "Age must be a positive integer", ButtonType.OK).showAndWait();
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Age must be a number", ButtonType.OK).showAndWait();
                    return null;
                }
                return new Patient(
                        id.getText().trim(),
                        name.getText().trim(),
                        disease.getText().trim(),
                        sex.getValue(),
                        admin.getValue(),
                        ageVal
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static boolean validate(TextField id, TextField name, TextField age,
                                    ComboBox<String> sex, ComboBox<String> admin,
                                    Label errorLabel, Node addBtn) {
        clearError(id); clearError(name); clearError(age);
        sex.setStyle(null); admin.setStyle(null);

        String err = null;
        if (id.getText().trim().isEmpty()) { err = "ID is required."; setError(id); }
        else if (name.getText().trim().isEmpty()) { err = "Name is required."; setError(name); }
        else if (sex.getValue() == null) { err = "Sex is required."; sex.setStyle("-fx-border-color: #d00000;"); }
        else if (admin.getValue() == null) { err = "Admission status is required."; admin.setStyle("-fx-border-color: #d00000;"); }
        else if (age.getText().trim().isEmpty()) { err = "Age is required."; setError(age); }
        else {
            try {
                int a = Integer.parseInt(age.getText().trim());
                if (a <= 0) { err = "Age must be a positive integer."; setError(age); }
            } catch (NumberFormatException e) { err = "Age must be a number."; setError(age); }
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

package Ass1;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class LabFormDialog {

    public static Optional<Lab> show() {
        Dialog<Lab> dialog = new Dialog<Lab>();
        dialog.setTitle("Add New Lab");
        dialog.setHeaderText("Fill in the details");

        ButtonType okType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        final TextField lab = new TextField();
        final TextField cost = new TextField();

        lab.setPromptText("Lab name");
        cost.setPromptText("e.g., 500");

        final Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #d00000; -fx-font-size: 12px;");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10); gp.setPadding(new Insets(10));
        gp.addRow(0, new Label("Lab Name:"), lab);
        gp.addRow(1, new Label("Cost:"), cost);
        gp.add(errorLabel, 0, 2, 2, 1);

        Node addBtn = dialog.getDialogPane().lookupButton(okType);
        addBtn.setDisable(true);

        lab.textProperty().addListener((o, ov, nv) -> validate(lab, cost, errorLabel, addBtn));
        cost.textProperty().addListener((o, ov, nv) -> validate(lab, cost, errorLabel, addBtn));

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                if (!validate(lab, cost, errorLabel, addBtn)) return null;
                try {
                    int c = Integer.parseInt(cost.getText().trim());
                    if (c <= 0) { new Alert(Alert.AlertType.ERROR, "Cost must be a positive integer", ButtonType.OK).showAndWait(); return null; }
                    return new Lab(lab.getText().trim(), c);
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Cost must be a number", ButtonType.OK).showAndWait();
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static boolean validate(TextField lab, TextField cost, Label errorLabel, Node addBtn) {
        lab.setStyle(null); cost.setStyle(null);
        String err = null;

        if (lab.getText().trim().isEmpty()) { err = "Lab name is required."; lab.setStyle("-fx-border-color: #d00000;"); }
        else if (cost.getText().trim().isEmpty()) { err = "Cost is required."; cost.setStyle("-fx-border-color: #d00000;"); }
        else {
            try {
                int v = Integer.parseInt(cost.getText().trim());
                if (v <= 0) { err = "Cost must be a positive integer."; cost.setStyle("-fx-border-color: #d00000;"); }
            } catch (NumberFormatException e) { err = "Cost must be a number."; cost.setStyle("-fx-border-color: #d00000;"); }
        }

        errorLabel.setText(err == null ? "" : err);
        addBtn.setDisable(err != null);
        return err == null;
    }
}

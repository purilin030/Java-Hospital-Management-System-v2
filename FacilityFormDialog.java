package Ass1;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class FacilityFormDialog {

    public static Optional<facility> show() {
        Dialog<facility> dialog = new Dialog<facility>();
        dialog.setTitle("Add New Facility");
        dialog.setHeaderText("Fill in the details");

        ButtonType okType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        final TextField name = new TextField();
        name.setPromptText("e.g., ICU");

        final Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #d00000; -fx-font-size: 12px;");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10); gp.setPadding(new Insets(10));
        gp.addRow(0, new Label("Facility Name:"), name);
        gp.add(errorLabel, 0, 1, 2, 1);

        Node addBtn = dialog.getDialogPane().lookupButton(okType);
        addBtn.setDisable(true);

        name.textProperty().addListener((o, ov, nv) -> validate(name, errorLabel, addBtn));

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                if (!validate(name, errorLabel, addBtn)) return null;
                return new facility(name.getText().trim());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static boolean validate(TextField name, Label errorLabel, Node addBtn) {
        name.setStyle(null);
        String err = null;

        if (name.getText().trim().isEmpty()) { err = "Facility name is required."; name.setStyle("-fx-border-color: #d00000;"); }

        errorLabel.setText(err == null ? "" : err);
        addBtn.setDisable(err != null);
        return err == null;
    }
}

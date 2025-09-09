package Ass1;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class DoctorFormDialog {

    public static Optional<Doctor> show() {
        Dialog<Doctor> dialog = new Dialog<Doctor>();
        dialog.setTitle("Add New Doctor");
        dialog.setHeaderText("Fill in the details");

        ButtonType okType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        final TextField id = new TextField();
        final TextField name = new TextField();
        final TextField specialist = new TextField();
        final TextField workTime = new TextField();
        final TextField qualification = new TextField();
        final TextField room = new TextField();

        id.setPromptText("e.g., D251");
        name.setPromptText("Name");
        specialist.setPromptText("e.g., Cardiologist");
        workTime.setPromptText("e.g., 9AM-5PM");
        qualification.setPromptText("e.g., MBBS");
        room.setPromptText("e.g., 101");

        final Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #d00000; -fx-font-size: 12px;");

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(10));
        gp.addRow(0, new Label("ID:"), id);
        gp.addRow(1, new Label("Name:"), name);
        gp.addRow(2, new Label("Specialist:"), specialist);
        gp.addRow(3, new Label("Work Time:"), workTime);
        gp.addRow(4, new Label("Qualification:"), qualification);
        gp.addRow(5, new Label("Room No.:"), room);
        gp.add(errorLabel, 0, 6, 2, 1);

        Node addBtn = dialog.getDialogPane().lookupButton(okType);
        addBtn.setDisable(true);

        // 实时校验
        id.textProperty().addListener((o, ov, nv) -> validate(id, name, room, errorLabel, addBtn));
        name.textProperty().addListener((o, ov, nv) -> validate(id, name, room, errorLabel, addBtn));
        room.textProperty().addListener((o, ov, nv) -> validate(id, name, room, errorLabel, addBtn));

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                // 最后兜底检查
                if (!validate(id, name, room, errorLabel, addBtn)) return null;
                int roomNo;
                try { roomNo = Integer.parseInt(room.getText().trim()); }
                catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Room must be a positive integer", ButtonType.OK).showAndWait();
                    return null;
                }
                return new Doctor(
                        id.getText().trim(),
                        name.getText().trim(),
                        specialist.getText().trim(),
                        workTime.getText().trim(),
                        qualification.getText().trim(),
                        roomNo
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static boolean validate(TextField id, TextField name, TextField room,
                                    Label errorLabel, Node addBtn) {
    
        clearError(id); clearError(name); clearError(room);
        String err = null;

        if (id.getText().trim().isEmpty()) { err = "ID is required."; setError(id); }
        else if (name.getText().trim().isEmpty()) { err = "Name is required."; setError(name); }
        else if (room.getText().trim().isEmpty()) { err = "Room No. is required."; setError(room); }
        else {
            try {
                int r = Integer.parseInt(room.getText().trim());
                if (r <= 0) { err = "Room No. must be a positive integer."; setError(room); }
            } catch (NumberFormatException e) { err = "Room No. must be a number."; setError(room); }
        }

        errorLabel.setText(err == null ? "" : err);
        addBtn.setDisable(err != null);
        return err == null;
    }

    private static void setError(TextField tf) {
        tf.setStyle("-fx-border-color: #d00000; -fx-border-width: 1.2;");
    }
    private static void clearError(TextField tf) {
        tf.setStyle(null);
    }
}

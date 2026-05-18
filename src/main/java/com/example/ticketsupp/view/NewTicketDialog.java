package com.example.ticketsupp.view;

import com.example.ticketsupp.model.Ticket;
import com.example.ticketsupp.model.TicketPriority;
import com.example.ticketsupp.model.TicketStore;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Window;

public class NewTicketDialog extends Dialog<Ticket> {
    private final TicketStore ticketStore;
    private final TextField requesterNameField = new TextField();
    private final TextField requesterEmailField = new TextField();
    private final TextField titleField = new TextField();
    private final TextArea descriptionArea = new TextArea();
    private final ComboBox<TicketPriority> priorityComboBox = new ComboBox<>();
    private final ComboBox<String> assigneeComboBox = new ComboBox<>();

    public NewTicketDialog(Window owner, TicketStore ticketStore) {
        this.ticketStore = ticketStore;
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("New Support Ticket");
        setHeaderText("Create a customer support ticket");
        buildDialog();
    }

    private void buildDialog() {
        ButtonType createType = new ButtonType("Create Ticket", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createType, ButtonType.CANCEL);

        priorityComboBox.getItems().setAll(TicketPriority.values());
        priorityComboBox.setValue(TicketPriority.MEDIUM);
        assigneeComboBox.getItems().setAll(TicketStore.SUPPORT_STAFF);
        assigneeComboBox.setValue(Ticket.UNASSIGNED);

        descriptionArea.setPrefRowCount(5);
        descriptionArea.setWrapText(true);

        requesterNameField.setPromptText("Customer name");
        requesterEmailField.setPromptText("customer@example.com");
        titleField.setPromptText("Short issue summary");
        descriptionArea.setPromptText("Describe what the customer reported...");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(14));
        form.setStyle("-fx-background-color: #f8fafc;");

        addRow(form, 0, "Requester Name", requesterNameField);
        addRow(form, 1, "Requester Email", requesterEmailField);
        addRow(form, 2, "Title", titleField);
        addRow(form, 3, "Description", descriptionArea);
        addRow(form, 4, "Priority", priorityComboBox);
        addRow(form, 5, "Assignee", assigneeComboBox);

        getDialogPane().setContent(form);
        getDialogPane().setPrefWidth(520);

        Node createButton = getDialogPane().lookupButton(createType);
        createButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> !isFormValid(),
                requesterNameField.textProperty(),
                requesterEmailField.textProperty(),
                titleField.textProperty(),
                descriptionArea.textProperty(),
                priorityComboBox.valueProperty()
        ));

        createButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            try {
                Ticket ticket = new Ticket();
                ticket.setRequesterName(requesterNameField.getText().trim());
                ticket.setRequesterEmail(requesterEmailField.getText().trim());
                ticket.setTitle(titleField.getText().trim());
                ticket.setDescription(descriptionArea.getText().trim());
                ticket.setPriority(priorityComboBox.getValue());
                ticket.setAssignee(assigneeComboBox.getValue());

                Ticket savedTicket = ticketStore.addTicket(ticket);
                setResult(savedTicket);
                showAlert(Alert.AlertType.INFORMATION, "Ticket Created",
                        "Ticket " + savedTicket.getTicketCode() + " was created successfully.");
                close();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            } catch (RuntimeException ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
            }
        });
    }

    private void addRow(GridPane form, int row, String labelText, Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        form.add(label, 0, row);
        form.add(field, 1, row);
        GridPane.setFillWidth(field, true);
        field.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #cbd5e1; "
                + "-fx-background-color: white; -fx-padding: 8;");
    }

    private boolean isFormValid() {
        // Basic validation happens live so the Create button only enables for complete input.
        return !requesterNameField.getText().isBlank()
                && !requesterEmailField.getText().isBlank()
                && requesterEmailField.getText().contains("@")
                && !titleField.getText().isBlank()
                && !descriptionArea.getText().isBlank()
                && priorityComboBox.getValue() != null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.initOwner(getDialogPane().getScene().getWindow());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

package com.example.ticketsupp.view;

import com.example.ticketsupp.model.Ticket;
import com.example.ticketsupp.model.TicketPriority;
import com.example.ticketsupp.model.TicketStatus;
import com.example.ticketsupp.model.TicketStore;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Optional;

public class TicketDetailView extends BorderPane {
    private final TicketStore ticketStore;
    private final Runnable onDelete;

    private final Label codeLabel = new Label();
    private final Label requesterNameLabel = new Label();
    private final Label requesterEmailLabel = new Label();
    private final Label createdAtLabel = new Label();
    private final Label updatedAtLabel = new Label();
    private final Label closedAtLabel = new Label();
    private final TextField titleField = new TextField();
    private final TextArea descriptionArea = new TextArea();
    private final ComboBox<String> assigneeComboBox = new ComboBox<>();
    private final ComboBox<TicketPriority> priorityComboBox = new ComboBox<>();
    private final ComboBox<TicketStatus> statusComboBox = new ComboBox<>();
    private final Button saveButton = new Button("Save Changes");
    private final Button closeButton = new Button("Close Ticket");
    private final Button reopenButton = new Button("Reopen Ticket");
    private final Button deleteButton = new Button("Delete Ticket");

    private Ticket currentTicket;

    public TicketDetailView(TicketStore ticketStore, Runnable onDelete) {
        this.ticketStore = ticketStore;
        this.onDelete = onDelete;
        buildView();
        setTicket(null);
    }

    public void setTicket(Ticket ticket) {
        currentTicket = ticket;
        if (ticket == null) {
            showEmptyState();
        } else {
            populateTicket(ticket);
            showDetailForm();
        }
    }

    private void buildView() {
        setPrefWidth(390);
        setMinWidth(340);
        setPadding(new Insets(18));
        setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 0 1;");

        assigneeComboBox.getItems().setAll(TicketStore.SUPPORT_STAFF);
        priorityComboBox.getItems().setAll(TicketPriority.values());
        statusComboBox.getItems().setAll(TicketStatus.values());

        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(7);

        saveButton.setOnAction(event -> saveChanges());
        closeButton.setOnAction(event -> closeTicket());
        reopenButton.setOnAction(event -> reopenTicket());
        deleteButton.setOnAction(event -> deleteTicket());

        stylePrimaryButton(saveButton);
        styleWarningButton(closeButton);
        stylePrimaryButton(reopenButton);
        styleDangerButton(deleteButton);
    }

    private void showEmptyState() {
        Label empty = new Label("Select a ticket to view details.");
        empty.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16px;");
        setTop(null);
        setCenter(empty);
        BorderPane.setAlignment(empty, Pos.CENTER);
        setBottom(null);
    }

    private void showDetailForm() {
        Label heading = new Label("Ticket Details");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        codeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: bold;");

        VBox top = new VBox(4, heading, codeLabel);
        top.setPadding(new Insets(0, 0, 16, 0));

        GridPane meta = new GridPane();
        meta.setHgap(8);
        meta.setVgap(8);
        addMetaRow(meta, 0, "Requester", requesterNameLabel);
        addMetaRow(meta, 1, "Email", requesterEmailLabel);
        addMetaRow(meta, 2, "Created", createdAtLabel);
        addMetaRow(meta, 3, "Updated", updatedAtLabel);
        addMetaRow(meta, 4, "Closed", closedAtLabel);

        VBox form = new VBox(10,
                meta,
                inputBlock("Title", titleField),
                inputBlock("Description", descriptionArea),
                inputBlock("Assignee", assigneeComboBox),
                inputBlock("Priority", priorityComboBox),
                inputBlock("Status", statusComboBox)
        );
        form.setPadding(new Insets(0, 0, 16, 0));

        HBox stateButtons = new HBox(10, closeButton, reopenButton);
        HBox.setHgrow(closeButton, Priority.ALWAYS);
        HBox.setHgrow(reopenButton, Priority.ALWAYS);
        closeButton.setMaxWidth(Double.MAX_VALUE);
        reopenButton.setMaxWidth(Double.MAX_VALUE);

        VBox buttons = new VBox(10, saveButton, stateButtons, deleteButton);
        saveButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setMaxWidth(Double.MAX_VALUE);

        setTop(top);
        setCenter(form);
        setBottom(buttons);
        refreshActionButtons();
    }

    private void populateTicket(Ticket ticket) {
        codeLabel.setText(ticket.getTicketCode());
        requesterNameLabel.setText(ticket.getRequesterName());
        requesterEmailLabel.setText(ticket.getRequesterEmail());
        createdAtLabel.setText(ticket.getCreatedAt());
        updatedAtLabel.setText(ticket.getUpdatedAt());
        closedAtLabel.setText(ticket.getClosedAt() == null ? "-" : ticket.getClosedAt());
        titleField.setText(ticket.getTitle());
        descriptionArea.setText(ticket.getDescription());
        assigneeComboBox.setValue(ticket.getAssignee());
        priorityComboBox.setValue(ticket.getPriority());
        statusComboBox.setValue(ticket.getStatus());
    }

    private VBox inputBlock(String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        field.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #cbd5e1; "
                + "-fx-background-color: white; -fx-padding: 8;");
        return new VBox(5, label, field);
    }

    private void addMetaRow(GridPane grid, int row, String labelText, Label value) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");
        value.setStyle("-fx-text-fill: #334155;");
        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private void saveChanges() {
        if (currentTicket == null) {
            return;
        }

        try {
            Ticket edited = currentTicket.copy();
            edited.setTitle(titleField.getText().trim());
            edited.setDescription(descriptionArea.getText().trim());
            edited.setAssignee(assigneeComboBox.getValue());
            edited.setPriority(priorityComboBox.getValue());
            edited.setStatus(statusComboBox.getValue());

            ticketStore.updateTicket(edited);
            setTicket(edited);
            showAlert(Alert.AlertType.INFORMATION, "Saved", "Ticket changes were saved successfully.");
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        }
    }

    private void closeTicket() {
        if (currentTicket == null) {
            return;
        }

        try {
            Ticket edited = currentTicket.copy();
            ticketStore.closeTicket(edited);
            setTicket(edited);
            showAlert(Alert.AlertType.INFORMATION, "Ticket Closed", "Ticket was closed successfully.");
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "Business Rule", ex.getMessage());
        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        }
    }

    private void reopenTicket() {
        if (currentTicket == null) {
            return;
        }

        try {
            Ticket edited = currentTicket.copy();
            ticketStore.reopenTicket(edited);
            setTicket(edited);
            showAlert(Alert.AlertType.INFORMATION, "Ticket Reopened", "Ticket was reopened successfully.");
        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        }
    }

    private void deleteTicket() {
        if (currentTicket == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + currentTicket.getTicketCode() + "? This cannot be undone.",
                ButtonType.CANCEL, ButtonType.OK);
        confirm.initOwner(getWindow());
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);

        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            ticketStore.deleteTicket(currentTicket);
            setTicket(null);
            onDelete.run();
        }
    }

    private void refreshActionButtons() {
        boolean finished = currentTicket != null && currentTicket.isFinished();
        closeButton.setVisible(!finished);
        closeButton.setManaged(!finished);
        closeButton.setDisable(finished);
        reopenButton.setVisible(finished);
        reopenButton.setManaged(finished);
        reopenButton.setDisable(!finished);
    }

    private void stylePrimaryButton(Button button) {
        button.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 12; -fx-padding: 10 14;");
    }

    private void styleWarningButton(Button button) {
        button.setStyle("-fx-background-color: #f97316; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 12; -fx-padding: 10 14;");
    }

    private void styleDangerButton(Button button) {
        button.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 12; -fx-padding: 10 14;");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.initOwner(getWindow());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private Window getWindow() {
        return getScene() == null ? null : getScene().getWindow();
    }
}

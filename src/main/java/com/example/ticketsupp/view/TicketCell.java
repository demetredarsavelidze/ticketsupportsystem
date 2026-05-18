package com.example.ticketsupp.view;

import com.example.ticketsupp.model.Ticket;
import com.example.ticketsupp.model.TicketPriority;
import com.example.ticketsupp.model.TicketStatus;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TicketCell extends ListCell<Ticket> {
    private VBox card;

    @Override
    protected void updateItem(Ticket ticket, boolean empty) {
        super.updateItem(ticket, empty);
        if (empty || ticket == null) {
            card = null;
            setText(null);
            setGraphic(null);
            setStyle("-fx-background-color: transparent;");
            return;
        }

        Label title = new Label(ticket.getTitle());
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #172033;");

        Label code = new Label(ticket.getTicketCode());
        code.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(8, title, spacer, code);
        header.setFillHeight(true);

        Label requester = new Label(ticket.getRequesterName() + "  •  " + ticket.getRequesterEmail());
        requester.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        Label assignee = new Label("Assignee: " + ticket.getAssignee());
        assignee.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        Label priorityBadge = badge(ticket.getPriority().getDisplayName(), priorityColor(ticket.getPriority()));
        Label statusBadge = badge(ticket.getStatus().getDisplayName(), statusColor(ticket.getStatus()));
        Label created = new Label("Created: " + ticket.getCreatedAt());
        created.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        HBox footer = new HBox(8, priorityBadge, statusBadge, created);
        footer.setPadding(new Insets(4, 0, 0, 0));

        card = new VBox(7, header, requester, assignee, footer);
        card.setPadding(new Insets(14));
        card.setStyle(cardStyle(isSelected()));

        setText(null);
        setGraphic(card);
        setPadding(new Insets(5, 8, 5, 8));
        setStyle("-fx-background-color: transparent;");
    }

    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
        if (card != null) {
            card.setStyle(cardStyle(selected));
        }
    }

    private Label badge(String text, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                + "-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 8; "
                + "-fx-background-radius: 999;");
        return label;
    }

    private String cardStyle(boolean selected) {
        String border = selected ? "#2563eb" : "#e2e8f0";
        String background = selected ? "#eff6ff" : "#ffffff";
        return "-fx-background-color: " + background + ";"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-radius: 14;"
                + "-fx-border-width: 1.5;"
                + "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.06), 10, 0, 0, 2);";
    }

    private String priorityColor(TicketPriority priority) {
        return switch (priority) {
            case CRITICAL -> "#dc2626";
            case HIGH -> "#ea580c";
            case MEDIUM -> "#2563eb";
            case LOW -> "#16a34a";
        };
    }

    private String statusColor(TicketStatus status) {
        return switch (status) {
            case OPEN -> "#0284c7";
            case IN_PROGRESS -> "#7c3aed";
            case RESOLVED -> "#059669";
            case CLOSED -> "#475569";
        };
    }
}

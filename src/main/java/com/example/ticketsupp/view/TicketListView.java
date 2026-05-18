package com.example.ticketsupp.view;

import com.example.ticketsupp.model.Ticket;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TicketListView extends BorderPane {
    private final TextField searchField = new TextField();
    private final Button newTicketButton = new Button("+ New Ticket");
    private final ListView<Ticket> ticketList = new ListView<>();

    public TicketListView() {
        buildView();
    }

    public void setTickets(ObservableList<Ticket> tickets) {
        ticketList.setItems(tickets);
    }

    public StringProperty searchTextProperty() {
        return searchField.textProperty();
    }

    public ReadOnlyObjectProperty<Ticket> selectedTicketProperty() {
        return ticketList.getSelectionModel().selectedItemProperty();
    }

    public void setOnNewTicket(Runnable action) {
        newTicketButton.setOnAction(event -> action.run());
    }

    public void selectTicket(Ticket ticket) {
        ticketList.getSelectionModel().select(ticket);
        ticketList.scrollTo(ticket);
    }

    public void clearSelection() {
        ticketList.getSelectionModel().clearSelection();
    }

    private void buildView() {
        setPadding(new Insets(18));
        setStyle("-fx-background-color: #f8fafc;");

        Label title = new Label("Tickets");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        searchField.setPromptText("Search title, requester, email, or ticket code...");
        searchField.setStyle("-fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #cbd5e1; "
                + "-fx-padding: 10 12; -fx-background-color: white;");

        newTicketButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 12; -fx-padding: 10 16;");

        HBox actions = new HBox(10, searchField, newTicketButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        VBox header = new VBox(12, title, actions);
        header.setPadding(new Insets(0, 0, 12, 0));

        ticketList.setCellFactory(listView -> new TicketCell());
        ticketList.setPlaceholder(new Label("No tickets found."));
        ticketList.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

        setTop(header);
        setCenter(ticketList);
    }
}

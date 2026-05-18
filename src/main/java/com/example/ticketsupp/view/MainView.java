package com.example.ticketsupp.view;

import com.example.ticketsupp.model.Ticket;
import com.example.ticketsupp.model.TicketPriority;
import com.example.ticketsupp.model.TicketStatus;
import com.example.ticketsupp.model.TicketStore;

import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;

public class MainView extends BorderPane {
    private final TicketStore ticketStore;
    private final Stage stage;
    private final TicketListView ticketListView = new TicketListView();
    private final TicketDetailView ticketDetailView;
    private final DashboardView dashboardView;
    private final FilteredList<Ticket> filteredTickets;
    private final HBox ticketsWorkspace = new HBox();
    private final StackPane contentStack = new StackPane();

    private final Label totalStat = new Label("0");
    private final Label openStat = new Label("0");
    private final Label criticalStat = new Label("0");

    private TicketStatus statusFilter;
    private TicketPriority priorityFilter;
    private String assigneeFilter;

    public MainView(TicketStore ticketStore, Stage stage) {
        this.ticketStore = ticketStore;
        this.stage = stage;
        this.ticketDetailView = new TicketDetailView(ticketStore, ticketListView::clearSelection);
        this.dashboardView = new DashboardView(ticketStore);
        this.filteredTickets = new FilteredList<>(ticketStore.getTickets(), ticket -> true);
        buildView();
        wireEvents();
        updateLiveStats();
    }

    private void buildView() {
        setStyle("-fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-background-color: #f8fafc;");
        setLeft(createSidebar());

        ticketListView.setTickets(filteredTickets);
        HBox.setHgrow(ticketListView, Priority.ALWAYS);
        ticketsWorkspace.getChildren().setAll(ticketListView, ticketDetailView);

        contentStack.getChildren().setAll(ticketsWorkspace, dashboardView);
        setCenter(contentStack);
        showTicketsWorkspace();
    }

    private void wireEvents() {
        ticketListView.searchTextProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        ticketListView.selectedTicketProperty().addListener((observable, oldTicket, newTicket) ->
                ticketDetailView.setTicket(newTicket));
        ticketListView.setOnNewTicket(this::showNewTicketDialog);
        ticketStore.getTickets().addListener((ListChangeListener<Ticket>) change -> updateLiveStats());
    }

    private VBox createSidebar() {
        Label appTitle = new Label("SupportDesk");
        appTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button dashboardButton = sidebarButton("Dashboard");
        dashboardButton.setOnAction(event -> showDashboard());

        VBox statusSection = sidebarSection("Status Filters",
                filterButton("All Tickets", () -> {
                    statusFilter = null;
                    showTicketsWorkspace();
                    applyFilters();
                }),
                filterButton("Open", () -> setStatusFilter(TicketStatus.OPEN)),
                filterButton("In Progress", () -> setStatusFilter(TicketStatus.IN_PROGRESS)),
                filterButton("Resolved", () -> setStatusFilter(TicketStatus.RESOLVED)),
                filterButton("Closed", () -> setStatusFilter(TicketStatus.CLOSED))
        );

        VBox prioritySection = sidebarSection("Priority Filters",
                filterButton("Critical", () -> setPriorityFilter(TicketPriority.CRITICAL)),
                filterButton("High", () -> setPriorityFilter(TicketPriority.HIGH)),
                filterButton("Medium", () -> setPriorityFilter(TicketPriority.MEDIUM)),
                filterButton("Low", () -> setPriorityFilter(TicketPriority.LOW))
        );

        VBox staffSection = sidebarSection("Support Staff Filters",
                filterButton("Unassigned", () -> setAssigneeFilter(Ticket.UNASSIGNED)),
                filterButton("Alice Johnson", () -> setAssigneeFilter("Alice Johnson")),
                filterButton("Bob Smith", () -> setAssigneeFilter("Bob Smith")),
                filterButton("Carol White", () -> setAssigneeFilter("Carol White")),
                filterButton("David Lee", () -> setAssigneeFilter("David Lee"))
        );

        Button clearButton = sidebarButton("Clear Filters");
        clearButton.setStyle("-fx-background-color: #e0f2fe; -fx-text-fill: #075985; -fx-font-weight: bold; "
                + "-fx-background-radius: 12; -fx-padding: 10 12;");
        clearButton.setOnAction(event -> {
            clearFilters();
            showTicketsWorkspace();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox stats = new VBox(6,
                liveStat("Total", totalStat),
                liveStat("Open", openStat),
                liveStat("Critical", criticalStat)
        );
        stats.setPadding(new Insets(12));
        stats.setStyle("-fx-background-color: rgba(255,255,255,0.10); -fx-background-radius: 14;");

        VBox sidebar = new VBox(18, appTitle, dashboardButton, statusSection, prioritySection,
                staffSection, clearButton, spacer, stats);
        sidebar.setPadding(new Insets(22, 18, 18, 18));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #0f172a, #1e293b);");
        return sidebar;
    }

    private VBox sidebarSection(String title, Button... buttons) {
        Label label = new Label(title);
        label.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 12px; -fx-font-weight: bold;");
        VBox section = new VBox(7);
        section.getChildren().add(label);
        section.getChildren().addAll(buttons);
        return section;
    }

    private Button filterButton(String text, Runnable action) {
        Button button = sidebarButton(text);
        button.setOnAction(event -> {
            action.run();
            showTicketsWorkspace();
            applyFilters();
        });
        return button;
    }

    private Button sidebarButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 10 12;");
        return button;
    }

    private HBox liveStat(String labelText, Label valueLabel) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #cbd5e1;");
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return new HBox(8, label, spacer, valueLabel);
    }

    private void showNewTicketDialog() {
        NewTicketDialog dialog = new NewTicketDialog(stage, ticketStore);
        dialog.showAndWait().ifPresent(ticket -> {
            clearFilters();
            showTicketsWorkspace();
            ticketListView.selectTicket(ticket);
        });
    }

    private void setStatusFilter(TicketStatus status) {
        statusFilter = status;
    }

    private void setPriorityFilter(TicketPriority priority) {
        priorityFilter = priority;
    }

    private void setAssigneeFilter(String assignee) {
        assigneeFilter = assignee;
    }

    private void clearFilters() {
        statusFilter = null;
        priorityFilter = null;
        assigneeFilter = null;
        ticketListView.searchTextProperty().set("");
        applyFilters();
    }

    private void applyFilters() {
        String query = ticketListView.searchTextProperty().get() == null
                ? ""
                : ticketListView.searchTextProperty().get().trim().toLowerCase(Locale.ROOT);

        filteredTickets.setPredicate(ticket -> {
            boolean matchesSearch = query.isBlank()
                    || contains(ticket.getTitle(), query)
                    || contains(ticket.getRequesterName(), query)
                    || contains(ticket.getRequesterEmail(), query)
                    || contains(ticket.getTicketCode(), query);
            boolean matchesStatus = statusFilter == null || ticket.getStatus() == statusFilter;
            boolean matchesPriority = priorityFilter == null || ticket.getPriority() == priorityFilter;
            boolean matchesAssignee = assigneeFilter == null || assigneeFilter.equals(ticket.getAssignee());
            return matchesSearch && matchesStatus && matchesPriority && matchesAssignee;
        });
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private void showDashboard() {
        dashboardView.setVisible(true);
        dashboardView.setManaged(true);
        ticketsWorkspace.setVisible(false);
        ticketsWorkspace.setManaged(false);
    }

    private void showTicketsWorkspace() {
        ticketsWorkspace.setVisible(true);
        ticketsWorkspace.setManaged(true);
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
    }

    private void updateLiveStats() {
        totalStat.setText(String.valueOf(ticketStore.getTickets().size()));
        openStat.setText(String.valueOf(ticketStore.countByStatus(TicketStatus.OPEN)));
        criticalStat.setText(String.valueOf(ticketStore.countByPriority(TicketPriority.CRITICAL)));
    }
}

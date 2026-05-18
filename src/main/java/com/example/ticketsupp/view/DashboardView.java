package com.example.ticketsupp.view;

import com.example.ticketsupp.model.Ticket;
import com.example.ticketsupp.model.TicketPriority;
import com.example.ticketsupp.model.TicketStatus;
import com.example.ticketsupp.model.TicketStore;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.EnumMap;
import java.util.Map;

public class DashboardView extends ScrollPane {
    private final TicketStore ticketStore;
    private final Map<String, Label> statLabels = new java.util.HashMap<>();
    private final PieChart statusChart = new PieChart();
    private final BarChart<String, Number> priorityChart;
    private final VBox recentTicketsBox = new VBox(8);

    public DashboardView(TicketStore ticketStore) {
        this.ticketStore = ticketStore;
        CategoryAxis priorityAxis = new CategoryAxis();
        NumberAxis countAxis = new NumberAxis();
        priorityChart = new BarChart<>(priorityAxis, countAxis);
        buildView();
        ticketStore.getTickets().addListener((ListChangeListener<Ticket>) change -> refresh());
        refresh();
    }

    private void buildView() {
        setFitToWidth(true);
        setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc;");

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Button demoButton = new Button("Load Demo Data");
        demoButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 12; -fx-padding: 10 16;");
        demoButton.setOnAction(event -> loadDemoData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(12, title, spacer, demoButton);

        GridPane stats = new GridPane();
        stats.setHgap(12);
        stats.setVgap(12);
        stats.add(statCard("Total Tickets", "total"), 0, 0);
        stats.add(statCard("Open Tickets", TicketStatus.OPEN.name()), 1, 0);
        stats.add(statCard("In Progress Tickets", TicketStatus.IN_PROGRESS.name()), 2, 0);
        stats.add(statCard("Resolved Tickets", TicketStatus.RESOLVED.name()), 0, 1);
        stats.add(statCard("Closed Tickets", TicketStatus.CLOSED.name()), 1, 1);
        stats.add(statCard("Critical Tickets", TicketPriority.CRITICAL.name()), 2, 1);

        statusChart.setTitle("Tickets by Status");
        statusChart.setLabelsVisible(true);
        statusChart.setLegendVisible(true);
        statusChart.setMinHeight(320);
        statusChart.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 14;");

        priorityChart.setTitle("Tickets by Priority");
        priorityChart.setLegendVisible(false);
        priorityChart.setAnimated(false);
        priorityChart.setMinHeight(320);
        priorityChart.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 14;");

        HBox charts = new HBox(14, statusChart, priorityChart);
        HBox.setHgrow(statusChart, Priority.ALWAYS);
        HBox.setHgrow(priorityChart, Priority.ALWAYS);

        Label recentTitle = new Label("Recent Tickets");
        recentTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        VBox recentSection = new VBox(10, recentTitle, recentTicketsBox);
        recentSection.setPadding(new Insets(16));
        recentSection.setStyle("-fx-background-color: white; -fx-background-radius: 16;");

        VBox content = new VBox(18, header, stats, charts, recentSection);
        content.setPadding(new Insets(24));
        setContent(content);
    }

    private VBox statCard(String title, String key) {
        Label value = new Label("0");
        value.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");
        statLabels.put(key, value);

        Label label = new Label(title);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569; -fx-font-weight: bold;");

        VBox card = new VBox(6, value, label);
        card.setPadding(new Insets(16));
        card.setMinWidth(190);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; "
                + "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.06), 10, 0, 0, 2);");
        return card;
    }

    private void refresh() {
        // Dashboard values are rebuilt from the ObservableList after each create/update/delete operation.
        statLabels.get("total").setText(String.valueOf(ticketStore.getTickets().size()));
        for (TicketStatus status : TicketStatus.values()) {
            statLabels.get(status.name()).setText(String.valueOf(ticketStore.countByStatus(status)));
        }
        statLabels.get(TicketPriority.CRITICAL.name()).setText(String.valueOf(ticketStore.countByPriority(TicketPriority.CRITICAL)));

        Map<TicketStatus, Integer> statusCounts = new EnumMap<>(TicketStatus.class);
        for (TicketStatus status : TicketStatus.values()) {
            statusCounts.put(status, ticketStore.countByStatus(status));
        }
        statusChart.getData().setAll(statusCounts.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey().getDisplayName(), entry.getValue()))
                .toList());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (TicketPriority priority : TicketPriority.values()) {
            series.getData().add(new XYChart.Data<>(priority.getDisplayName(), ticketStore.countByPriority(priority)));
        }
        priorityChart.getData().setAll(series);

        recentTicketsBox.getChildren().clear();
        if (ticketStore.getTickets().isEmpty()) {
            Label empty = new Label("No recent tickets yet.");
            empty.setStyle("-fx-text-fill: #64748b;");
            recentTicketsBox.getChildren().add(empty);
        } else {
            for (Ticket ticket : ticketStore.getRecentTickets(5)) {
                Label item = new Label(ticket.getTicketCode() + "  •  " + ticket.getTitle()
                        + "  •  " + ticket.getStatus().getDisplayName());
                item.setStyle("-fx-text-fill: #334155; -fx-padding: 8 0;");
                recentTicketsBox.getChildren().add(item);
            }
        }
    }

    private void loadDemoData() {
        boolean inserted = ticketStore.loadDemoData();
        if (inserted) {
            showAlert(Alert.AlertType.INFORMATION, "Demo Data Loaded", "Demo tickets were loaded successfully.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Demo Data", "Demo data already exists.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        Window window = getScene() == null ? null : getScene().getWindow();
        if (window != null) {
            alert.initOwner(window);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

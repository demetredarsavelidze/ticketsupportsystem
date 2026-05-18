package com.example.ticketsupp.model;

import com.example.ticketsupp.db.TicketDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TicketStore {
    public static final List<String> SUPPORT_STAFF = List.of(
            Ticket.UNASSIGNED,
            "Alice Johnson",
            "Bob Smith",
            "Carol White",
            "David Lee"
    );

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final TicketDAO ticketDAO;
    private final ObservableList<Ticket> tickets = FXCollections.observableArrayList();

    public TicketStore() {
        this(new TicketDAO());
    }

    public TicketStore(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
        this.ticketDAO.createTable();
        loadTickets();
    }

    public ObservableList<Ticket> getTickets() {
        return tickets;
    }

    public void loadTickets() {
        // Reloading from the DAO keeps the ObservableList synchronized with persistent SQLite data.
        tickets.setAll(ticketDAO.getAllTickets());
    }

    public Ticket addTicket(Ticket ticket) {
        prepareNewTicket(ticket);
        validateTicket(ticket);
        ticketDAO.insertTicket(ticket);
        tickets.add(0, ticket);
        return ticket;
    }

    public void updateTicket(Ticket ticket) {
        validateTicket(ticket);
        applyClosedAtRule(ticket);
        ticket.setUpdatedAt(now());
        ticketDAO.updateTicket(ticket);
        replaceTicketInList(ticket);
    }

    public void deleteTicket(Ticket ticket) {
        if (ticket == null) {
            return;
        }
        deleteTicket(ticket.getId());
    }

    public void deleteTicket(int id) {
        ticketDAO.deleteTicket(id);
        tickets.removeIf(ticket -> ticket.getId() == id);
    }

    public void closeTicket(Ticket ticket) {
        ensureCanFinish(ticket);
        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setUpdatedAt(now());
        ticket.setClosedAt(ticket.getUpdatedAt());
        ticketDAO.updateTicket(ticket);
        replaceTicketInList(ticket);
    }

    public void reopenTicket(Ticket ticket) {
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setUpdatedAt(now());
        ticket.setClosedAt(null);
        ticketDAO.updateTicket(ticket);
        replaceTicketInList(ticket);
    }

    public boolean loadDemoData() {
        // Demo data is intentionally inserted only into an empty table to preserve user-created tickets.
        boolean inserted = ticketDAO.insertDemoDataIfEmpty();
        if (inserted) {
            loadTickets();
        }
        return inserted;
    }

    public int countByStatus(TicketStatus status) {
        return (int) tickets.stream()
                .filter(ticket -> ticket.getStatus() == status)
                .count();
    }

    public int countByPriority(TicketPriority priority) {
        return (int) tickets.stream()
                .filter(ticket -> ticket.getPriority() == priority)
                .count();
    }

    public List<Ticket> getRecentTickets(int limit) {
        return tickets.stream()
                .sorted(Comparator.comparingInt(Ticket::getId).reversed())
                .limit(limit)
                .toList();
    }

    public static String now() {
        return DATE_FORMATTER.format(LocalDateTime.now());
    }

    public static String generateTicketCode() {
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
        return "TKT-" + code;
    }

    private void prepareNewTicket(Ticket ticket) {
        String timestamp = now();
        ticket.setId(0);
        ticket.setTicketCode(generateTicketCode());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(timestamp);
        ticket.setUpdatedAt(timestamp);
        ticket.setClosedAt(null);
        if (ticket.getAssignee() == null || ticket.getAssignee().isBlank()) {
            ticket.setAssignee(Ticket.UNASSIGNED);
        }
    }

    private void validateTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket is required.");
        }
        if (isBlank(ticket.getRequesterName())) {
            throw new IllegalArgumentException("Requester name cannot be empty.");
        }
        if (isBlank(ticket.getRequesterEmail())) {
            throw new IllegalArgumentException("Requester email cannot be empty.");
        }
        if (!ticket.getRequesterEmail().contains("@")) {
            throw new IllegalArgumentException("Requester email must contain @.");
        }
        if (isBlank(ticket.getTitle())) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        if (isBlank(ticket.getDescription())) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }
        if (ticket.getPriority() == null) {
            throw new IllegalArgumentException("Priority must be selected.");
        }
        if (ticket.getStatus() == null) {
            throw new IllegalArgumentException("Status must be selected.");
        }
        if (ticket.getStatus().isFinished()) {
            ensureCanFinish(ticket);
        }
    }

    private void ensureCanFinish(Ticket ticket) {
        // Business rule: support staff must be assigned before a ticket can be resolved or closed.
        if (ticket == null || !ticket.isAssigned()) {
            throw new IllegalArgumentException("Please assign this ticket before closing or resolving it.");
        }
    }

    private void applyClosedAtRule(Ticket ticket) {
        // CLOSED and RESOLVED retain a closed_at timestamp; active statuses clear it when reopened.
        if (ticket.getStatus().isFinished()) {
            if (isBlank(ticket.getClosedAt())) {
                ticket.setClosedAt(now());
            }
        } else {
            ticket.setClosedAt(null);
        }
    }

    private void replaceTicketInList(Ticket ticket) {
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getId() == ticket.getId()) {
                tickets.set(i, ticket);
                return;
            }
        }
        tickets.add(0, ticket);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

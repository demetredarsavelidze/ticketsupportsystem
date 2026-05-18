package com.example.ticketsupp.db;

import com.example.ticketsupp.model.Ticket;
import com.example.ticketsupp.model.TicketPriority;
import com.example.ticketsupp.model.TicketStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void createTable() {
        // Kept public for the project requirement; Database.initialize() uses the same table definition.
        Database.initialize();
    }

    public void insertTicket(Ticket ticket) {
        String sql = """
                INSERT INTO tickets (
                    ticket_code, title, description, requester_name, requester_email,
                    assignee, priority, status, created_at, updated_at, closed_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindTicket(statement, ticket);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    ticket.setId(keys.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not insert ticket.", ex);
        }
    }

    public void updateTicket(Ticket ticket) {
        String sql = """
                UPDATE tickets
                SET ticket_code = ?, title = ?, description = ?, requester_name = ?,
                    requester_email = ?, assignee = ?, priority = ?, status = ?,
                    created_at = ?, updated_at = ?, closed_at = ?
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindTicket(statement, ticket);
            statement.setInt(12, ticket.getId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not update ticket.", ex);
        }
    }

    public void deleteTicket(int id) {
        String sql = "DELETE FROM tickets WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not delete ticket.", ex);
        }
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets ORDER BY id DESC";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tickets.add(mapTicket(resultSet));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not load tickets.", ex);
        }

        return tickets;
    }

    public int countTickets() {
        String sql = "SELECT COUNT(*) FROM tickets";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not count tickets.", ex);
        }
    }

    public boolean isEmpty() {
        return countTickets() == 0;
    }

    public boolean insertDemoDataIfEmpty() {
        if (!isEmpty()) {
            return false;
        }

        List<Ticket> demoTickets = createDemoTickets();
        for (Ticket ticket : demoTickets) {
            insertTicket(ticket);
        }
        return true;
    }

    private void bindTicket(PreparedStatement statement, Ticket ticket) throws SQLException {
        // All user-entered values are bound parameters to avoid SQL injection.
        statement.setString(1, ticket.getTicketCode());
        statement.setString(2, ticket.getTitle());
        statement.setString(3, ticket.getDescription());
        statement.setString(4, ticket.getRequesterName());
        statement.setString(5, ticket.getRequesterEmail());
        statement.setString(6, ticket.getAssignee());
        statement.setString(7, ticket.getPriority().name());
        statement.setString(8, ticket.getStatus().name());
        statement.setString(9, ticket.getCreatedAt());
        statement.setString(10, ticket.getUpdatedAt());
        if (ticket.getClosedAt() == null || ticket.getClosedAt().isBlank()) {
            statement.setNull(11, Types.VARCHAR);
        } else {
            statement.setString(11, ticket.getClosedAt());
        }
    }

    private Ticket mapTicket(ResultSet resultSet) throws SQLException {
        return new Ticket(
                resultSet.getInt("id"),
                resultSet.getString("ticket_code"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getString("requester_name"),
                resultSet.getString("requester_email"),
                resultSet.getString("assignee"),
                TicketPriority.valueOf(resultSet.getString("priority")),
                TicketStatus.valueOf(resultSet.getString("status")),
                resultSet.getString("created_at"),
                resultSet.getString("updated_at"),
                resultSet.getString("closed_at")
        );
    }

    private List<Ticket> createDemoTickets() {
        LocalDateTime baseTime = LocalDateTime.now().minusDays(10);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(demoTicket("TKT-1001", "Cannot login", "Customer cannot access account after password reset.",
                "Ana Maisuradze", "ana@example.com", "Alice Johnson", TicketPriority.HIGH, TicketStatus.OPEN, baseTime.plusDays(1)));
        tickets.add(demoTicket("TKT-1002", "Payment failed", "Payment was charged but order was not completed.",
                "Giorgi Beridze", "giorgi@example.com", "Bob Smith", TicketPriority.CRITICAL, TicketStatus.IN_PROGRESS, baseTime.plusDays(2)));
        tickets.add(demoTicket("TKT-1003", "Password reset email not arriving", "Customer did not receive password reset email.",
                "Mariam Kapanadze", "mariam@example.com", "Carol White", TicketPriority.MEDIUM, TicketStatus.RESOLVED, baseTime.plusDays(3)));
        tickets.add(demoTicket("TKT-1004", "Dashboard charts not loading", "User reports that dashboard statistics are not visible.",
                "Nika Gelashvili", "nika@example.com", "David Lee", TicketPriority.HIGH, TicketStatus.OPEN, baseTime.plusDays(4)));
        tickets.add(demoTicket("TKT-1005", "Request dark mode support", "Customer requested a dark mode option.",
                "Lika Tsereteli", "lika@example.com", "Alice Johnson", TicketPriority.LOW, TicketStatus.CLOSED, baseTime.plusDays(5)));
        tickets.add(demoTicket("TKT-1006", "CSV export produces corrupted file", "Exported CSV file does not open correctly.",
                "Saba K.", "saba@example.com", "Bob Smith", TicketPriority.HIGH, TicketStatus.RESOLVED, baseTime.plusDays(6)));
        tickets.add(demoTicket("TKT-1007", "Account locked after failed attempts", "Customer account was locked after multiple login attempts.",
                "Tekla M.", "tekla@example.com", "Carol White", TicketPriority.CRITICAL, TicketStatus.IN_PROGRESS, baseTime.plusDays(7)));
        tickets.add(demoTicket("TKT-1008", "Notification settings not saving", "Notification preferences reset after closing the app.",
                "Luka D.", "luka@example.com", "David Lee", TicketPriority.MEDIUM, TicketStatus.OPEN, baseTime.plusDays(8)));
        tickets.add(demoTicket("TKT-1009", "Wrong name displayed on profile", "Customer profile shows incorrect display name.",
                "Salome B.", "salome@example.com", "Alice Johnson", TicketPriority.LOW, TicketStatus.RESOLVED, baseTime.plusDays(9)));
        tickets.add(demoTicket("TKT-1010", "App freezes when opening reports", "Application freezes when the reports page is opened.",
                "Irakli T.", "irakli@example.com", "Bob Smith", TicketPriority.HIGH, TicketStatus.CLOSED, baseTime.plusDays(10)));
        return tickets;
    }

    private Ticket demoTicket(String code, String title, String description, String requesterName, String requesterEmail,
                              String assignee, TicketPriority priority, TicketStatus status, LocalDateTime createdTime) {
        String createdAt = DATE_FORMATTER.format(createdTime);
        String updatedAt = DATE_FORMATTER.format(createdTime.plusHours(3));
        String closedAt = status.isFinished() ? DATE_FORMATTER.format(createdTime.plusHours(4)) : null;

        return new Ticket(code, title, description, requesterName, requesterEmail, assignee,
                priority, status, createdAt, updatedAt, closedAt);
    }
}

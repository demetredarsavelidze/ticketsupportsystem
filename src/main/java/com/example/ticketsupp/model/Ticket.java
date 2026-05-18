package com.example.ticketsupp.model;

public class Ticket {
    public static final String UNASSIGNED = "Unassigned";

    private int id;
    private String ticketCode;
    private String title;
    private String description;
    private String requesterName;
    private String requesterEmail;
    private String assignee;
    private TicketPriority priority;
    private TicketStatus status;
    private String createdAt;
    private String updatedAt;
    private String closedAt;

    public Ticket() {
    }

    public Ticket(String ticketCode, String title, String description, String requesterName,
                  String requesterEmail, String assignee, TicketPriority priority,
                  TicketStatus status, String createdAt, String updatedAt, String closedAt) {
        this(0, ticketCode, title, description, requesterName, requesterEmail, assignee,
                priority, status, createdAt, updatedAt, closedAt);
    }

    public Ticket(int id, String ticketCode, String title, String description, String requesterName,
                  String requesterEmail, String assignee, TicketPriority priority,
                  TicketStatus status, String createdAt, String updatedAt, String closedAt) {
        this.id = id;
        this.ticketCode = ticketCode;
        this.title = title;
        this.description = description;
        this.requesterName = requesterName;
        this.requesterEmail = requesterEmail;
        this.assignee = assignee;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.closedAt = closedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }

    public boolean isAssigned() {
        return assignee != null && !UNASSIGNED.equals(assignee);
    }

    public boolean isFinished() {
        return status != null && status.isFinished();
    }

    public Ticket copy() {
        return new Ticket(id, ticketCode, title, description, requesterName, requesterEmail,
                assignee, priority, status, createdAt, updatedAt, closedAt);
    }

    @Override
    public String toString() {
        return ticketCode + " - " + title;
    }
}

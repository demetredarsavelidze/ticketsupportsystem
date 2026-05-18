package com.example.ticketsupp.model;

public enum TicketStatus {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    CLOSED("Closed");

    private final String displayName;

    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isFinished() {
        return this == RESOLVED || this == CLOSED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

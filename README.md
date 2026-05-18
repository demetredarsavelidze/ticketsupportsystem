# Customer Support Ticket Management System

## Purpose

Customer Support Ticket Management System is a JavaFX desktop application for an internal support admin. When a customer reports a problem by phone, email, or in person, the admin can create a ticket, assign support staff, set priority, track status, and close or reopen the ticket.

This is not a web app and does not include customer login, a customer portal, authentication, chat, email sending, or file uploads.

## Technologies Used

- Java 17
- JavaFX
- Maven
- SQLite
- JDBC

## How to Run

From the project root, run:

```bash
mvn clean javafx:run
```

In IntelliJ IDEA, import the folder as a Maven project, wait for Maven dependencies to load, and run:

```text
com.example.ticketsupp.Launcher
```

The app creates a local SQLite database file named `tickets.db` automatically when it starts.

## Main Features

- Three-column JavaFX support desk layout
  - Left sidebar with Dashboard, status filters, priority filters, support staff filters, clear filters, and live stats
  - Center ticket list with real-time search and a `+ New Ticket` button
  - Right detail panel for viewing and editing the selected ticket
- CRUD operations backed by SQLite/JDBC
  - Create tickets
  - Read/list tickets
  - Update ticket details, assignee, priority, and status
  - Delete tickets with confirmation
- Search matches ticket title, requester name, requester email, and ticket code
- Filters work together for status, priority, and support staff
- Dashboard stat cards and JavaFX charts
  - Total Tickets
  - Open Tickets
  - In Progress Tickets
  - Resolved Tickets
  - Closed Tickets
  - Critical Tickets
  - Tickets by Status
  - Tickets by Priority
- Demo data loader with 10 sample tickets
- Persistence through `tickets.db`
- Validation and business rules
  - Required requester name, requester email, title, description, and priority
  - Requester email must contain `@`
  - New tickets are automatically `OPEN`
  - `updated_at` changes on every edit
  - `closed_at` is set only for `CLOSED` and `RESOLVED` tickets
  - Reopening a ticket clears `closed_at`
  - A ticket cannot be `CLOSED` or `RESOLVED` while assigned to `Unassigned`

## Database

The application uses a SQLite database file:

```text
tickets.db
```

It contains one table:

```text
tickets
```

Fields:

- `id INTEGER PRIMARY KEY AUTOINCREMENT`
- `ticket_code TEXT NOT NULL UNIQUE`
- `title TEXT NOT NULL`
- `description TEXT NOT NULL`
- `requester_name TEXT NOT NULL`
- `requester_email TEXT NOT NULL`
- `assignee TEXT NOT NULL`
- `priority TEXT NOT NULL`
- `status TEXT NOT NULL`
- `created_at TEXT NOT NULL`
- `updated_at TEXT NOT NULL`
- `closed_at TEXT NULL`

## Demo Data

Use the **Load Demo Data** button on the dashboard to insert 10 sample tickets. Demo data is inserted only when the database table is empty. If tickets already exist, the app shows:

```text
Demo data already exists.
```

## Project Structure

```text
src/main/java/com/example/ticketsupp/
├── App.java
├── db/
│   ├── Database.java
│   └── TicketDAO.java
├── model/
│   ├── Ticket.java
│   ├── TicketPriority.java
│   ├── TicketStatus.java
│   └── TicketStore.java
└── view/
    ├── MainView.java
    ├── DashboardView.java
    ├── TicketListView.java
    ├── TicketDetailView.java
    ├── TicketCell.java
    └── NewTicketDialog.java
```

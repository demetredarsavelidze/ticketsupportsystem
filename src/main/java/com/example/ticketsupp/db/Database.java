package com.example.ticketsupp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DATABASE_URL = "jdbc:sqlite:tickets.db";

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        // SQLite creates tickets.db automatically in the project folder when the first connection opens.
        Connection connection = DriverManager.getConnection(DATABASE_URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void initialize() {
        createTicketsTable();
    }

    private static void createTicketsTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS tickets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ticket_code TEXT NOT NULL UNIQUE,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    requester_name TEXT NOT NULL,
                    requester_email TEXT NOT NULL,
                    assignee TEXT NOT NULL,
                    priority TEXT NOT NULL,
                    status TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    closed_at TEXT NULL
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not create tickets database table.", ex);
        }
    }
}

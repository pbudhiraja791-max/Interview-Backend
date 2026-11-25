package com.interviewhub;

import com.interviewhub.database.DBConnection;
import com.interviewhub.ui.ConsoleUI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("                 INTERVIEWHUB                    ");
        System.out.println("  Multi-Interviewer Interview Management System  ");
        System.out.println("=================================================");
        System.out.println("Runtime Version : Java " + System.getProperty("java.version"));

        // Initialize and test database connectivity
        boolean dbConnected = DBConnection.testConnection();
        if (dbConnected) {
            System.out.println("Database Status : CONNECTED [MySQL 8.0]");
        } else {
            System.out.println("Database Status : DISCONNECTED (Configure src/main/resources/database.properties)");
        }

        // Single Scanner lifecycle managed over System.in
        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleUI ui = new ConsoleUI(scanner);
            ui.start();
        } catch (Exception e) {
            System.err.println("Fatal application error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cleanly shutdown MySQL Connector abandoned connection cleanup daemon
            try {
                com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
            } catch (Throwable ignored) {}
        }
    }
}

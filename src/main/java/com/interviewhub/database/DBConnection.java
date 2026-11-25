package com.interviewhub.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton database connection manager for InterviewHub.
 * Demonstrates JDBC DriverManager, Properties configuration, and robust connection lifecycle.
 */
public class DBConnection {

    private static final String DEFAULT_PROPERTIES_PATH = "src/main/resources/database.properties";
    private static final String[] ALTERNATIVE_CONFIG_PATHS = {
        "src/main/resources/database.properties",
        "database.properties"
    };
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/interviewhub_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static boolean initialized = false;

    static {
        loadConfiguration();
    }

    private DBConnection() {
        // Prevent external instantiation
    }

    /**
     * Loads database credentials from database.properties or environment variables.
     */
    public static synchronized void loadConfiguration() {
        Properties props = new Properties();
        File propFile = null;
        for (String configPath : ALTERNATIVE_CONFIG_PATHS) {
            File candidate = new File(configPath);
            if (candidate.exists()) {
                propFile = candidate;
                break;
            }
        }

        if (propFile != null && propFile.exists()) {
            try (InputStream input = new FileInputStream(propFile)) {
                props.load(input);
            } catch (IOException e) {
                System.err.println("[WARN] Failed to load " + propFile.getPath() + ": " + e.getMessage());
            }
        } else {
            // Attempt to load from classpath
            try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
                if (input != null) {
                    props.load(input);
                }
            } catch (IOException ignored) {}
        }

        dbUrl = props.getProperty("db.url", System.getenv().getOrDefault("DB_URL", DEFAULT_URL));
        dbUser = props.getProperty("db.user", System.getenv().getOrDefault("DB_USER", DEFAULT_USER));
        dbPassword = props.getProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASS));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            initialized = true;
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Updates runtime credentials manually (e.g. from user console input or tests).
     */
    public static synchronized void configure(String url, String user, String password) {
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
    }

    /**
     * Obtains a fresh database connection.
     * Callers must close it or manage it within try-with-resources.
     *
     * @return active java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            loadConfiguration();
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Tests if the database connection succeeds.
     *
     * @return true if connection is valid, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }
}

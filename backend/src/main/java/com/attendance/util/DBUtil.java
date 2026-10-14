package com.attendance.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection utility.
 * Loads configuration from db.properties or environment variables.
 */
public class DBUtil {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            loadConfig();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    private static void loadConfig() {
        // Try environment variables first
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPassword = System.getenv("DB_PASSWORD");

        if (envUrl != null && envUser != null) {
            URL = envUrl;
            USER = envUser;
            PASSWORD = envPassword != null ? envPassword : "";
            return;
        }

        // Fall back to properties file
        try (InputStream input = DBUtil.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/attendance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                USER = props.getProperty("db.user", "root");
                PASSWORD = props.getProperty("db.password", "");
            } else {
                // Defaults
                URL = "jdbc:mysql://localhost:3306/attendance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                USER = "root";
                PASSWORD = "";
            }
        } catch (IOException e) {
            URL = "jdbc:mysql://localhost:3306/attendance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            USER = "root";
            PASSWORD = "";
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}

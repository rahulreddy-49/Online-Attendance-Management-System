package com.attendance.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Provisions the database on first boot: creates the tables defined in
 * {@code schema.sql} and loads the reference/demo data from {@code seed.sql}.
 *
 * <p>Both scripts are idempotent and the whole step is skipped when a
 * connection cannot be established, so the web application still starts while
 * a remote database is waking up. Database-name directives ({@code CREATE
 * DATABASE}/{@code USE}) are ignored because the target database is already
 * selected by the JDBC connection URL.</p>
 */
@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private static final int MAX_ATTEMPTS = 8;
    private static final long RETRY_DELAY_MS = 3000L;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Connection conn = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS && conn == null; attempt++) {
            try {
                conn = DBUtil.getConnection();
            } catch (Exception e) {
                sce.getServletContext().log(
                        "DB init: connection attempt " + attempt + "/" + MAX_ATTEMPTS + " failed: " + e.getMessage());
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        if (conn == null) {
            sce.getServletContext().log("DB init: database unavailable, skipping schema initialization.");
            return;
        }

        try (Connection c = conn) {
            if (tableExists(c, "users")) {
                sce.getServletContext().log("DB init: schema already present, skipping.");
                return;
            }
            sce.getServletContext().log("DB init: provisioning schema...");
            runScript(c, "schema.sql");
            runScript(c, "seed.sql");
            sce.getServletContext().log("DB init: schema provisioning complete.");
        } catch (Exception e) {
            sce.getServletContext().log("DB init: failed - " + e.getMessage());
        }
    }

    private boolean tableExists(Connection conn, String table) {
        try (ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), null, table, null)) {
            if (rs.next()) {
                return true;
            }
        } catch (Exception ignored) {
            // Fall through to the direct probe below.
        }
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1 FROM " + table + " LIMIT 1")) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void runScript(Connection conn, String resource) throws Exception {
        String sql = readResource(resource);
        if (sql == null) {
            throw new IllegalStateException("Missing SQL resource on classpath: " + resource);
        }
        try (Statement st = conn.createStatement()) {
            for (String statement : splitStatements(sql)) {
                st.execute(statement);
            }
        }
    }

    private String readResource(String name) throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(name)) {
            if (in == null) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();
            }
        }
    }

    private List<String> splitStatements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String rawLine : sql.split("\n")) {
            String trimmed = rawLine.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }
            current.append(rawLine).append('\n');
            if (trimmed.endsWith(";")) {
                String stmt = current.toString().trim();
                stmt = stmt.substring(0, stmt.length() - 1).trim();
                if (!stmt.isEmpty() && !isDatabaseDirective(stmt)) {
                    statements.add(stmt);
                }
                current.setLength(0);
            }
        }
        String tail = current.toString().trim();
        if (!tail.isEmpty() && !isDatabaseDirective(tail)) {
            statements.add(tail);
        }
        return statements;
    }

    private boolean isDatabaseDirective(String statement) {
        String upper = statement.toUpperCase();
        return upper.startsWith("CREATE DATABASE") || upper.startsWith("USE ");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to clean up.
    }
}

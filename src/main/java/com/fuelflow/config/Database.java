package com.fuelflow.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Reads connection settings from environment variables first, then db.properties.
 * Do not hard-code or commit database passwords.
 */
public final class Database {
    private static final Properties P = new Properties();

    static {
        try (var in = Database.class.getResourceAsStream("/db.properties")) {
            if (in != null) P.load(in);
        } catch (Exception e) {
            System.err.println("Could not load db.properties: " + e.getMessage());
        }
    }

    private Database() {}

    public static Connection getConnection() throws SQLException {
        String host = required("DB_HOST", "db.host");
        String port = required("DB_PORT", "db.port");
        String name = value("DB_NAME", "db.name", "java_pbl_db");
        String user = required("DB_USER", "db.user");
        String password = required("DB_PASSWORD", "db.password");
        String trustStore = value("DB_TRUSTSTORE", "db.trustStore", "certs/aiven-truststore.jks");
        String trustStorePassword = value("DB_TRUSTSTORE_PASSWORD", "db.trustStorePassword", "changeit");
        String sslMode = value("DB_SSL_MODE", "db.sslMode", "VERIFY_CA");

        StringBuilder url = new StringBuilder("jdbc:mysql://")
                .append(host).append(':').append(port).append('/').append(name)
                .append("?sslMode=").append(sslMode)
                .append("&serverTimezone=UTC");

        if ("VERIFY_CA".equalsIgnoreCase(sslMode) || "VERIFY_IDENTITY".equalsIgnoreCase(sslMode)) {
            Path trustPath = Paths.get(trustStore).toAbsolutePath().normalize();
            if (!java.nio.file.Files.isRegularFile(trustPath)) {
                throw new SQLException("Aiven Java truststore not found: " + trustPath
                        + ". Create certs/aiven-truststore.jks using the Aiven CA certificate.");
            }
            url.append("&trustCertificateKeyStoreUrl=")
                    .append(trustPath.toUri().toString())
                    .append("&trustCertificateKeyStoreType=JKS")
                    .append("&trustCertificateKeyStorePassword=").append(trustStorePassword);
        }

        return DriverManager.getConnection(url.toString(), user, password);
    }

    private static String required(String env, String property) throws SQLException {
        String v = value(env, property, null);
        if (v == null || v.isBlank() || v.startsWith("YOUR_")) {
            throw new SQLException("Set environment variable " + env
                    + " (or property " + property + ") before starting the application.");
        }
        return v.trim();
    }

    private static String value(String env, String property, String fallback) {
        String v = System.getenv(env);
        if (v != null && !v.isBlank()) return v.trim();
        v = P.getProperty(property);
        return (v == null || v.isBlank()) ? fallback : v.trim();
    }

    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            return c.isValid(5);
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}

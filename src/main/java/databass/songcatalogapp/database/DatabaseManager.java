package databass.songcatalogapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/databass_db";

    public static Connection getConnection() throws SQLException {
        String url = getEnvOrDefault("DB_URL", DEFAULT_URL);
        String user = getRequiredEnv("DB_USER");
        String password = getRequiredEnv("DB_PASSWORD");

        return DriverManager.getConnection(url, user, password);
    }

    private static String getEnvOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String getRequiredEnv(String name) throws SQLException {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new SQLException("Missing required environment variable: " + name);
        }

        return value;
    }
}


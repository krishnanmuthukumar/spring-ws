package org.krish.learn.servlet;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = System.getenv("DB_URL");
    private static final String USERNAME = System.getenv("DB_USERNAME");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {

        if(URL == null || USERNAME == null || PASSWORD == null) {
        	System.out.println("Database configuration is missing");
            throw new IllegalStateException("Database configuration is missing.");
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "PostgreSQL JDBC driver is not available to the web application.", e);
        }

        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

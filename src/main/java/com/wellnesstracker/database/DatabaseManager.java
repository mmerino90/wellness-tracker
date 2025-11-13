package com.wellnesstracker.database;

import java.sql.*;
import java.io.File;

/**
 * DatabaseManager handles SQLite database connections and operations.
 * Provides static methods for executing SQL queries and managing transactions.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.home") + "/.wellness-tracker/wellness_tracker.db";
    private static Connection connection;

    /**
     * Initializes the database connection.
     * Creates the database and tables if they don't exist.
     */
    public static void initialize() {
        try {
            // Ensure database directory exists
            String dbPath = System.getProperty("user.home") + "/.wellness-tracker";
            File dbDir = new File(dbPath);
            if (!dbDir.exists()) {
                dbDir.mkdirs();
                System.out.println("Created database directory: " + dbPath);
            }
            
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Database connection established.");
                initializeSchema();
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the database schema by executing init_db.sql script.
     */
    private static void initializeSchema() {
        try {
            String[] sqlStatements = {
                "CREATE TABLE IF NOT EXISTS users (" +
                "    user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    username TEXT NOT NULL UNIQUE," +
                "    email TEXT NOT NULL UNIQUE," +
                "    password_hash TEXT NOT NULL," +
                "    first_name TEXT," +
                "    last_name TEXT," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");",
                
                "CREATE TABLE IF NOT EXISTS habits (" +
                "    habit_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    user_id INTEGER NOT NULL," +
                "    habit_name TEXT NOT NULL," +
                "    description TEXT," +
                "    category TEXT," +
                "    frequency TEXT CHECK(frequency IN ('daily', 'weekly', 'monthly'))," +
                "    streak_count INTEGER DEFAULT 0," +
                "    is_active BOOLEAN DEFAULT 1," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ");",
                
                "CREATE TABLE IF NOT EXISTS mood_entries (" +
                "    entry_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    user_id INTEGER NOT NULL," +
                "    mood_level TEXT NOT NULL," +
                "    emotional_context TEXT," +
                "    notes TEXT," +
                "    activities TEXT," +
                "    energy_level TEXT CHECK(energy_level IN ('low', 'medium', 'high'))," +
                "    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ");",
                
                "CREATE TABLE IF NOT EXISTS challenges (" +
                "    challenge_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    challenge_name TEXT NOT NULL," +
                "    description TEXT," +
                "    difficulty TEXT CHECK(difficulty IN ('easy', 'medium', 'hard'))," +
                "    duration_days INTEGER," +
                "    category TEXT," +
                "    reward TEXT," +
                "    max_participants INTEGER," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    start_date DATE," +
                "    end_date DATE" +
                ");",
                
                "CREATE TABLE IF NOT EXISTS user_challenges (" +
                "    user_challenge_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    user_id INTEGER NOT NULL," +
                "    challenge_id INTEGER NOT NULL," +
                "    progress INTEGER DEFAULT 0," +
                "    status TEXT CHECK(status IN ('active', 'completed', 'abandoned'))," +
                "    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    completed_at TIMESTAMP," +
                "    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "    FOREIGN KEY (challenge_id) REFERENCES challenges(challenge_id) ON DELETE CASCADE," +
                "    UNIQUE(user_id, challenge_id)" +
                ");"
            };

            for (String sql : sqlStatements) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                }
            }
            System.out.println("Database schema initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing schema: " + e.getMessage());
        }
    }

    /**
     * Gets the database connection.
     *
     * @return the Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initialize();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Executes a query and returns the ResultSet.
     *
     * @param query the SQL query to execute
     * @return the ResultSet containing query results
     */
    public static ResultSet executeQuery(String query) {
        try {
            Statement stmt = getConnection().createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            return null;
        }
    }

    /**
     * Executes an update (INSERT, UPDATE, DELETE).
     *
     * @param sql the SQL statement to execute
     * @return the number of rows affected
     */
    public static int executeUpdate(String sql) {
        try (Statement stmt = getConnection().createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error executing update: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Executes a prepared statement with parameters.
     *
     * @param sql the SQL statement with placeholders
     * @param parameters the parameters to insert
     * @return the ResultSet if it's a query, null for updates
     */
    public static ResultSet executePreparedQuery(String sql, Object... parameters) {
        try {
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            for (int i = 0; i < parameters.length; i++) {
                pstmt.setObject(i + 1, parameters[i]);
            }
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error executing prepared query: " + e.getMessage());
            return null;
        }
    }

    /**
     * Executes a prepared update with parameters.
     *
     * @param sql the SQL statement with placeholders
     * @param parameters the parameters to insert
     * @return the number of rows affected
     */
    public static int executePreparedUpdate(String sql, Object... parameters) {
        try {
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            for (int i = 0; i < parameters.length; i++) {
                pstmt.setObject(i + 1, parameters[i]);
            }
            int result = pstmt.executeUpdate();
            pstmt.close();
            return result;
        } catch (SQLException e) {
            System.err.println("Error executing prepared update: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Closes the database connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}

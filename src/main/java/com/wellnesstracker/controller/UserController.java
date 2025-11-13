package com.wellnesstracker.controller;

import com.wellnesstracker.model.User;
import com.wellnesstracker.database.DatabaseManager;
import com.wellnesstracker.utils.PasswordUtil;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserController handles CRUD operations for User entities.
 * Manages user authentication, registration, and profile management.
 */
public class UserController {

    /**
     * Authenticates a user with the provided username and password.
     *
     * @param username the username to authenticate
     * @param password the password to verify
     * @return the authenticated User object if successful, null otherwise
     */
    public User authenticate(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.err.println("Username and password cannot be empty");
            return null;
        }

        try {
            User user = getUserByUsername(username);
            if (user != null && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                return user;
            }
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
        }
        return null;
    }

    /**
     * Registers a new user in the system.
     *
     * @param username the username for the new account
     * @param email the email address
     * @param password the plain text password
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @return the newly created User object if successful, null otherwise
     */
    public User registerUser(String username, String email, String password, String firstName, String lastName) {
        // Validate input
        if (username == null || username.isEmpty() || email == null || email.isEmpty() || 
            password == null || password.isEmpty()) {
            System.err.println("Username, email, and password cannot be empty");
            return null;
        }

        if (username.length() < 3 || username.length() > 50) {
            System.err.println("Username must be between 3 and 50 characters");
            return null;
        }

        if (password.length() < 6) {
            System.err.println("Password must be at least 6 characters");
            return null;
        }

        // Check if username already exists
        if (getUserByUsername(username) != null) {
            System.err.println("Username already exists");
            return null;
        }

        // Check if email already exists
        if (getUserByEmail(email) != null) {
            System.err.println("Email already exists");
            return null;
        }

        try {
            String hashedPassword = PasswordUtil.hashPassword(password);
            String sql = "INSERT INTO users (username, email, password_hash, first_name, last_name) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            DatabaseManager.executePreparedUpdate(sql, username, email, hashedPassword, firstName, lastName);
            
            // Return the newly created user
            return getUserByUsername(username);
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a user by username.
     *
     * @param username the username to search for
     * @return the User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, username);
            
            if (rs != null && rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by username: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a user by email.
     *
     * @param email the email to search for
     * @return the User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        try {
            String sql = "SELECT * FROM users WHERE email = ?";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, email);
            
            if (rs != null && rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by email: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a user by user ID.
     *
     * @param userId the user ID to search for
     * @return the User object if found, null otherwise
     */
    public User getUserById(int userId) {
        try {
            String sql = "SELECT * FROM users WHERE user_id = ?";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, userId);
            
            if (rs != null && rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates a user's profile information.
     *
     * @param user the User object with updated information
     * @return true if update is successful, false otherwise
     */
    public boolean updateUser(User user) {
        if (user == null || user.getUserId() <= 0) {
            System.err.println("Invalid user object");
            return false;
        }

        try {
            String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, updated_at = CURRENT_TIMESTAMP " +
                        "WHERE user_id = ?";
            
            int rowsAffected = DatabaseManager.executePreparedUpdate(sql, 
                user.getFirstName(), user.getLastName(), user.getEmail(), user.getUserId());
            
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Changes a user's password.
     *
     * @param userId the user ID
     * @param oldPassword the current password (for verification)
     * @param newPassword the new password
     * @return true if password change is successful, false otherwise
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            System.err.println("New password must be at least 6 characters");
            return false;
        }

        try {
            User user = getUserById(userId);
            if (user == null || !PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
                System.err.println("Old password is incorrect");
                return false;
            }

            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            String sql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
            
            int rowsAffected = DatabaseManager.executePreparedUpdate(sql, hashedPassword, userId);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a user from the system.
     *
     * @param userId the ID of the user to delete
     * @return true if deletion is successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        try {
            String sql = "DELETE FROM users WHERE user_id = ?";
            int rowsAffected = DatabaseManager.executePreparedUpdate(sql, userId);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a User object.
     *
     * @param rs the ResultSet containing user data
     * @return the User object
     * @throws SQLException if there's an error reading from ResultSet
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setCreatedAt(rs.getString("created_at"));
        user.setUpdatedAt(rs.getString("updated_at"));
        return user;
    }
}

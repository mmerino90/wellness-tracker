package com.wellnesstracker.controller;

import com.wellnesstracker.model.Habit;
import com.wellnesstracker.database.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * HabitController handles CRUD operations for Habit entities.
 * Manages habit creation, tracking, and progress monitoring.
 */
public class HabitController {

    /**
     * Creates a new habit for a user.
     *
     * @param habit the Habit object to create
     * @return the ID of the newly created habit, or -1 if creation fails
     */
    public int createHabit(Habit habit) {
        if (habit == null || habit.getUserId() <= 0 || habit.getHabitName() == null || habit.getHabitName().isEmpty()) {
            System.err.println("Invalid habit data");
            return -1;
        }

        try {
            String sql = "INSERT INTO habits (user_id, habit_name, description, category, frequency, is_active) " +
                        "VALUES (?, ?, ?, ?, ?, 1)";
            
            DatabaseManager.executePreparedUpdate(sql, 
                habit.getUserId(), 
                habit.getHabitName(), 
                habit.getDescription(), 
                habit.getCategory(), 
                habit.getFrequency());
            
            // Get the inserted habit ID
            Habit createdHabit = getHabitByNameAndUserId(habit.getUserId(), habit.getHabitName());
            return createdHabit != null ? createdHabit.getHabitId() : -1;
        } catch (Exception e) {
            System.err.println("Error creating habit: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves all habits for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of Habit objects for the user
     */
    public List<Habit> getUserHabits(int userId) {
        List<Habit> habits = new ArrayList<>();
        try {
            String sql = "SELECT * FROM habits WHERE user_id = ? AND is_active = 1 ORDER BY created_at DESC";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, userId);
            
            if (rs != null) {
                while (rs.next()) {
                    habits.add(mapResultSetToHabit(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user habits: " + e.getMessage());
        }
        return habits;
    }

    /**
     * Retrieves all habits (including inactive) for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of all Habit objects for the user
     */
    public List<Habit> getAllUserHabits(int userId) {
        List<Habit> habits = new ArrayList<>();
        try {
            String sql = "SELECT * FROM habits WHERE user_id = ? ORDER BY created_at DESC";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, userId);
            
            if (rs != null) {
                while (rs.next()) {
                    habits.add(mapResultSetToHabit(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all user habits: " + e.getMessage());
        }
        return habits;
    }

    /**
     * Retrieves a specific habit by ID.
     *
     * @param habitId the ID of the habit to retrieve
     * @return the Habit object if found, null otherwise
     */
    public Habit getHabitById(int habitId) {
        try {
            String sql = "SELECT * FROM habits WHERE habit_id = ?";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, habitId);
            
            if (rs != null && rs.next()) {
                return mapResultSetToHabit(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving habit by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a habit by name and user ID.
     *
     * @param userId the user ID
     * @param habitName the name of the habit
     * @return the Habit object if found, null otherwise
     */
    private Habit getHabitByNameAndUserId(int userId, String habitName) {
        try {
            String sql = "SELECT * FROM habits WHERE user_id = ? AND habit_name = ?";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, userId, habitName);
            
            if (rs != null && rs.next()) {
                return mapResultSetToHabit(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving habit: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates an existing habit.
     *
     * @param habit the Habit object with updated information
     * @return true if update is successful, false otherwise
     */
    public boolean updateHabit(Habit habit) {
        if (habit == null || habit.getHabitId() <= 0) {
            System.err.println("Invalid habit object");
            return false;
        }

        try {
            String sql = "UPDATE habits SET habit_name = ?, description = ?, category = ?, " +
                        "frequency = ?, updated_at = CURRENT_TIMESTAMP WHERE habit_id = ?";
            
            int rowsAffected = DatabaseManager.executePreparedUpdate(sql, 
                habit.getHabitName(), 
                habit.getDescription(), 
                habit.getCategory(), 
                habit.getFrequency(), 
                habit.getHabitId());
            
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error updating habit: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a habit (soft delete - marks as inactive).
     *
     * @param habitId the ID of the habit to delete
     * @return true if deletion is successful, false otherwise
     */
    public boolean deleteHabit(int habitId) {
        try {
            String sql = "UPDATE habits SET is_active = 0, updated_at = CURRENT_TIMESTAMP WHERE habit_id = ?";
            int rowsAffected = DatabaseManager.executePreparedUpdate(sql, habitId);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error deleting habit: " + e.getMessage());
        }
        return false;
    }

    /**
     * Permanently deletes a habit.
     *
     * @param habitId the ID of the habit to permanently delete
     * @return true if deletion is successful, false otherwise
     */
    public boolean permanentlyDeleteHabit(int habitId) {
        try {
            String sql = "DELETE FROM habits WHERE habit_id = ?";
            int rowsAffected = DatabaseManager.executePreparedUpdate(sql, habitId);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error permanently deleting habit: " + e.getMessage());
        }
        return false;
    }

    /**
     * Increments the streak count for a habit (mark as completed today).
     *
     * @param habitId the ID of the habit
     * @return true if streak is incremented, false otherwise
     */
    public boolean incrementStreak(int habitId) {
        try {
            // Check if habit was already completed today
            String checkSql = "SELECT * FROM habit_tracking WHERE habit_id = ? AND completion_date = DATE('now')";
            ResultSet rs = DatabaseManager.executePreparedQuery(checkSql, habitId);
            
            if (rs != null && rs.next()) {
                System.out.println("Habit already completed today");
                return false;
            }

            // Mark as completed today
            String insertSql = "INSERT INTO habit_tracking (habit_id, completion_date, is_completed) VALUES (?, DATE('now'), 1)";
            DatabaseManager.executePreparedUpdate(insertSql, habitId);

            // Increment streak
            String updateSql = "UPDATE habits SET streak_count = streak_count + 1, updated_at = CURRENT_TIMESTAMP WHERE habit_id = ?";
            int rowsAffected = DatabaseManager.executePreparedUpdate(updateSql, habitId);
            
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error incrementing streak: " + e.getMessage());
        }
        return false;
    }

    /**
     * Resets the streak count for a habit.
     *
     * @param habitId the ID of the habit
     * @return true if streak is reset, false otherwise
     */
    public boolean resetStreak(int habitId) {
        try {
            String sql = "UPDATE habits SET streak_count = 0, updated_at = CURRENT_TIMESTAMP WHERE habit_id = ?";
            int rowsAffected = DatabaseManager.executePreparedUpdate(sql, habitId);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error resetting streak: " + e.getMessage());
        }
        return false;
    }

    /**
     * Gets the completion rate for a habit over a number of days.
     *
     * @param habitId the habit ID
     * @param days the number of days to check
     * @return the completion rate as a percentage (0-100)
     */
    public double getCompletionRate(int habitId, int days) {
        try {
            String sql = "SELECT COUNT(*) as completed FROM habit_tracking " +
                        "WHERE habit_id = ? AND is_completed = 1 AND completion_date >= DATE('now', '-' || ? || ' days')";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, habitId, days);
            
            if (rs != null && rs.next()) {
                int completed = rs.getInt("completed");
                return (completed / (double) days) * 100;
            }
        } catch (SQLException e) {
            System.err.println("Error getting completion rate: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Maps a ResultSet row to a Habit object.
     *
     * @param rs the ResultSet containing habit data
     * @return the Habit object
     * @throws SQLException if there's an error reading from ResultSet
     */
    private Habit mapResultSetToHabit(ResultSet rs) throws SQLException {
        Habit habit = new Habit();
        habit.setHabitId(rs.getInt("habit_id"));
        habit.setUserId(rs.getInt("user_id"));
        habit.setHabitName(rs.getString("habit_name"));
        habit.setDescription(rs.getString("description"));
        habit.setCategory(rs.getString("category"));
        habit.setFrequency(rs.getString("frequency"));
        habit.setStreakCount(rs.getInt("streak_count"));
        habit.setActive(rs.getBoolean("is_active"));
        habit.setCreatedAt(rs.getString("created_at"));
        habit.setUpdatedAt(rs.getString("updated_at"));
        return habit;
    }
}

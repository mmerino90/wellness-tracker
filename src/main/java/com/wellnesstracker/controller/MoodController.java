package com.wellnesstracker.controller;

import com.wellnesstracker.model.MoodEntry;
import com.wellnesstracker.database.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MoodController handles CRUD operations for Mood entries.
 * Manages mood logging and mood history retrieval.
 */
public class MoodController {

    /**
     * Creates a new mood entry for a user.
     *
     * @param moodEntry the MoodEntry object to create
     * @return the ID of the newly created mood entry, or -1 if creation fails
     */
    public int createMoodEntry(MoodEntry moodEntry) {
        if (moodEntry == null || moodEntry.getUserId() <= 0 || moodEntry.getMoodLevel() == null || moodEntry.getMoodLevel().isEmpty()) {
            System.err.println("Invalid mood entry data");
            return -1;
        }

        try {
            String sql = "INSERT INTO mood_entries (user_id, mood_level, emotional_context, notes, activities, energy_level) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            
            DatabaseManager.executePreparedUpdate(sql, 
                moodEntry.getUserId(), 
                moodEntry.getMoodLevel(), 
                moodEntry.getEmotionalContext(), 
                moodEntry.getNotes(), 
                moodEntry.getActivities(), 
                moodEntry.getEnergyLevel());
            
            // Get the inserted entry ID
            MoodEntry createdEntry = getLatestMoodEntry(moodEntry.getUserId());
            return createdEntry != null ? createdEntry.getEntryId() : -1;
        } catch (Exception e) {
            System.err.println("Error creating mood entry: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves all mood entries for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of MoodEntry objects for the user
     */
    public List<MoodEntry> getUserMoodEntries(int userId) {
        List<MoodEntry> entries = new ArrayList<>();
        try {
            String sql = "SELECT * FROM mood_entries WHERE user_id = ? ORDER BY timestamp DESC";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, userId);
            
            if (rs != null) {
                while (rs.next()) {
                    entries.add(mapResultSetToMoodEntry(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user mood entries: " + e.getMessage());
        }
        return entries;
    }

    /**
     * Retrieves a specific mood entry by ID.
     *
     * @param entryId the ID of the mood entry to retrieve
     * @return the MoodEntry object if found, null otherwise
     */
    public MoodEntry getMoodEntryById(int entryId) {
        try {
            String sql = "SELECT * FROM mood_entries WHERE entry_id = ?";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, entryId);
            
            if (rs != null && rs.next()) {
                return mapResultSetToMoodEntry(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving mood entry by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves the latest mood entry for a user.
     *
     * @param userId the user ID
     * @return the latest MoodEntry object, or null if none found
     */
    private MoodEntry getLatestMoodEntry(int userId) {
        try {
            String sql = "SELECT * FROM mood_entries WHERE user_id = ? ORDER BY timestamp DESC LIMIT 1";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, userId);
            
            if (rs != null && rs.next()) {
                return mapResultSetToMoodEntry(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving latest mood entry: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates an existing mood entry.
     *
     * @param moodEntry the MoodEntry object with updated information
     * @return true if update is successful, false otherwise
     */
    public boolean updateMoodEntry(MoodEntry moodEntry) {
        if (moodEntry == null || moodEntry.getEntryId() <= 0) {
            System.err.println("Invalid mood entry object");
            return false;
        }

        try {
            String sql = "UPDATE mood_entries SET mood_level = ?, emotional_context = ?, notes = ?, " +
                        "activities = ?, energy_level = ? WHERE entry_id = ?";
            
            int rowsAffected = DatabaseManager.executePreparedUpdate(sql, 
                moodEntry.getMoodLevel(), 
                moodEntry.getEmotionalContext(), 
                moodEntry.getNotes(), 
                moodEntry.getActivities(), 
                moodEntry.getEnergyLevel(), 
                moodEntry.getEntryId());
            
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error updating mood entry: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a mood entry.
     *
     * @param entryId the ID of the mood entry to delete
     * @return true if deletion is successful, false otherwise
     */
    public boolean deleteMoodEntry(int entryId) {
        try {
            String sql = "DELETE FROM mood_entries WHERE entry_id = ?";
            int rowsAffected = DatabaseManager.executePreparedUpdate(sql, entryId);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error deleting mood entry: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves mood entries for a user within a specific date range.
     *
     * @param userId the ID of the user
     * @param startDate the start date for the range (YYYY-MM-DD format)
     * @param endDate the end date for the range (YYYY-MM-DD format)
     * @return a list of MoodEntry objects within the date range
     */
    public List<MoodEntry> getMoodEntriesByDateRange(int userId, String startDate, String endDate) {
        List<MoodEntry> entries = new ArrayList<>();
        try {
            String sql = "SELECT * FROM mood_entries WHERE user_id = ? AND DATE(timestamp) BETWEEN ? AND ? ORDER BY timestamp DESC";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, userId, startDate, endDate);
            
            if (rs != null) {
                while (rs.next()) {
                    entries.add(mapResultSetToMoodEntry(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving mood entries by date range: " + e.getMessage());
        }
        return entries;
    }

    /**
     * Calculates average mood for a user over a period.
     *
     * @param userId the ID of the user
     * @param days the number of days to look back
     * @return the average mood level, or -1 if no data available
     */
    public double getAverageMood(int userId, int days) {
        try {
            String sql = "SELECT AVG(CAST(mood_level AS REAL)) as avg_mood FROM mood_entries " +
                        "WHERE user_id = ? AND timestamp >= DATE('now', '-' || ? || ' days')";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, userId, days);
            
            if (rs != null && rs.next()) {
                return rs.getDouble("avg_mood");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating average mood: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Gets mood entry count for a user over a period.
     *
     * @param userId the user ID
     * @param days the number of days to look back
     * @return the count of mood entries
     */
    public int getMoodEntryCount(int userId, int days) {
        try {
            String sql = "SELECT COUNT(*) as count FROM mood_entries " +
                        "WHERE user_id = ? AND timestamp >= DATE('now', '-' || ? || ' days')";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, userId, days);
            
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting mood entry count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets the most common mood for a user over a period.
     *
     * @param userId the user ID
     * @param days the number of days to look back
     * @return the most common mood level
     */
    public String getMostCommonMood(int userId, int days) {
        try {
            String sql = "SELECT mood_level FROM mood_entries " +
                        "WHERE user_id = ? AND timestamp >= DATE('now', '-' || ? || ' days') " +
                        "GROUP BY mood_level ORDER BY COUNT(*) DESC LIMIT 1";
            ResultSet rs = DatabaseManager.executePreparedQuery(sql, userId, days);
            
            if (rs != null && rs.next()) {
                return rs.getString("mood_level");
            }
        } catch (SQLException e) {
            System.err.println("Error getting most common mood: " + e.getMessage());
        }
        return null;
    }

    /**
     * Maps a ResultSet row to a MoodEntry object.
     *
     * @param rs the ResultSet containing mood entry data
     * @return the MoodEntry object
     * @throws SQLException if there's an error reading from ResultSet
     */
    private MoodEntry mapResultSetToMoodEntry(ResultSet rs) throws SQLException {
        MoodEntry entry = new MoodEntry();
        entry.setEntryId(rs.getInt("entry_id"));
        entry.setUserId(rs.getInt("user_id"));
        entry.setMoodLevel(rs.getString("mood_level"));
        entry.setEmotionalContext(rs.getString("emotional_context"));
        entry.setNotes(rs.getString("notes"));
        entry.setActivities(rs.getString("activities"));
        entry.setEnergyLevel(rs.getString("energy_level"));
        entry.setTimestamp(rs.getString("timestamp"));
        return entry;
    }
}

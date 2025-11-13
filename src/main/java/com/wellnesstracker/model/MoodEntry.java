package com.wellnesstracker.model;

/**
 * MoodEntry class representing a user's mood log entry.
 * Stores mood data along with emotional context and notes.
 */
public class MoodEntry {

    private int entryId;
    private int userId;
    private String moodLevel; // 1-10 scale or emotional labels
    private String emotionalContext; // e.g., happy, sad, anxious, calm
    private String notes;
    private String activities; // activities related to the mood
    private String energyLevel; // low, medium, high
    private String timestamp;

    /**
     * Default constructor for MoodEntry class.
     */
    public MoodEntry() {
    }

    /**
     * Constructor with main mood entry details.
     *
     * @param entryId unique identifier for the mood entry
     * @param userId user who created this entry
     * @param moodLevel mood level (1-10 or emotional label)
     * @param emotionalContext emotional context
     * @param notes additional notes about the mood
     */
    public MoodEntry(int entryId, int userId, String moodLevel, 
                     String emotionalContext, String notes) {
        this.entryId = entryId;
        this.userId = userId;
        this.moodLevel = moodLevel;
        this.emotionalContext = emotionalContext;
        this.notes = notes;
    }

    // Getters and Setters
    public int getEntryId() { return entryId; }
    public void setEntryId(int entryId) { this.entryId = entryId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMoodLevel() { return moodLevel; }
    public void setMoodLevel(String moodLevel) { this.moodLevel = moodLevel; }

    public String getEmotionalContext() { return emotionalContext; }
    public void setEmotionalContext(String emotionalContext) { this.emotionalContext = emotionalContext; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getActivities() { return activities; }
    public void setActivities(String activities) { this.activities = activities; }

    public String getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(String energyLevel) { this.energyLevel = energyLevel; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    /**
     * Returns a string representation of the MoodEntry object.
     */
    @Override
    public String toString() {
        return "MoodEntry{" +
                "entryId=" + entryId +
                ", moodLevel='" + moodLevel + '\'' +
                ", emotionalContext='" + emotionalContext + '\'' +
                ", energyLevel='" + energyLevel + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

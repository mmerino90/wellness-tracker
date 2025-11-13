package com.wellnesstracker.model;

/**
 * Habit class representing a wellness habit tracked by a user.
 * Stores habit details and tracking information.
 */
public class Habit {

    private int habitId;
    private int userId;
    private String habitName;
    private String description;
    private String category;
    private String frequency; // daily, weekly, monthly
    private int streakCount;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;

    /**
     * Default constructor for Habit class.
     */
    public Habit() {
    }

    /**
     * Constructor with main habit details.
     *
     * @param habitId unique identifier for the habit
     * @param userId user who owns this habit
     * @param habitName name of the habit
     * @param description description of the habit
     * @param frequency frequency of the habit (daily, weekly, monthly)
     */
    public Habit(int habitId, int userId, String habitName, String description, String frequency) {
        this.habitId = habitId;
        this.userId = userId;
        this.habitName = habitName;
        this.description = description;
        this.frequency = frequency;
        this.isActive = true;
    }

    // Getters and Setters
    public int getHabitId() { return habitId; }
    public void setHabitId(int habitId) { this.habitId = habitId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getHabitName() { return habitName; }
    public void setHabitName(String habitName) { this.habitName = habitName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public int getStreakCount() { return streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Returns a string representation of the Habit object.
     */
    @Override
    public String toString() {
        return "Habit{" +
                "habitId=" + habitId +
                ", habitName='" + habitName + '\'' +
                ", frequency='" + frequency + '\'' +
                ", streakCount=" + streakCount +
                ", isActive=" + isActive +
                '}';
    }
}

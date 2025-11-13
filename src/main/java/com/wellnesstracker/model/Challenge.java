package com.wellnesstracker.model;

/**
 * Challenge class representing a wellness challenge.
 * Can be assigned to users to promote healthy behavior.
 */
public class Challenge {

    private int challengeId;
    private String challengeName;
    private String description;
    private String difficulty; // easy, medium, hard
    private int durationDays;
    private String category; // health, fitness, mindfulness, etc.
    private String reward;
    private int maxParticipants;
    private String createdAt;
    private String startDate;
    private String endDate;

    /**
     * Default constructor for Challenge class.
     */
    public Challenge() {
    }

    /**
     * Constructor with main challenge details.
     *
     * @param challengeId unique identifier for the challenge
     * @param challengeName name of the challenge
     * @param description description of the challenge
     * @param difficulty difficulty level
     * @param durationDays duration in days
     */
    public Challenge(int challengeId, String challengeName, String description, 
                     String difficulty, int durationDays) {
        this.challengeId = challengeId;
        this.challengeName = challengeName;
        this.description = description;
        this.difficulty = difficulty;
        this.durationDays = durationDays;
    }

    // Getters and Setters
    public int getChallengeId() { return challengeId; }
    public void setChallengeId(int challengeId) { this.challengeId = challengeId; }

    public String getChallengeName() { return challengeName; }
    public void setChallengeName(String challengeName) { this.challengeName = challengeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getReward() { return reward; }
    public void setReward(String reward) { this.reward = reward; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    /**
     * Returns a string representation of the Challenge object.
     */
    @Override
    public String toString() {
        return "Challenge{" +
                "challengeId=" + challengeId +
                ", challengeName='" + challengeName + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", durationDays=" + durationDays +
                ", category='" + category + '\'' +
                '}';
    }
}

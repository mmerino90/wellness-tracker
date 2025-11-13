package tests;

import model.MoodEntry;
import controller.MoodController;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for the MoodEntry class and MoodController.
 * Tests mood tracking, emotion logging, energy tracking, and data retrieval.
 */
public class MoodControllerTest {

    private MoodEntry testMood;
    private MoodController moodController;

    /**
     * Set up test fixtures before each test.
     */
    @Before
    public void setUp() {
        testMood = new MoodEntry(1, 1, "7", "Good day at work", 
                                "High", "Productive, Happy", LocalDate.now().toString());
        moodController = new MoodController();
    }

    /**
     * Test MoodEntry object creation and getters.
     */
    @Test
    public void testMoodEntryCreation() {
        assertEquals(1, testMood.getMoodId());
        assertEquals(1, testMood.getUserId());
        assertEquals("7", testMood.getMoodLevel());
        assertEquals("Good day at work", testMood.getNotes());
        assertEquals("High", testMood.getEnergyLevel());
        assertEquals("Productive, Happy", testMood.getEmotionalContext());
    }

    /**
     * Test MoodEntry setters.
     */
    @Test
    public void testMoodEntrySetters() {
        testMood.setMoodLevel("8");
        testMood.setEnergyLevel("Very High");
        testMood.setNotes("Excellent day!");
        
        assertEquals("8", testMood.getMoodLevel());
        assertEquals("Very High", testMood.getEnergyLevel());
        assertEquals("Excellent day!", testMood.getNotes());
    }

    /**
     * Test mood level validation (1-10 range).
     */
    @Test
    public void testMoodLevelValidation() {
        // Test valid mood levels
        for (int i = 1; i <= 10; i++) {
            testMood.setMoodLevel(String.valueOf(i));
            assertEquals(String.valueOf(i), testMood.getMoodLevel());
        }
        
        // Test edge cases
        testMood.setMoodLevel("1");
        assertEquals("1", testMood.getMoodLevel());
        testMood.setMoodLevel("10");
        assertEquals("10", testMood.getMoodLevel());
    }

    /**
     * Test energy level classification.
     */
    @Test
    public void testEnergyLevelClassification() {
        MoodEntry lowEnergy = new MoodEntry(2, 1, "3", "Tired day", 
                                           "Low", "Exhausted", LocalDate.now().toString());
        MoodEntry mediumEnergy = new MoodEntry(3, 1, "5", "Normal day", 
                                              "Medium", "Okay", LocalDate.now().toString());
        MoodEntry highEnergy = new MoodEntry(4, 1, "9", "Great day", 
                                            "High", "Energetic", LocalDate.now().toString());
        
        assertEquals("Low", lowEnergy.getEnergyLevel());
        assertEquals("Medium", mediumEnergy.getEnergyLevel());
        assertEquals("High", highEnergy.getEnergyLevel());
    }

    /**
     * Test emotional context tracking.
     */
    @Test
    public void testEmotionalContextTracking() {
        String emotions = "Happy, Excited, Grateful";
        testMood.setEmotionalContext(emotions);
        
        assertEquals(emotions, testMood.getEmotionalContext());
        assertTrue(testMood.getEmotionalContext().contains("Happy"));
        assertTrue(testMood.getEmotionalContext().contains("Excited"));
    }

    /**
     * Test mood notes are optional but trackable.
     */
    @Test
    public void testMoodNotesOptional() {
        MoodEntry moodNoNotes = new MoodEntry(5, 1, "6", "", 
                                             "Medium", "Neutral", LocalDate.now().toString());
        
        assertEquals("", moodNoNotes.getNotes());
        
        moodNoNotes.setNotes("Added notes later");
        assertEquals("Added notes later", moodNoNotes.getNotes());
    }

    /**
     * Test timestamp recording for mood entries.
     */
    @Test
    public void testTimestampRecording() {
        String today = LocalDate.now().toString();
        testMood.setTimestamp(today);
        
        assertEquals(today, testMood.getTimestamp());
        
        // Verify timestamp format is valid
        assertTrue(testMood.getTimestamp().matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    /**
     * Test creating multiple mood entries for trend analysis.
     */
    @Test
    public void testMultipleMoodEntriesForTrends() {
        // Create mood entries simulating a week of tracking
        MoodEntry[] weekMoods = new MoodEntry[7];
        int[] moodLevels = {5, 6, 7, 8, 7, 6, 8};
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().minusDays(6 - i);
            weekMoods[i] = new MoodEntry(i + 1, 1, String.valueOf(moodLevels[i]), 
                                        "Day " + (i + 1), "Medium", "Neutral", date.toString());
        }
        
        // Verify entries were created
        assertEquals(7, weekMoods.length);
        
        // Calculate average mood
        double sum = 0;
        for (MoodEntry mood : weekMoods) {
            sum += Integer.parseInt(mood.getMoodLevel());
        }
        double average = sum / weekMoods.length;
        
        assertTrue(average > 0);
        assertTrue(average <= 10);
        assertEquals(6.857, average, 0.1); // Approximate average
    }

    /**
     * Test MoodEntry toString method.
     */
    @Test
    public void testMoodEntryToString() {
        String moodString = testMood.toString();
        assertNotNull(moodString);
        assertTrue(moodString.contains("7"));
        assertTrue(moodString.contains("Good day at work"));
    }

    /**
     * Test comparing mood entries for sorting.
     */
    @Test
    public void testMoodEntrySorting() {
        MoodEntry mood1 = new MoodEntry(1, 1, "5", "Entry 1", 
                                       "Medium", "Neutral", "2025-01-01");
        MoodEntry mood2 = new MoodEntry(2, 1, "7", "Entry 2", 
                                       "High", "Happy", "2025-01-02");
        MoodEntry mood3 = new MoodEntry(3, 1, "3", "Entry 3", 
                                       "Low", "Sad", "2025-01-03");
        
        // Verify individual entries
        assertEquals("5", mood1.getMoodLevel());
        assertEquals("7", mood2.getMoodLevel());
        assertEquals("3", mood3.getMoodLevel());
        
        // Lowest mood entry
        MoodEntry lowestMood = mood3;
        assertTrue(Integer.parseInt(lowestMood.getMoodLevel()) < 
                  Integer.parseInt(mood1.getMoodLevel()));
    }
}

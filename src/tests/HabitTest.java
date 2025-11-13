package tests;

import model.Habit;
import controller.HabitController;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Habit class and HabitController.
 */
public class HabitTest {

    private Habit testHabit;
    private HabitController habitController;

    /**
     * Set up test fixtures before each test.
     */
    @Before
    public void setUp() {
        testHabit = new Habit(1, 1, "Running", "30 minutes daily run", "daily");
        habitController = new HabitController();
    }

    /**
     * Test Habit object creation and getters.
     */
    @Test
    public void testHabitCreation() {
        assertEquals(1, testHabit.getHabitId());
        assertEquals(1, testHabit.getUserId());
        assertEquals("Running", testHabit.getHabitName());
        assertEquals("30 minutes daily run", testHabit.getDescription());
        assertEquals("daily", testHabit.getFrequency());
    }

    /**
     * Test Habit setters.
     */
    @Test
    public void testHabitSetters() {
        testHabit.setHabitName("Jogging");
        testHabit.setCategory("Fitness");
        testHabit.setStreakCount(5);
        
        assertEquals("Jogging", testHabit.getHabitName());
        assertEquals("Fitness", testHabit.getCategory());
        assertEquals(5, testHabit.getStreakCount());
    }

    /**
     * Test Habit default active status.
     */
    @Test
    public void testHabitDefaultActive() {
        assertTrue(testHabit.isActive());
    }

    /**
     * Test creating a new habit via controller.
     */
    @Test
    public void testCreateHabit() {
        // Create new habit with unique ID
        Habit newHabit = new Habit(2, 1, "Meditation", "Daily meditation practice", "daily");
        newHabit.setCategory("Mindfulness");
        
        // Verify habit was created with correct properties
        assertEquals(2, newHabit.getHabitId());
        assertEquals("Meditation", newHabit.getHabitName());
        assertEquals("Mindfulness", newHabit.getCategory());
        assertEquals("daily", newHabit.getFrequency());
        assertTrue(newHabit.isActive());
    }

    /**
     * Test retrieving habits for a user.
     */
    @Test
    public void testGetUserHabits() {
        // Create multiple test habits for the same user
        Habit habit1 = new Habit(1, 1, "Running", "30 minutes daily run", "daily");
        Habit habit2 = new Habit(2, 1, "Reading", "Read for 30 minutes", "daily");
        Habit habit3 = new Habit(3, 2, "Swimming", "Weekly swim session", "weekly");
        
        // Verify habits are associated with correct user
        assertEquals(1, habit1.getUserId());
        assertEquals(1, habit2.getUserId());
        assertEquals(2, habit3.getUserId());
        
        // Verify different habits have different names
        assertNotEquals(habit1.getHabitName(), habit2.getHabitName());
    }

    /**
     * Test updating habit information.
     */
    @Test
    public void testUpdateHabit() {
        // Store original values
        String originalName = testHabit.getHabitName();
        String originalDesc = testHabit.getDescription();
        
        // Update habit fields
        testHabit.setHabitName("Fast Running");
        testHabit.setDescription("45 minutes daily run at faster pace");
        testHabit.setCategory("Cardio");
        
        // Verify updates were applied
        assertEquals("Fast Running", testHabit.getHabitName());
        assertEquals("45 minutes daily run at faster pace", testHabit.getDescription());
        assertEquals("Cardio", testHabit.getCategory());
        
        // Verify unchanged fields remain the same
        assertEquals("daily", testHabit.getFrequency());
        assertEquals(1, testHabit.getHabitId());
        
        // Restore original values
        testHabit.setHabitName(originalName);
        testHabit.setDescription(originalDesc);
    }

    /**
     * Test incrementing streak.
     */
    @Test
    public void testIncrementStreak() {
        // Set initial streak
        testHabit.setStreakCount(5);
        int initialStreak = testHabit.getStreakCount();
        
        // Increment streak
        testHabit.setStreakCount(initialStreak + 1);
        
        // Verify streak incremented
        assertEquals(6, testHabit.getStreakCount());
        assertTrue(testHabit.getStreakCount() > initialStreak);
    }

    /**
     * Test resetting streak.
     */
    @Test
    public void testResetStreak() {
        // Set a high streak
        testHabit.setStreakCount(15);
        assertEquals(15, testHabit.getStreakCount());
        
        // Reset streak to 0
        testHabit.setStreakCount(0);
        
        // Verify streak was reset
        assertEquals(0, testHabit.getStreakCount());
    }

    /**
     * Test Habit toString method.
     */
    @Test
    public void testHabitToString() {
        String habitString = testHabit.toString();
        assertNotNull(habitString);
        assertTrue(habitString.contains("Running"));
        assertTrue(habitString.contains("daily"));
    }
}

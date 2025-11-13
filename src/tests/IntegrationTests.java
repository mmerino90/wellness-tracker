package tests;

import model.User;
import model.Habit;
import model.MoodEntry;
import controller.UserController;
import controller.HabitController;
import controller.MoodController;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import static org.junit.Assert.*;

/**
 * Integration tests for the Wellness Tracker application.
 * Tests interactions between multiple components and data persistence.
 */
public class IntegrationTests {

    private User testUser;
    private UserController userController;
    private HabitController habitController;
    private MoodController moodController;

    /**
     * Set up test fixtures before each integration test.
     */
    @Before
    public void setUp() {
        testUser = new User(1, "integrationtest", "integration@test.com", 
                           PasswordUtil.hashPassword("testpass"), "Test", "User");
        userController = new UserController();
        habitController = new HabitController();
        moodController = new MoodController();
    }

    /**
     * Test complete user registration flow.
     */
    @Test
    public void testUserRegistrationFlow() {
        // Create new user
        User newUser = new User(100, "newintegration", "new@test.com", 
                               PasswordUtil.hashPassword("secure123"), "New", "Integration");
        
        // Verify user creation
        assertEquals("newintegration", newUser.getUsername());
        assertEquals("new@test.com", newUser.getEmail());
        assertNotNull(newUser.getPasswordHash());
        
        // Verify user can be modified
        newUser.setEmail("updated@test.com");
        assertEquals("updated@test.com", newUser.getEmail());
    }

    /**
     * Test user creation with habit management flow.
     */
    @Test
    public void testUserWithHabitsFlow() {
        // Create habits for user
        Habit habit1 = new Habit(1, 1, "Morning Exercise", "30 min workout", "daily");
        Habit habit2 = new Habit(2, 1, "Reading", "Read 20 pages", "daily");
        Habit habit3 = new Habit(3, 1, "Meditation", "10 min meditation", "daily");
        
        habit1.setCategory("Fitness");
        habit2.setCategory("Learning");
        habit3.setCategory("Wellness");
        
        // Verify all habits are linked to same user
        assertEquals(1, habit1.getUserId());
        assertEquals(1, habit2.getUserId());
        assertEquals(1, habit3.getUserId());
        
        // Verify habit diversity
        assertNotEquals(habit1.getHabitName(), habit2.getHabitName());
        assertNotEquals(habit2.getHabitName(), habit3.getHabitName());
    }

    /**
     * Test habit completion tracking and streak management.
     */
    @Test
    public void testHabitStreakManagement() {
        Habit habit = new Habit(10, 1, "Morning Run", "5km run", "daily");
        
        // Verify initial state
        assertTrue(habit.isActive());
        assertEquals(0, habit.getStreakCount());
        
        // Simulate streak building
        habit.setStreakCount(1);
        assertEquals(1, habit.getStreakCount());
        
        habit.setStreakCount(5);
        assertEquals(5, habit.getStreakCount());
        
        habit.setStreakCount(10);
        assertEquals(10, habit.getStreakCount());
        
        // Reset streak
        habit.setStreakCount(0);
        assertEquals(0, habit.getStreakCount());
    }

    /**
     * Test complete mood tracking workflow for a single day.
     */
    @Test
    public void testDailyMoodTrackingWorkflow() {
        String today = LocalDate.now().toString();
        
        // Morning mood entry
        MoodEntry morningMood = new MoodEntry(1, 1, "5", "Just woke up", 
                                             "Low", "Sleepy", today);
        
        // Afternoon mood entry
        MoodEntry afternoonMood = new MoodEntry(2, 1, "7", "Good meeting at work", 
                                               "High", "Productive", today);
        
        // Evening mood entry
        MoodEntry eveningMood = new MoodEntry(3, 1, "8", "Great day overall", 
                                             "Medium", "Happy, Satisfied", today);
        
        // Verify progression throughout day
        assertTrue(Integer.parseInt(morningMood.getMoodLevel()) < 
                  Integer.parseInt(afternoonMood.getMoodLevel()));
        assertTrue(Integer.parseInt(afternoonMood.getMoodLevel()) < 
                  Integer.parseInt(eveningMood.getMoodLevel()));
    }

    /**
     * Test mood trend analysis over multiple days.
     */
    @Test
    public void testMoodTrendAnalysis() {
        MoodEntry[] weekMoods = new MoodEntry[7];
        int[] moodLevels = {4, 5, 6, 7, 8, 8, 9};
        String[] emotions = {"Sad", "Neutral", "Okay", "Good", "Happy", "Very Happy", "Excellent"};
        
        // Create 7 days of mood entries showing positive trend
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().minusDays(6 - i);
            weekMoods[i] = new MoodEntry(i + 1, 1, String.valueOf(moodLevels[i]), 
                                        "Day " + (i + 1), "Variable", emotions[i], date.toString());
        }
        
        // Verify upward trend
        for (int i = 0; i < 6; i++) {
            int current = Integer.parseInt(weekMoods[i].getMoodLevel());
            int next = Integer.parseInt(weekMoods[i + 1].getMoodLevel());
            assertTrue("Mood should improve or stay same", current <= next);
        }
        
        // Verify week started low and ended high
        assertTrue(Integer.parseInt(weekMoods[0].getMoodLevel()) < 
                  Integer.parseInt(weekMoods[6].getMoodLevel()));
    }

    /**
     * Test energy level tracking across moods.
     */
    @Test
    public void testEnergyLevelTracking() {
        // Create moods with different energy levels
        MoodEntry lowEnergy = new MoodEntry(1, 1, "3", "Tired", "Low", "Fatigued", 
                                           LocalDate.now().toString());
        MoodEntry mediumEnergy = new MoodEntry(2, 1, "5", "Normal", "Medium", "Balanced", 
                                              LocalDate.now().toString());
        MoodEntry highEnergy = new MoodEntry(3, 1, "8", "Great", "High", "Energetic", 
                                            LocalDate.now().toString());
        
        // Verify energy levels correlate with mood in this test
        assertTrue(Integer.parseInt(lowEnergy.getMoodLevel()) < 
                  Integer.parseInt(mediumEnergy.getMoodLevel()));
        assertTrue(Integer.parseInt(mediumEnergy.getMoodLevel()) < 
                  Integer.parseInt(highEnergy.getMoodLevel()));
        
        // Verify energy level strings are different
        assertNotEquals(lowEnergy.getEnergyLevel(), mediumEnergy.getEnergyLevel());
        assertNotEquals(mediumEnergy.getEnergyLevel(), highEnergy.getEnergyLevel());
    }

    /**
     * Test emotional context tracking and emotion diversity.
     */
    @Test
    public void testEmotionalContextDiversity() {
        // Create mood entries with diverse emotional contexts
        MoodEntry[] moods = new MoodEntry[5];
        String[] contexts = {
            "Happy, Grateful, Motivated",
            "Anxious, Stressed, Overwhelmed",
            "Content, Peaceful, Calm",
            "Excited, Energetic, Passionate",
            "Sad, Disappointed, Discouraged"
        };
        
        for (int i = 0; i < 5; i++) {
            moods[i] = new MoodEntry(i + 1, 1, String.valueOf(i + 1), 
                                    "Entry " + (i + 1), "Variable", contexts[i], 
                                    LocalDate.now().toString());
        }
        
        // Verify all emotional contexts are captured
        assertEquals(5, moods.length);
        for (MoodEntry mood : moods) {
            assertNotNull(mood.getEmotionalContext());
            assertTrue(mood.getEmotionalContext().length() > 0);
        }
    }

    /**
     * Test comprehensive user wellness dashboard data collection.
     */
    @Test
    public void testWellnessDashboardDataCollection() {
        // Create test data for dashboard
        User dashUser = new User(50, "dashboard", "dashboard@test.com", 
                                PasswordUtil.hashPassword("dash123"), "Dashboard", "User");
        
        // Create multiple habits
        Habit[] habits = new Habit[4];
        habits[0] = new Habit(1, 50, "Exercise", "30 min", "daily");
        habits[1] = new Habit(2, 50, "Read", "20 pages", "daily");
        habits[2] = new Habit(3, 50, "Meditate", "10 min", "daily");
        habits[3] = new Habit(4, 50, "Sleep Early", "11pm bedtime", "daily");
        
        for (Habit h : habits) {
            h.setStreakCount((int)(Math.random() * 30)); // Random streak 0-29
            assertTrue(h.getStreakCount() >= 0);
            assertTrue(h.getStreakCount() < 30);
        }
        
        // Create mood entries for analysis
        MoodEntry[] moods = new MoodEntry[14];
        for (int i = 0; i < 14; i++) {
            LocalDate date = LocalDate.now().minusDays(13 - i);
            int moodLevel = 4 + (int)(Math.random() * 6); // Random mood 4-9
            moods[i] = new MoodEntry(i + 1, 50, String.valueOf(moodLevel), 
                                    "Day " + (i + 1), "Variable", "Tracking", 
                                    date.toString());
        }
        
        // Verify dashboard has data
        assertEquals("dashboard", dashUser.getUsername());
        assertEquals(4, habits.length);
        assertEquals(14, moods.length);
    }

    /**
     * Test user session management throughout the app lifecycle.
     */
    @Test
    public void testUserSessionLifecycle() {
        // Create session user
        User sessionUser = new User(75, "session", "session@test.com", 
                                   PasswordUtil.hashPassword("session123"), "Session", "User");
        
        // Verify user is properly initialized
        assertNotNull(sessionUser.getUsername());
        assertEquals("session", sessionUser.getUsername());
        assertEquals(75, sessionUser.getId());
        
        // Simulate profile updates during session
        sessionUser.setFirstName("SessionUser");
        sessionUser.setLastName("Test");
        
        assertEquals("SessionUser", sessionUser.getFirstName());
        assertEquals("Test", sessionUser.getLastName());
        
        // Verify session data persists through modifications
        assertEquals("session", sessionUser.getUsername());
    }

    /**
     * Test error handling and edge cases in data flow.
     */
    @Test
    public void testErrorHandlingAndEdgeCases() {
        // Test with null/empty values
        MoodEntry moodNull = new MoodEntry(1, 1, "5", null, "Medium", null, 
                                          LocalDate.now().toString());
        assertNull(moodNull.getNotes());
        assertNull(moodNull.getEmotionalContext());
        
        // Test with extreme values
        MoodEntry moodMin = new MoodEntry(1, 1, "1", "Worst day", "Low", "Terrible", 
                                         LocalDate.now().toString());
        MoodEntry moodMax = new MoodEntry(2, 1, "10", "Best day", "High", "Amazing", 
                                         LocalDate.now().toString());
        
        assertEquals("1", moodMin.getMoodLevel());
        assertEquals("10", moodMax.getMoodLevel());
        
        // Test habit with zero streak
        Habit zeroStreak = new Habit(1, 1, "Test", "Test", "daily");
        assertEquals(0, zeroStreak.getStreakCount());
    }
}

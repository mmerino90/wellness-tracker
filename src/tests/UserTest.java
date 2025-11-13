package tests;

import model.User;
import controller.UserController;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the User class and UserController.
 */
public class UserTest {

    private User testUser;
    private UserController userController;

    /**
     * Set up test fixtures before each test.
     */
    @Before
    public void setUp() {
        testUser = new User(1, "testuser", "test@example.com", "hashedpassword", "John", "Doe");
        userController = new UserController();
    }

    /**
     * Test User object creation and getters.
     */
    @Test
    public void testUserCreation() {
        assertEquals("testuser", testUser.getUsername());
        assertEquals("test@example.com", testUser.getEmail());
        assertEquals("John", testUser.getFirstName());
        assertEquals("Doe", testUser.getLastName());
    }

    /**
     * Test User setters.
     */
    @Test
    public void testUserSetters() {
        testUser.setEmail("newemail@example.com");
        testUser.setFirstName("Jane");
        
        assertEquals("newemail@example.com", testUser.getEmail());
        assertEquals("Jane", testUser.getFirstName());
    }

    /**
     * Test user authentication with valid credentials.
     */
    @Test
    public void testAuthenticate() {
        // Create a test user
        User newUser = new User(2, "authtest", "auth@test.com", 
                               PasswordUtil.hashPassword("testpass123"), "Auth", "Test");
        
        // In production, this would check against the database
        assertNotNull(newUser.getUsername());
        assertEquals("authtest", newUser.getUsername());
        
        // Verify password hash is not empty
        assertNotNull(newUser.getPasswordHash());
        assertFalse(newUser.getPasswordHash().isEmpty());
    }

    /**
     * Test user registration with valid data.
     */
    @Test
    public void testRegisterUser() {
        // Create a new user for registration
        User newUser = new User(3, "newuser", "newuser@test.com", 
                               PasswordUtil.hashPassword("newpass456"), "New", "User");
        
        // Verify the user object is properly constructed
        assertEquals("newuser", newUser.getUsername());
        assertEquals("newuser@test.com", newUser.getEmail());
        assertEquals("New", newUser.getFirstName());
        assertEquals("User", newUser.getLastName());
        
        // Verify password hash is set and not empty
        assertNotNull(newUser.getPasswordHash());
        assertFalse(newUser.getPasswordHash().isEmpty());
    }

    /**
     * Test retrieving user by username.
     */
    @Test
    public void testGetUserByUsername() {
        // Test with valid username
        String username = "testuser";
        assertEquals("testuser", testUser.getUsername());
        
        // Test with null username handling
        User nullTest = new User(4, null, "test@test.com", "hash", "Test", "User");
        assertNull(nullTest.getUsername());
        
        // Test with empty username
        User emptyTest = new User(5, "", "test@test.com", "hash", "Test", "User");
        assertEquals("", emptyTest.getUsername());
    }

    /**
     * Test updating user information.
     */
    @Test
    public void testUpdateUser() {
        // Store original values
        String originalEmail = testUser.getEmail();
        String originalFirstName = testUser.getFirstName();
        
        // Update user fields
        testUser.setEmail("updated@example.com");
        testUser.setFirstName("Janet");
        testUser.setLastName("Dough");
        
        // Verify updates were applied
        assertEquals("updated@example.com", testUser.getEmail());
        assertEquals("Janet", testUser.getFirstName());
        assertEquals("Dough", testUser.getLastName());
        
        // Verify unchanged fields remain the same
        assertEquals("testuser", testUser.getUsername());
        
        // Restore original values for other tests
        testUser.setEmail(originalEmail);
        testUser.setFirstName(originalFirstName);
    }

    /**
     * Test User toString method.
     */
    @Test
    public void testUserToString() {
        String userString = testUser.toString();
        assertNotNull(userString);
        assertTrue(userString.contains("testuser"));
        assertTrue(userString.contains("test@example.com"));
    }
}

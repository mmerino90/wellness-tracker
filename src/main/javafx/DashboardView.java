package javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;

/**
 * Controller for the Dashboard view.
 * Displays main dashboard with navigation options for habits, mood tracking, and challenges.
 */
public class DashboardView {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button habitsButton;

    @FXML
    private Button moodButton;

    @FXML
    private Button challengesButton;

    @FXML
    private Button logoutButton;

    @FXML
    private AnchorPane mainContainer;

    private User currentUser;
    private Stage primaryStage;

    /**
     * Sets the current user and primary stage.
     * @param user the logged-in user
     * @param stage the primary application stage
     */
    public void setUserAndStage(User user, Stage stage) {
        this.currentUser = user;
        this.primaryStage = stage;
        updateWelcomeMessage();
    }

    /**
     * Updates the welcome message with user information.
     */
    private void updateWelcomeMessage() {
        if (currentUser != null) {
            String fullName = (currentUser.getFirstName() != null && !currentUser.getFirstName().isEmpty()) 
                ? currentUser.getFirstName() + " " + currentUser.getLastName()
                : currentUser.getUsername();
            welcomeLabel.setText("Welcome, " + fullName + "!");
        }
    }

    /**
     * Handles navigation to Habits view.
     */
    @FXML
    public void handleHabitsNavigation() {
        try {
            // Load HabitView FXML
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/habits.fxml"));
            javafx.scene.layout.BorderPane root = loader.load();

            // Get the HabitView controller and set user context
            HabitView habitController = loader.getController();
            habitController.setUserAndStage(currentUser, primaryStage);

            // Create scene
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 900, 700);

            // Load stylesheet
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Update stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Wellness Tracker - Habits");
        } catch (Exception e) {
            System.err.println("Error loading habits view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles navigation to Mood Tracking view.
     */
    @FXML
    public void handleMoodNavigation() {
        try {
            // Load MoodTrackerView FXML
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/mood_tracker.fxml"));
            javafx.scene.layout.BorderPane root = loader.load();

            // Get the MoodTrackerView controller and set user context
            MoodTrackerView moodController = loader.getController();
            moodController.setUserAndStage(currentUser, primaryStage);

            // Create scene
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 900, 700);

            // Load stylesheet
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Update stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Wellness Tracker - Mood Tracking");
        } catch (Exception e) {
            System.err.println("Error loading mood tracker view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles navigation to Challenges view.
     */
    @FXML
    public void handleChallengesNavigation() {
        // TODO: Load challenges interface
        // TODO: Display available and active challenges
        System.out.println("Challenges view - coming soon");
    }

    /**
     * Handles navigation to Analytics view.
     */
    @FXML
    public void handleAnalyticsNavigation() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/analytics.fxml"));
            javafx.scene.layout.BorderPane root = loader.load();

            AnalyticsView analyticsController = loader.getController();
            analyticsController.setUserAndStage(currentUser, primaryStage);

            javafx.scene.Scene scene = new javafx.scene.Scene(root, 1000, 800);

            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Wellness Tracker - Analytics");
            primaryStage.setWidth(1000);
            primaryStage.setHeight(800);
        } catch (Exception e) {
            System.err.println("Error loading analytics view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles logout action.
     */
    @FXML
    public void handleLogout() {
        try {
            // Clear user session
            currentUser = null;

            // Load login view
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/login.fxml"));
            javafx.scene.layout.BorderPane root = loader.load();

            // Get the LoginView controller and set the primary stage
            LoginView loginController = loader.getController();
            loginController.setPrimaryStage(primaryStage);

            // Create scene
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 800, 600);

            // Load stylesheet
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Update stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Wellness Tracker - Login");
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the DashboardView controller.
     */
    @FXML
    public void initialize() {
        // Set button styles
        habitsButton.setStyle("-fx-font-size: 14; -fx-padding: 12; -fx-background-color: #3498db; -fx-text-fill: white;");
        moodButton.setStyle("-fx-font-size: 14; -fx-padding: 12; -fx-background-color: #9b59b6; -fx-text-fill: white;");
        challengesButton.setStyle("-fx-font-size: 14; -fx-padding: 12; -fx-background-color: #e67e22; -fx-text-fill: white;");
        logoutButton.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-background-color: #e74c3c; -fx-text-fill: white;");
    }
}

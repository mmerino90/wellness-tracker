package com.wellnesstracker.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.wellnesstracker.model.User;

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
    private Button analyticsButton;

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
        try {
            javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
            
            // Top bar with back button
            javafx.scene.layout.HBox topBar = new javafx.scene.layout.HBox(10);
            topBar.setStyle("-fx-padding: 15; -fx-background-color: #fff; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");
            javafx.scene.control.Button backButton = new javafx.scene.control.Button("< Back");
            backButton.setOnAction(e -> loadDashboard());
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Challenges");
            titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            topBar.getChildren().addAll(backButton, titleLabel);
            root.setTop(topBar);
            
            // Center with placeholder content
            javafx.scene.layout.VBox centerBox = new javafx.scene.layout.VBox(20);
            centerBox.setStyle("-fx-padding: 40; -fx-alignment: center;");
            javafx.scene.control.Label placeholderLabel = new javafx.scene.control.Label("Challenges Coming Soon!\n\nTrack your wellness challenges and achievements.");
            placeholderLabel.setStyle("-fx-font-size: 16; -fx-text-alignment: center;");
            centerBox.getChildren().add(placeholderLabel);
            root.setCenter(centerBox);
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 900, 700);
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Wellness Tracker - Challenges");
        } catch (Exception e) {
            System.err.println("Error loading challenges view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles navigation to Profile view.
     */
    @FXML
    public void handleProfileNavigation() {
        try {
            javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
            
            // Top bar with back button
            javafx.scene.layout.HBox topBar = new javafx.scene.layout.HBox(10);
            topBar.setStyle("-fx-padding: 15; -fx-background-color: #fff; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");
            javafx.scene.control.Button backButton = new javafx.scene.control.Button("< Back");
            backButton.setOnAction(e -> loadDashboard());
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Profile");
            titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            topBar.getChildren().addAll(backButton, titleLabel);
            root.setTop(topBar);
            
            // Center with user info
            javafx.scene.layout.VBox centerBox = new javafx.scene.layout.VBox(20);
            centerBox.setStyle("-fx-padding: 40; -fx-alignment: top_center;");
            
            if (currentUser != null) {
                javafx.scene.control.Label userLabel = new javafx.scene.control.Label("User: " + currentUser.getUsername());
                userLabel.setStyle("-fx-font-size: 14;");
                javafx.scene.control.Label emailLabel = new javafx.scene.control.Label("Email: " + currentUser.getEmail());
                emailLabel.setStyle("-fx-font-size: 14;");
                centerBox.getChildren().addAll(userLabel, emailLabel);
            }
            root.setCenter(centerBox);
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 900, 700);
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Wellness Tracker - Profile");
        } catch (Exception e) {
            System.err.println("Error loading profile view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles navigation to Settings view.
     */
    @FXML
    public void handleSettingsNavigation() {
        try {
            javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
            
            // Top bar with back button
            javafx.scene.layout.HBox topBar = new javafx.scene.layout.HBox(10);
            topBar.setStyle("-fx-padding: 15; -fx-background-color: #fff; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");
            javafx.scene.control.Button backButton = new javafx.scene.control.Button("< Back");
            backButton.setOnAction(e -> loadDashboard());
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Settings");
            titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            topBar.getChildren().addAll(backButton, titleLabel);
            root.setTop(topBar);
            
            // Center with placeholder content
            javafx.scene.layout.VBox centerBox = new javafx.scene.layout.VBox(20);
            centerBox.setStyle("-fx-padding: 40; -fx-alignment: center;");
            javafx.scene.control.Label placeholderLabel = new javafx.scene.control.Label("Settings Coming Soon!\n\nManage your preferences and account settings.");
            placeholderLabel.setStyle("-fx-font-size: 16; -fx-text-alignment: center;");
            centerBox.getChildren().add(placeholderLabel);
            root.setCenter(centerBox);
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 900, 700);
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Wellness Tracker - Settings");
        } catch (Exception e) {
            System.err.println("Error loading settings view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the dashboard view.
     */
    private void loadDashboard() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/main.fxml"));
            javafx.scene.layout.BorderPane root = loader.load();

            DashboardView dashboardController = loader.getController();
            dashboardController.setUserAndStage(currentUser, primaryStage);

            javafx.scene.Scene scene = new javafx.scene.Scene(root, 1000, 700);

            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Wellness Tracker - Dashboard");
        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
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
        // Debug: Print button status
        System.out.println("Initializing DashboardView...");
        System.out.println("Habits Button: " + habitsButton);
        System.out.println("Mood Button: " + moodButton);
        System.out.println("Analytics Button: " + analyticsButton);
        System.out.println("Challenges Button: " + challengesButton);
        System.out.println("Logout Button: " + logoutButton);
        
        // Set button styles
        if (habitsButton != null) {
            habitsButton.setStyle("-fx-font-size: 14; -fx-padding: 12; -fx-background-color: #3498db; -fx-text-fill: white;");
        }
        if (moodButton != null) {
            moodButton.setStyle("-fx-font-size: 14; -fx-padding: 12; -fx-background-color: #9b59b6; -fx-text-fill: white;");
        }
        if (analyticsButton != null) {
            analyticsButton.setStyle("-fx-font-size: 14; -fx-padding: 12; -fx-background-color: #27ae60; -fx-text-fill: white;");
        }
        if (challengesButton != null) {
            challengesButton.setStyle("-fx-font-size: 14; -fx-padding: 12; -fx-background-color: #e67e22; -fx-text-fill: white;");
        }
        if (logoutButton != null) {
            logoutButton.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-background-color: #e74c3c; -fx-text-fill: white;");
        }
    }
}

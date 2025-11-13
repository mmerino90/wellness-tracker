package com.wellnesstracker.javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import com.wellnesstracker.model.User;
import com.wellnesstracker.controller.UserController;

/**
 * Controller for the Login screen.
 * Handles user authentication and navigation to dashboard upon successful login.
 */
public class LoginView {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorLabel;

    private UserController userController;
    private Stage primaryStage;
    private User currentUser;

    /**
     * Constructor initializes the UserController.
     */
    public LoginView() {
        this.userController = new UserController();
    }

    /**
     * Sets the primary stage for scene transitions.
     * @param stage the primary application stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Handles login button click event.
     * Validates user credentials and transitions to dashboard.
     */
    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        User user = userController.authenticate(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("User logged in: " + user.getUsername());
            errorLabel.setText("");

            try {
                // Load dashboard view
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/main.fxml"));
                javafx.scene.layout.BorderPane root = loader.load();

                // Get the DashboardView controller and set user context
                DashboardView dashboardController = loader.getController();
                dashboardController.setUserAndStage(currentUser, primaryStage);

                // Create scene
                javafx.scene.Scene scene = new javafx.scene.Scene(root, 900, 700);

                // Load stylesheet
                String css = getClass().getResource("/style.css").toExternalForm();
                scene.getStylesheets().add(css);

                // Update stage
                primaryStage.setScene(scene);
                primaryStage.setTitle("Wellness Tracker - Dashboard");
                primaryStage.setWidth(900);
                primaryStage.setHeight(700);
            } catch (Exception e) {
                showError("Error loading dashboard: " + e.getMessage());
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Invalid username or password");
        }
    }

    /**
     * Handles register button click event.
     * Transitions to registration screen.
     */
    @FXML
    public void handleRegister() {
        try {
            // Load registration view
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/register.fxml"));
            javafx.scene.layout.BorderPane root = loader.load();

            // Get the RegisterView controller and set the primary stage
            RegisterView registerController = loader.getController();
            registerController.setPrimaryStage(primaryStage);

            // Create scene
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 800, 700);

            // Load stylesheet
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Update stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Wellness Tracker - Register");
        } catch (Exception e) {
            showError("Error loading registration screen: " + e.getMessage());
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shows an error message in the error label.
     * @param message the error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Gets the currently logged-in user.
     * @return the User object
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Initializes the LoginView controller.
     */
    @FXML
    public void initialize() {
        // TODO: Set up event handlers
        // TODO: Initialize UI components
    }
}

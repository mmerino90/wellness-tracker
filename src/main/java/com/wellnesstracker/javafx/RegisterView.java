package com.wellnesstracker.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.wellnesstracker.model.User;
import com.wellnesstracker.controller.UserController;
import com.wellnesstracker.utils.PasswordUtil;

/**
 * Controller for the Registration screen.
 * Handles new user registration with validation and password strength checking.
 */
public class RegisterView {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private CheckBox termsCheckBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Label usernameError;

    @FXML
    private Label emailError;

    @FXML
    private Label passwordError;

    @FXML
    private Label confirmError;

    @FXML
    private Label passwordStrengthLabel;

    @FXML
    private ProgressBar passwordStrengthBar;

    private UserController userController;
    private Stage primaryStage;

    /**
     * Constructor initializes the UserController.
     */
    public RegisterView() {
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
     * Handles registration button click event.
     * Validates all input fields and creates new user account.
     */
    @FXML
    public void handleRegister() {
        // Clear previous error messages
        clearErrors();

        // Get input values
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate all fields
        boolean isValid = true;

        // Username validation
        if (username.isEmpty()) {
            showError(usernameError, "Username is required");
            isValid = false;
        } else if (username.length() < 3) {
            showError(usernameError, "Username must be at least 3 characters");
            isValid = false;
        } else if (username.length() > 50) {
            showError(usernameError, "Username cannot exceed 50 characters");
            isValid = false;
        }

        // Email validation
        if (email.isEmpty()) {
            showError(emailError, "Email is required");
            isValid = false;
        } else if (!isValidEmail(email)) {
            showError(emailError, "Invalid email format");
            isValid = false;
        }

        // Password validation
        if (password.isEmpty()) {
            showError(passwordError, "Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            showError(passwordError, "Password must be at least 6 characters");
            isValid = false;
        }

        // Confirm password validation
        if (!password.equals(confirmPassword)) {
            showError(confirmError, "Passwords do not match");
            isValid = false;
        }

        // Terms and conditions validation
        if (!termsCheckBox.isSelected()) {
            errorLabel.setText("You must agree to the Terms and Conditions");
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Attempt registration
        try {
            User newUser = userController.registerUser(username, email, password, firstName, lastName);

            if (newUser != null) {
                // Success - show confirmation and navigate to login
                errorLabel.setText("Account created successfully! Redirecting to login...");
                errorLabel.setStyle("-fx-text-fill: #27ae60;");
                registerButton.setDisable(true);

                // Transition to login view after 2 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            handleCancel();
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            } else {
                // Check for specific errors
                User existingUser = userController.getUserByUsername(username);
                if (existingUser != null) {
                    errorLabel.setText("Username already exists");
                } else {
                    User existingEmail = userController.getUserByEmail(email);
                    if (existingEmail != null) {
                        errorLabel.setText("Email already registered");
                    } else {
                        errorLabel.setText("Registration failed. Please try again.");
                    }
                }
                errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        } catch (Exception e) {
            errorLabel.setText("An error occurred during registration: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles cancel button click event.
     * Returns to login screen.
     */
    @FXML
    public void handleCancel() {
        try {
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
            System.err.println("Error loading login view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles login link click event.
     * Transitions to login screen.
     */
    @FXML
    public void handleLoginLink() {
        handleCancel();
    }

    /**
     * Updates password strength indicator based on password criteria.
     */
    @FXML
    public void updatePasswordStrength() {
        String password = passwordField.getText();
        double strength = 0;
        int requirements = 0;

        // Check password length
        if (password.length() >= 6) {
            requirements++;
        }
        if (password.length() >= 10) {
            requirements++;
        }

        // Check for uppercase letters
        if (password.matches(".*[A-Z].*")) {
            requirements++;
        }

        // Check for lowercase letters
        if (password.matches(".*[a-z].*")) {
            requirements++;
        }

        // Check for numbers
        if (password.matches(".*\\d.*")) {
            requirements++;
        }

        // Check for special characters
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?].*")) {
            requirements++;
        }

        // Calculate strength percentage
        strength = (requirements / 6.0) * 100;

        // Update progress bar
        passwordStrengthBar.setProgress(strength / 100.0);

        // Update strength label and color
        if (strength < 30) {
            passwordStrengthLabel.setText("Password Strength: Very Weak");
            passwordStrengthBar.setStyle("-fx-accent: #e74c3c;");
        } else if (strength < 50) {
            passwordStrengthLabel.setText("Password Strength: Weak");
            passwordStrengthBar.setStyle("-fx-accent: #e67e22;");
        } else if (strength < 70) {
            passwordStrengthLabel.setText("Password Strength: Fair");
            passwordStrengthBar.setStyle("-fx-accent: #f39c12;");
        } else if (strength < 90) {
            passwordStrengthLabel.setText("Password Strength: Good");
            passwordStrengthBar.setStyle("-fx-accent: #27ae60;");
        } else {
            passwordStrengthLabel.setText("Password Strength: Strong");
            passwordStrengthBar.setStyle("-fx-accent: #2ecc71;");
        }
    }

    /**
     * Validates email format using basic regex.
     * @param email the email to validate
     * @return true if email format is valid
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    /**
     * Shows error message in a label.
     * @param label the label to display error in
     * @param message the error message
     */
    private void showError(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11;");
    }

    /**
     * Clears all error messages.
     */
    private void clearErrors() {
        usernameError.setText("");
        emailError.setText("");
        passwordError.setText("");
        confirmError.setText("");
        errorLabel.setText("");
    }

    /**
     * Initializes the RegisterView controller.
     */
    @FXML
    public void initialize() {
        // Attach password strength listener
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordStrength();
        });

        // Set initial button states
        registerButton.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-background-color: #27ae60; -fx-text-fill: white;");
        cancelButton.setStyle("-fx-font-size: 14; -fx-background-color: #95a5a6; -fx-text-fill: white;");
    }
}

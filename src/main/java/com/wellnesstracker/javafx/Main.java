package com.wellnesstracker.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.wellnesstracker.database.DatabaseManager;
import com.wellnesstracker.javafx.LoginView;

/**
 * Entry point for the Wellness Tracker JavaFX application.
 * This class initializes and launches the main application window.
 */
public class Main extends Application {

    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 800;

    /**
     * Starts the JavaFX application and displays the login screen.
     * @param primaryStage the primary stage for this application
     * @throws Exception if there is an error during application startup
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize database
        System.out.println("Initializing database...");
        DatabaseManager.initialize();
        
        // Load login view
        System.out.println("Loading FXML...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        BorderPane root = loader.load();
        
        // Get the LoginView controller and set the primary stage
        LoginView loginController = loader.getController();
        loginController.setPrimaryStage(primaryStage);
        
        // Create scene
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Load stylesheet
        String css = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        // Configure primary stage
        primaryStage.setTitle("Wellness Tracker");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.show();
        
        // Handle window close event
        primaryStage.setOnCloseRequest(event -> {
            DatabaseManager.closeConnection();
            System.exit(0);
        });
    }

    /**
     * Main method to launch the JavaFX application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("Launching Wellness Tracker...");
        launch(args);
    }
}

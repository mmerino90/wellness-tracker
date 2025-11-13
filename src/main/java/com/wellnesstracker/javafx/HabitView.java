package com.wellnesstracker.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import com.wellnesstracker.model.User;
import com.wellnesstracker.model.Habit;
import com.wellnesstracker.controller.HabitController;
import java.util.List;

/**
 * Controller for the Habit Management view.
 * Handles creation, editing, deletion, and tracking of habits.
 */
public class HabitView {

    @FXML
    private TextField habitNameField;

    @FXML
    private TextArea habitDescriptionField;

    @FXML
    private ComboBox<String> categoryCombo;

    @FXML
    private ComboBox<String> frequencyCombo;

    @FXML
    private Button createHabitButton;

    @FXML
    private Button clearButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label habitNameError;

    @FXML
    private TableView<Habit> habitsTable;

    @FXML
    private TableColumn<Habit, String> nameColumn;

    @FXML
    private TableColumn<Habit, String> categoryColumn;

    @FXML
    private TableColumn<Habit, String> frequencyColumn;

    @FXML
    private TableColumn<Habit, String> streakColumn;

    @FXML
    private TableColumn<Habit, String> lastCompletedColumn;

    @FXML
    private TableColumn<Habit, String> actionsColumn;

    @FXML
    private Label emptyLabel;

    @FXML
    private Label totalHabitsLabel;

    @FXML
    private Label bestStreakLabel;

    @FXML
    private Label avgCompletionLabel;

    @FXML
    private TextField searchField;

    private HabitController habitController;
    private User currentUser;
    private Stage primaryStage;
    private ObservableList<Habit> allHabits;

    /**
     * Constructor initializes the HabitController.
     */
    public HabitView() {
        this.habitController = new HabitController();
    }

    /**
     * Sets the current user and primary stage.
     * @param user the logged-in user
     * @param stage the primary application stage
     */
    public void setUserAndStage(User user, Stage stage) {
        this.currentUser = user;
        this.primaryStage = stage;
        refreshHabitsList();
        refreshStatistics();
    }

    /**
     * Handles adding a new habit.
     */
    @FXML
    public void handleCreateHabit() {
        // Validate input
        habitNameError.setText("");

        String habitName = habitNameField.getText().trim();
        if (habitName.isEmpty()) {
            showError(habitNameError, "Habit name is required");
            return;
        }

        if (categoryCombo.getValue() == null || categoryCombo.getValue().equals("Select category...")) {
            showStatus("Please select a category", true);
            return;
        }

        if (frequencyCombo.getValue() == null) {
            showStatus("Please select a frequency", true);
            return;
        }

        try {
            // Create habit
            Habit habit = new Habit();
            habit.setUserId(currentUser.getUserId());
            habit.setHabitName(habitName);
            habit.setDescription(habitDescriptionField.getText());
            habit.setCategory(categoryCombo.getValue());
            habit.setFrequency(frequencyCombo.getValue());

            // Save to database
            int habitId = habitController.createHabit(habit);

            if (habitId > 0) {
                showStatus("✓ Habit created successfully!", false);
                handleClear();
                refreshHabitsList();
                refreshStatistics();
            } else {
                showStatus("Error: Failed to create habit", true);
            }
        } catch (Exception e) {
            showStatus("Error: " + e.getMessage(), true);
            System.err.println("Error creating habit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles clearing the form.
     */
    @FXML
    public void handleClear() {
        habitNameField.clear();
        habitDescriptionField.clear();
        categoryCombo.setValue("Select category...");
        frequencyCombo.setValue(null);
        habitNameError.setText("");
    }

    /**
     * Handles adding a new habit via button.
     */
    @FXML
    public void handleAddNewHabit() {
        // Scroll to top to show the form
        handleClear();
    }

    /**
     * Handles back button click.
     */
    @FXML
    public void handleBack() {
        try {
            // Load dashboard view
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/main.fxml"));
            javafx.scene.layout.BorderPane root = loader.load();

            // Get the DashboardView controller
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
        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the habits list table.
     */
    private void refreshHabitsList() {
        try {
            if (currentUser == null) return;

            List<Habit> habits = habitController.getUserHabits(currentUser.getUserId());

            if (habits != null && !habits.isEmpty()) {
                // Store all habits for search filtering
                allHabits = FXCollections.observableArrayList(habits);
                habitsTable.setItems(allHabits);
                emptyLabel.setText("");
                
                // Clear search field
                if (searchField != null) {
                    searchField.clear();
                }

                // Configure columns
                nameColumn.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getHabitName())
                );
                categoryColumn.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory() != null ? cellData.getValue().getCategory() : "")
                );
                frequencyColumn.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFrequency())
                );
                streakColumn.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getStreakCount()) + " days")
                );
            } else {
                habitsTable.setItems(FXCollections.observableArrayList());
                emptyLabel.setText("No habits yet. Create one to get started!");
            }
        } catch (Exception e) {
            System.err.println("Error refreshing habits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Refreshes habit statistics.
     */
    private void refreshStatistics() {
        try {
            if (currentUser == null) return;

            List<Habit> habits = habitController.getAllUserHabits(currentUser.getUserId());
            
            if (habits != null) {
                // Total habits
                totalHabitsLabel.setText(String.valueOf(habits.size()));

                // Best streak
                int bestStreak = 0;
                double totalCompletion = 0;
                int habitCount = 0;

                for (Habit habit : habits) {
                    if (habit.getStreakCount() > bestStreak) {
                        bestStreak = habit.getStreakCount();
                    }
                    if (habit.isActive()) {
                        double rate = habitController.getCompletionRate(habit.getHabitId(), 30);
                        totalCompletion += rate;
                        habitCount++;
                    }
                }

                bestStreakLabel.setText(bestStreak + " days");
                
                double avgCompletion = habitCount > 0 ? totalCompletion / habitCount : 0;
                avgCompletionLabel.setText(String.format("%.0f%%", avgCompletion));
            }
        } catch (Exception e) {
            System.err.println("Error refreshing statistics: " + e.getMessage());
            e.printStackTrace();
        }
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
     * Shows status message with color coding.
     * @param message the message to display
     * @param isError whether this is an error message
     */
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (isError ? "#e74c3c" : "#27ae60") + "; -fx-font-size: 12;");

        // Auto-clear success messages after 3 seconds
        if (!isError) {
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> statusLabel.setText(""));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    /**
     * Initializes the HabitView controller.
     */
    @FXML
    public void initialize() {
        // Set button styles
        createHabitButton.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-background-color: #27ae60; -fx-text-fill: white;");
        clearButton.setStyle("-fx-font-size: 13; -fx-background-color: #95a5a6; -fx-text-fill: white;");
        
        // Add search listener for real-time filtering
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterHabits(newValue);
            });
        }
    }

    /**
     * Filters habits based on search term.
     * @param searchTerm the search term to filter by
     */
    private void filterHabits(String searchTerm) {
        if (allHabits == null || allHabits.isEmpty()) {
            return;
        }
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            // Show all habits if search is empty
            habitsTable.setItems(allHabits);
        } else {
            // Filter habits by name or category
            String lowerSearch = searchTerm.toLowerCase();
            ObservableList<Habit> filteredHabits = FXCollections.observableArrayList(
                allHabits.stream()
                    .filter(habit -> habit.getHabitName().toLowerCase().contains(lowerSearch) ||
                                   (habit.getCategory() != null && habit.getCategory().toLowerCase().contains(lowerSearch)))
                    .toList()
            );
            habitsTable.setItems(filteredHabits);
        }
    }
}

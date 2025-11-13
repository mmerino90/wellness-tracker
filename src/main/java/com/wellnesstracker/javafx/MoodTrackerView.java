package com.wellnesstracker.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import com.wellnesstracker.model.MoodEntry;
import com.wellnesstracker.model.User;
import com.wellnesstracker.controller.MoodController;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the Mood Tracker view.
 * Handles mood entry creation, viewing history, and mood statistics.
 */
public class MoodTrackerView {

    @FXML
    private Slider moodLevelSlider;

    @FXML
    private Label moodValueLabel;

    @FXML
    private ComboBox<String> emotionalContextCombo;

    @FXML
    private ComboBox<String> energyLevelCombo;

    @FXML
    private ComboBox<String> dateRangeCombo;

    @FXML
    private TextArea activitiesArea;

    @FXML
    private TextArea notesArea;

    @FXML
    private Button saveMoodButton;

    @FXML
    private Button clearButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private TableView<MoodEntry> moodHistoryTable;

    @FXML
    private TableColumn<MoodEntry, String> dateColumn;

    @FXML
    private TableColumn<MoodEntry, String> moodColumn;

    @FXML
    private TableColumn<MoodEntry, String> emotionColumn;

    @FXML
    private TableColumn<MoodEntry, String> energyColumn;

    @FXML
    private TableColumn<MoodEntry, String> notesColumn;

    @FXML
    private TableColumn<MoodEntry, String> actionsColumn;

    @FXML
    private Label avgMoodLabel;

    @FXML
    private Label entryCountLabel;

    @FXML
    private Label commonMoodLabel;

    @FXML
    private TextField searchField;

    private MoodController moodController;
    private User currentUser;
    private Stage primaryStage;
    private ObservableList<MoodEntry> allMoodEntries;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Constructor initializes the MoodController.
     */
    public MoodTrackerView() {
        this.moodController = new MoodController();
    }

    /**
     * Sets the current user and primary stage.
     * @param user the logged-in user
     * @param stage the primary application stage
     */
    public void setUserAndStage(User user, Stage stage) {
        this.currentUser = user;
        this.primaryStage = stage;
    }

    /**
     * Handles saving a new mood entry.
     */
    @FXML
    public void handleSaveMood() {
        // Validate input
        if (currentUser == null) {
            showStatus("Error: User not authenticated", true);
            return;
        }

        if (emotionalContextCombo.getValue() == null || 
            emotionalContextCombo.getValue().equals("Select an emotion...")) {
            showStatus("Please select an emotional context", true);
            return;
        }

        if (energyLevelCombo.getValue() == null || 
            energyLevelCombo.getValue().equals("Select energy level...")) {
            showStatus("Please select an energy level", true);
            return;
        }

        try {
            // Create mood entry
            MoodEntry entry = new MoodEntry();
            entry.setUserId(currentUser.getUserId());
            entry.setMoodLevel(String.valueOf((int) moodLevelSlider.getValue()));
            entry.setEmotionalContext(emotionalContextCombo.getValue());
            entry.setEnergyLevel(energyLevelCombo.getValue());
            entry.setActivities(activitiesArea.getText());
            entry.setNotes(notesArea.getText());
            entry.setTimestamp(LocalDateTime.now().format(dateFormatter));

            // Save to database
            int entryId = moodController.createMoodEntry(entry);

            if (entryId > 0) {
                showStatus("✓ Mood entry saved successfully!", false);
                handleClear();
                refreshHistory();
                refreshStatistics();
            } else {
                showStatus("Error: Failed to save mood entry", true);
            }
        } catch (Exception e) {
            showStatus("Error: " + e.getMessage(), true);
            System.err.println("Error saving mood: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles clearing the form.
     */
    @FXML
    public void handleClear() {
        moodLevelSlider.setValue(5);
        emotionalContextCombo.setValue("Select an emotion...");
        energyLevelCombo.setValue("Select energy level...");
        activitiesArea.clear();
        notesArea.clear();
        statusLabel.setText("");
    }

    /**
     * Handles refreshing mood history.
     */
    @FXML
    public void handleRefreshHistory() {
        refreshHistory();
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
     * Refreshes the mood history table.
     */
    private void refreshHistory() {
        try {
            if (currentUser == null) return;

            String selectedRange = dateRangeCombo.getValue();
            List<MoodEntry> entries = null;

            if (selectedRange != null && selectedRange.contains("All")) {
                entries = moodController.getUserMoodEntries(currentUser.getUserId());
            } else {
                // Default to last 7 days if no specific range selected
                int days = 7;
                if (selectedRange != null) {
                    if (selectedRange.contains("14")) days = 14;
                    else if (selectedRange.contains("30")) days = 30;
                }

                LocalDate startDate = LocalDate.now().minusDays(days);
                LocalDate endDate = LocalDate.now();
                entries = moodController.getMoodEntriesByDateRange(
                    currentUser.getUserId(),
                    startDate.format(dateOnlyFormatter),
                    endDate.format(dateOnlyFormatter)
                );
            }

            // Populate table
            if (entries != null) {
                // Store all entries for search filtering
                allMoodEntries = FXCollections.observableArrayList(entries);
                moodHistoryTable.setItems(allMoodEntries);
                
                // Clear search field
                if (searchField != null) {
                    searchField.clear();
                }

                // Configure columns if not already done
                if (dateColumn.getCellValueFactory() == null) {
                    dateColumn.setCellValueFactory(cellData -> 
                        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTimestamp())
                    );
                    moodColumn.setCellValueFactory(cellData -> 
                        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMoodLevel())
                    );
                    emotionColumn.setCellValueFactory(cellData -> 
                        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmotionalContext())
                    );
                    energyColumn.setCellValueFactory(cellData -> 
                        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEnergyLevel())
                    );
                    notesColumn.setCellValueFactory(cellData -> 
                        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNotes() != null ? cellData.getValue().getNotes() : "")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error refreshing history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Refreshes mood statistics.
     */
    private void refreshStatistics() {
        try {
            if (currentUser == null) return;

            // Get average mood (last 7 days)
            double avgMood = moodController.getAverageMood(currentUser.getUserId(), 7);
            avgMoodLabel.setText(String.format("%.1f", avgMood));

            // Get entry count (last 7 days)
            int entryCount = moodController.getMoodEntryCount(currentUser.getUserId(), 7);
            entryCountLabel.setText(String.valueOf(entryCount));

            // Get most common mood (last 7 days)
            String commonMood = moodController.getMostCommonMood(currentUser.getUserId(), 7);
            commonMoodLabel.setText(commonMood != null ? commonMood : "N/A");
        } catch (Exception e) {
            System.err.println("Error refreshing statistics: " + e.getMessage());
            e.printStackTrace();
        }
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
     * Initializes the MoodTrackerView controller.
     */
    @FXML
    public void initialize() {
        // Set current date
        dateLabel.setText(LocalDate.now().format(dateOnlyFormatter));

        // Initialize mood level slider listener
        moodLevelSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            moodValueLabel.setText(String.valueOf(newValue.intValue()));
        });

        // Initialize date range combo box
        if (dateRangeCombo.getValue() == null) {
            dateRangeCombo.setValue("Last 7 days");
        }
        
        // Add search listener for real-time filtering
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterMoodEntries(newValue);
            });
        }
    }

    /**
     * Filters mood entries based on search term.
     * @param searchTerm the search term to filter by
     */
    private void filterMoodEntries(String searchTerm) {
        if (allMoodEntries == null || allMoodEntries.isEmpty()) {
            return;
        }
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            // Show all entries if search is empty
            moodHistoryTable.setItems(allMoodEntries);
        } else {
            // Filter entries by emotion, energy level, or notes
            String lowerSearch = searchTerm.toLowerCase();
            ObservableList<MoodEntry> filteredEntries = FXCollections.observableArrayList(
                allMoodEntries.stream()
                    .filter(entry -> entry.getEmotionalContext().toLowerCase().contains(lowerSearch) ||
                                   entry.getEnergyLevel().toLowerCase().contains(lowerSearch) ||
                                   (entry.getNotes() != null && entry.getNotes().toLowerCase().contains(lowerSearch)))
                    .toList()
            );
            moodHistoryTable.setItems(filteredEntries);
        }
    }

    /**
     * Load initial history and statistics.
     * Called after initialize.
     */
    private void loadInitialData() {
        refreshHistory();
        refreshStatistics();
    }
}

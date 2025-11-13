package com.wellnesstracker.javafx;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import com.wellnesstracker.model.User;
import com.wellnesstracker.model.MoodEntry;
import com.wellnesstracker.model.Habit;
import com.wellnesstracker.controller.MoodController;
import com.wellnesstracker.controller.HabitController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Analytics & Visualization view.
 * Displays comprehensive charts and statistics for mood and habit tracking.
 */
public class AnalyticsView {

    @FXML
    private ComboBox<String> timeRangeCombo;

    @FXML
    private Label totalMoodEntriesLabel;

    @FXML
    private Label averageMoodLabel;

    @FXML
    private Label highestMoodLabel;

    @FXML
    private Label lowestMoodLabel;

    @FXML
    private PieChart moodDistributionChart;

    @FXML
    private LineChart<String, Number> moodTrendsChart;

    @FXML
    private BarChart<String, Number> habitCompletionChart;

    @FXML
    private BarChart<String, Number> energyLevelChart;

    @FXML
    private BarChart<String, Number> emotionChart;

    private MoodController moodController;
    private HabitController habitController;
    private User currentUser;
    private Stage primaryStage;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Constructor initializes the controllers.
     */
    public AnalyticsView() {
        this.moodController = new MoodController();
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
        refreshAllCharts();
    }

    /**
     * Handles back button click.
     */
    @FXML
    public void handleBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/main.fxml"));
            javafx.scene.layout.BorderPane root = loader.load();

            DashboardView dashboardController = loader.getController();
            dashboardController.setUserAndStage(currentUser, primaryStage);

            javafx.scene.Scene scene = new javafx.scene.Scene(root, 900, 700);
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
     * Handles refresh button click.
     */
    @FXML
    public void handleRefresh() {
        refreshAllCharts();
    }

    /**
     * Refreshes all charts based on selected time range.
     */
    private void refreshAllCharts() {
        try {
            if (currentUser == null) return;

            int days = getSelectedDays();
            List<MoodEntry> moodEntries = getMoodEntriesForRange(days);

            updateOverviewStats(moodEntries);
            updateMoodDistributionChart(moodEntries);
            updateMoodTrendsChart(moodEntries);
            updateHabitCompletionChart();
            updateEnergyLevelChart(moodEntries);
            updateEmotionChart(moodEntries);
        } catch (Exception e) {
            System.err.println("Error refreshing charts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the number of days for the selected time range.
     */
    private int getSelectedDays() {
        String selected = timeRangeCombo.getValue();
        if (selected == null || selected.contains("7")) return 7;
        if (selected.contains("30")) return 30;
        if (selected.contains("90")) return 90;
        return Integer.MAX_VALUE; // All time
    }

    /**
     * Gets mood entries for the specified number of days.
     */
    private List<MoodEntry> getMoodEntriesForRange(int days) {
        if (days == Integer.MAX_VALUE) {
            return moodController.getUserMoodEntries(currentUser.getUserId());
        }
        LocalDate startDate = LocalDate.now().minusDays(days);
        return moodController.getMoodEntriesByDateRange(
            currentUser.getUserId(),
            startDate.format(dateFormatter),
            LocalDate.now().format(dateFormatter)
        );
    }

    /**
     * Updates overview statistics cards.
     */
    private void updateOverviewStats(List<MoodEntry> entries) {
        totalMoodEntriesLabel.setText(String.valueOf(entries.size()));

        if (entries.isEmpty()) {
            averageMoodLabel.setText("0.0");
            highestMoodLabel.setText("0");
            lowestMoodLabel.setText("0");
            return;
        }

        double sum = 0;
        int highest = 0;
        int lowest = 10;

        for (MoodEntry entry : entries) {
            try {
                int mood = Integer.parseInt(entry.getMoodLevel());
                sum += mood;
                if (mood > highest) highest = mood;
                if (mood < lowest) lowest = mood;
            } catch (NumberFormatException e) {
                // Skip non-numeric mood values
            }
        }

        double average = sum / entries.size();
        averageMoodLabel.setText(String.format("%.1f", average));
        highestMoodLabel.setText(String.valueOf(highest));
        lowestMoodLabel.setText(String.valueOf(lowest));
    }

    /**
     * Updates mood distribution pie chart.
     */
    private void updateMoodDistributionChart(List<MoodEntry> entries) {
        Map<String, Integer> moodCounts = new TreeMap<>();

        for (MoodEntry entry : entries) {
            String mood = entry.getMoodLevel();
            moodCounts.put(mood, moodCounts.getOrDefault(mood, 0) + 1);
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
            pieData.add(new PieChart.Data("Mood " + entry.getKey(), entry.getValue()));
        }

        moodDistributionChart.setData(pieData);
    }

    /**
     * Updates mood trends line chart.
     */
    private void updateMoodTrendsChart(List<MoodEntry> entries) {
        // Group entries by date
        Map<String, List<MoodEntry>> byDate = entries.stream()
            .collect(Collectors.groupingBy(e -> e.getTimestamp().substring(0, 10)));

        // Calculate average mood per day
        Map<String, Double> dailyAverages = new TreeMap<>();
        for (Map.Entry<String, List<MoodEntry>> entry : byDate.entrySet()) {
            double avg = entry.getValue().stream()
                .mapToDouble(e -> {
                    try {
                        return Double.parseDouble(e.getMoodLevel());
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                })
                .average()
                .orElse(0);
            dailyAverages.put(entry.getKey(), avg);
        }

        // Create series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Mood Average");

        for (Map.Entry<String, Double> entry : dailyAverages.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        moodTrendsChart.getData().clear();
        moodTrendsChart.getData().add(series);
    }

    /**
     * Updates habit completion bar chart.
     */
    private void updateHabitCompletionChart() {
        List<Habit> habits = habitController.getUserHabits(currentUser.getUserId());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Completion Rate (%)");

        for (Habit habit : habits) {
            double rate = habitController.getCompletionRate(habit.getHabitId(), 30);
            series.getData().add(new XYChart.Data<>(habit.getHabitName(), rate));
        }

        habitCompletionChart.getData().clear();
        habitCompletionChart.getData().add(series);
    }

    /**
     * Updates energy level distribution chart.
     */
    private void updateEnergyLevelChart(List<MoodEntry> entries) {
        Map<String, Integer> energyCounts = new HashMap<>();
        energyCounts.put("Low", 0);
        energyCounts.put("Medium", 0);
        energyCounts.put("High", 0);

        for (MoodEntry entry : entries) {
            String energy = entry.getEnergyLevel();
            if (energy != null) {
                energyCounts.put(energy, energyCounts.getOrDefault(energy, 0) + 1);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Frequency");

        for (Map.Entry<String, Integer> entry : energyCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        energyLevelChart.getData().clear();
        energyLevelChart.getData().add(series);
    }

    /**
     * Updates emotional context analysis chart.
     */
    private void updateEmotionChart(List<MoodEntry> entries) {
        Map<String, Integer> emotionCounts = new HashMap<>();

        for (MoodEntry entry : entries) {
            String emotion = entry.getEmotionalContext();
            if (emotion != null && !emotion.isEmpty()) {
                emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0) + 1);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Occurrences");

        // Sort by frequency descending
        emotionCounts.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(10) // Show top 10 emotions
            .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        emotionChart.getData().clear();
        emotionChart.getData().add(series);
    }

    /**
     * Initializes the AnalyticsView controller.
     */
    @FXML
    public void initialize() {
        // Set default time range
        if (timeRangeCombo.getValue() == null) {
            timeRangeCombo.setValue("Last 7 days");
        }

        // Add listener for time range changes
        timeRangeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (currentUser != null) {
                refreshAllCharts();
            }
        });
    }
}

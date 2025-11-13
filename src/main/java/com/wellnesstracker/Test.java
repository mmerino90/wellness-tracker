package com.wellnesstracker;

import com.wellnesstracker.database.DatabaseManager;

public class Test {
    public static void main(String[] args) {
        System.out.println("Starting test...");
        try {
            System.out.println("Initializing database...");
            DatabaseManager.initialize();
            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Test complete.");
    }
}

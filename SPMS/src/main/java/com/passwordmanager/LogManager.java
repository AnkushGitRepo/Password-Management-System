package com.passwordmanager;

public class LogManager {

    private StackDSA<String> activityLog;

    public void fetchLogs(User user) {
        activityLog = DatabaseConnection.getLogs(user.getUserName());
    }

    public void logActivity(String userName, String activity) {
        if (DatabaseConnection.userExists(userName)) {
            DatabaseConnection.saveLog(userName, activity);
        } else {
            System.err.println("Error: Attempted to log activity for a non-existent user.");
        }
    }

    public LogManager() {
        activityLog = new StackDSA<>(1000);
    }

    // Method to retrieve the activity logs for a user
    public StackDSA<String> getActivityLogs(String userName) {
        return DatabaseConnection.getLogs(userName); // Fetches logs from the database
    }

}

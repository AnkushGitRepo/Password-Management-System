package com.passwordmanager;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PasswordManager {
    public static Scanner scanner = new Scanner(System.in);
    private UserManager userManager;
    private PasswordService passwordService;
    private LogManager logManager;

    public PasswordManager() {
        this.userManager = new UserManager();
        this.passwordService = new PasswordService();
        this.logManager = new LogManager();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        displayWelcomeMessage();
        boolean exitProgram = false;

        while (!exitProgram) {
            System.out.println(Color.PURPLE + Color.BOLD + "     Secure Password Manager Main Menu" + Color.RESET);
            System.out.println(Color.GREEN +  "     1. Sign Up" + Color.RESET);
            System.out.println(Color.GREEN +  "     2. Login" + Color.RESET);
            System.out.println(Color.GREEN + "     3. Exit" + Color.RESET);
            System.out.print(Color.YELLOW + "     ENTER YOUR CHOICE: " + Color.RESET);
            int choice = getInputInt();

            switch (choice) {
                case 1:
                    userManager.signUp();
                    break;
                case 2:
                    if (userManager.login()) {
                        logManager.fetchLogs(userManager.getCurrentUser());
                        passwordService.loadUserPasswords(userManager.getCurrentUser());

                        boolean exitUserSession = false;
                        while (!exitUserSession) {
                            System.out.println(Color.PURPLE + Color.BOLD + "     "+userManager.getCurrentUser().getUserName().replaceAll("\\d","") +"'s Account Menu" + Color.RESET);
                            System.out.println(Color.GREEN +  "     1. Add Password");
                            System.out.println("     2. Search Password");
                            System.out.println("     3. Update Password");
                            System.out.println("     4. Delete Password");
                            System.out.println("     5. Export Details");
                            System.out.println("     6. Display All Passwords");
                            System.out.println("     7. History");
                            System.out.println("     8. Logout" + Color.RESET);
                            System.out.print(Color.YELLOW + "     ENTER YOUR CHOICE: " + Color.RESET);
                            int action = getInputInt();

                            switch (action) {
                                case 1:
                                    passwordService.addPassword(userManager.getCurrentUser());
                                    logManager.logActivity(userManager.getCurrentUser().getUserName(), "Added password entry");
                                    break;
                                case 2:
                                    passwordService.searchPassword(userManager.getCurrentUser());
                                    break;
                                case 3:
                                    passwordService.updatePassword(userManager.getCurrentUser());
                                    logManager.logActivity(userManager.getCurrentUser().getUserName(), "Updated password entry");
                                    break;
                                case 4:
                                    passwordService.deletePassword(userManager.getCurrentUser());
                                    logManager.logActivity(userManager.getCurrentUser().getUserName(), "Deleted password entry");
                                    break;
                                case 5:
                                    passwordService.exportDetails(userManager.getCurrentUser());
                                    break;
                                case 6:
                                    viewAllPasswords(userManager.getCurrentUser().getUserName()); // View All Passwords
                                    break;
                                case 7:
                                    viewActivityLog(userManager.getCurrentUser().getUserName()); // View Activity Log
                                    break;
                                case 8:
                                    System.out.println(Color.BLUE + Color.BOLD +"     Logging Out..."+Color.RESET);
                                    System.out.println();
                                    exitUserSession = true;  // End user session, go back to the main menu
                                    break;
                                default:
                                    System.out.println(Color.RED + Color.BOLD + "     Invalid choice." + Color.RESET);
                                    break;
                            }
                        }
                    }
                    break;
                case 3:
                    exitProgram = true;  // Exit the program
                    displayClosingMessage();
                    break;
                default:
                    System.out.println(Color.RED + Color.BOLD+  "     Invalid choice." +Color.RESET) ;
                    break;
            }
        }

        scanner.close();
    }

    private static void displayWelcomeMessage() {
        System.out.println(Color.CYAN + Color.BOLD + "     ********************************************" + Color.RESET);
        System.out.println(Color.CYAN + Color.BOLD + "     *                                          *" + Color.RESET);
        System.out.println(Color.CYAN + Color.BOLD + "     *  " + Color.YELLOW + "Welcome to the Secure Password Manager" + Color.CYAN + "  *" + Color.RESET);
        System.out.println(Color.CYAN + Color.BOLD + "     *                                          *" + Color.RESET);
        System.out.println(Color.CYAN + Color.BOLD + "     ********************************************" + Color.RESET);
        System.out.println();
    }

    private static void displayClosingMessage() {
        System.out.println(Color.CYAN + Color.BOLD + "     **************************************************" + Color.RESET);
        System.out.println(Color.CYAN + Color.BOLD + "     *                                                *" + Color.RESET);
        System.out.println(Color.CYAN + Color.BOLD + "     *  " + Color.YELLOW + "Thank you for using Secure Password Manager" + Color.CYAN + "   *" + Color.RESET);
        System.out.println(Color.CYAN + Color.BOLD + "     *                                                *" + Color.RESET);
        System.out.println(Color.CYAN + Color.BOLD + "     **************************************************" + Color.RESET);
        System.out.println();
    }

    private void viewActivityLog(String userName) {
        StackDSA<String> logs = logManager.getActivityLogs(userName);
        if (logs.isEmpty()) {
            System.out.println(Color.RED + Color.BOLD + "     No activities found." + Color.RESET);
        } else {
            // Temporary storage to calculate max lengths
            int maxLogIdLength = "Log ID".length();
            int maxActivityLength = "Activity".length();
            int maxTimestampLength = "Timestamp".length();

            StackDSA<String> tempStack = new StackDSA<>(logs.size());
            while (!logs.isEmpty()) {
                String logEntry = logs.pop();
                tempStack.push(logEntry);

                // Split the logEntry and calculate the max length of each column
                String[] logParts = logEntry.split(" \\| ");
                maxLogIdLength = Math.max(maxLogIdLength, logParts[0].replace("Log ID: ", "").trim().length());
                maxActivityLength = Math.max(maxActivityLength, logParts[2].replace("Activity: ", "").trim().length());
                maxTimestampLength = Math.max(maxTimestampLength, logParts[3].replace("Timestamp: ", "").trim().length());
            }

            // Rebuild the logs stack to display
            logs = tempStack;

            // Display UserName only once
            System.out.println(Color.BLUE + Color.BOLD + "     Activity Log for User: " + userName + Color.RESET);
            System.out.println(Color.YELLOW + "     " + "-".repeat(maxLogIdLength + maxActivityLength + maxTimestampLength + 12) + Color.RESET);
            System.out.printf(Color.YELLOW + "     | %-" + maxLogIdLength + "s | %-" + maxActivityLength + "s | %-" + maxTimestampLength + "s |%n", "Log ID", "Activity", "Timestamp" + Color.RESET);
            System.out.println(Color.YELLOW + "     " + "-".repeat(maxLogIdLength + maxActivityLength + maxTimestampLength + 12) + Color.RESET);

            // Print each log entry without the UserName
            while (!logs.isEmpty()) {
                String logEntry = logs.pop();
                String[] logParts = logEntry.split(" \\| ");
                String logId = logParts[0].replace("Log ID: ", "").trim();
                String activity = logParts[2].replace("Activity: ", "").trim();
                String timestamp = formatTimestamp(logParts[3].replace("Timestamp: ", "").trim());

                System.out.printf(Color.CYAN + "     | %-" + maxLogIdLength + "s | %-" + maxActivityLength + "s | %-" + maxTimestampLength + "s |%n" + Color.RESET, logId, activity, timestamp);
            }

            System.out.println(Color.YELLOW + "     " + "-".repeat(maxLogIdLength + maxActivityLength + maxTimestampLength + 12) + Color.RESET);
        }
        System.out.println();
    }

    private void viewAllPasswords(String userName) {
        LinkedListDSA<PasswordEntry> passwords = passwordService.getAllPasswords(userName);

        if (passwords.isEmpty()) {
            System.out.println(Color.RED + Color.BOLD + "     No passwords stored." + Color.RESET);
            System.out.println();
            return;
        }

        // Calculate the maximum lengths for dynamic formatting
        int maxSiteURLLength = "Site URL".length();
        int maxSiteNameLength = "Site Name".length();
        int maxUserNameLength = "Username".length();
        int maxPasswordLength = "Password".length();

        for (int i = 0; i < passwords.size(); i++) {
            PasswordEntry entry = passwords.get(i);
            maxSiteURLLength = Math.max(maxSiteURLLength, Math.min(entry.getSiteURL().length(), 30));
            maxSiteNameLength = Math.max(maxSiteNameLength, Math.min(entry.getSiteName().length(), 20));
            maxUserNameLength = Math.max(maxUserNameLength, Math.min(entry.getSiteUserName().length(), 20));
            maxPasswordLength = Math.max(maxPasswordLength, Math.min(passwordService.decryptPassword(entry.getEncryptedPassword(), entry.getEncryptionKey()).length(), 20));
        }

        int totalWidth = maxSiteURLLength + maxSiteNameLength + maxUserNameLength + maxPasswordLength + 13;

        // Print header
        System.out.println(Color.BLUE + Color.BOLD + "     All Stored Passwords:" + Color.RESET);
        System.out.println(Color.YELLOW + "     " + "-".repeat(totalWidth) + Color.RESET);
        System.out.printf(Color.YELLOW + "     | %-" + maxSiteURLLength + "s | %-" + maxSiteNameLength + "s | %-" + maxUserNameLength + "s | %-" + maxPasswordLength + "s |%n", "Site URL", "Site Name", "Username", "Password" + Color.RESET);
        System.out.println(Color.YELLOW + "     " + "-".repeat(totalWidth) + Color.RESET);

        // Print each password entry
        for (int i = 0; i < passwords.size(); i++) {
            PasswordEntry entry = passwords.get(i);
            String decryptedPassword = passwordService.decryptPassword(entry.getEncryptedPassword(), entry.getEncryptionKey());

            System.out.printf(Color.CYAN + "     | %-" + maxSiteURLLength + "s | %-" + maxSiteNameLength + "s | %-" + maxUserNameLength + "s | %-" + maxPasswordLength + "s |%n" + Color.RESET,
                    truncateString(entry.getSiteURL(), maxSiteURLLength),
                    truncateString(entry.getSiteName(), maxSiteNameLength),
                    truncateString(entry.getSiteUserName(), maxUserNameLength),
                    truncateString(decryptedPassword, maxPasswordLength));
        }

        System.out.println(Color.YELLOW + "     " + "-".repeat(totalWidth) + Color.RESET);
        System.out.println();
    }

    private String truncateString(String str, int maxLength) {
        return (str.length() > maxLength) ? str.substring(0, maxLength - 3) + "..." : str;
    }


    private String formatTimestamp(String timestamp) {
        try {
            // Parse the timestamp and format with 2 decimal precision for seconds
            Timestamp ts = Timestamp.valueOf(timestamp);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS").format(ts);
        } catch (IllegalArgumentException e) {
            return timestamp; // Return the original if parsing fails
        }
    }




    private static int getInputInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.print(Color.RED + Color.BOLD + "     Invalid input." + Color.RESET + Color.YELLOW + "\n     Please enter a valid number: " + Color.RESET);
                scanner.nextLine();
            }
        }
    }

    public static void main(String[] args) {
        PasswordManager manager = new PasswordManager();
        manager.run();
    }
}

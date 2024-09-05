package com.passwordmanager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class PasswordService {

    private LinkedListDSA<PasswordEntry> passwordList;

    public PasswordService() {
        passwordList = new LinkedListDSA<>();
    }

    public void loadUserPasswords(User user) {
        passwordList = DatabaseConnection.getPasswords(user.getUserName());
    }

    public LinkedListDSA<PasswordEntry> getAllPasswords(String userName) {
        // Retrieve passwords from the database for the given userName
        LinkedListDSA<PasswordEntry> passwordList = DatabaseConnection.getPasswords(userName);
        return passwordList;
    }

    public void addPassword(User user) {
        Scanner scanner = new Scanner(System.in);

        String siteURL, siteName, siteUserName, password;

        // Validate site URL
        while (true) {
            System.out.print(Color.YELLOW +  "     Enter site URL: "+Color.RESET );
            siteURL = scanner.nextLine().toLowerCase();
            if (siteURL == null || siteURL.isEmpty()) {
                System.out.println(Color.RED + Color.BOLD + "     Site URL cannot be null or empty. Please enter a valid site URL."+ Color.RESET);
            } else {
                break;
            }
        }

        // Validate site name
        while (true) {
            System.out.print(Color.YELLOW+ "     Enter site name: "+ Color.RESET);
            siteName = scanner.nextLine();
            if (siteName == null || siteName.isEmpty()) {
                System.out.println(Color.RED + Color.BOLD + "     Site name cannot be null or empty. Please enter a valid site name."+ Color.RESET);
            } else {
                break;
            }
        }

        // Validate site username
        while (true) {
            System.out.print(Color.YELLOW+"     Enter site username: "+ Color.RESET);
            siteUserName = scanner.nextLine();
            if (siteUserName == null || siteUserName.isEmpty()) {
                System.out.println(Color.RED + Color.BOLD + "     Site username cannot be null or empty. Please enter a valid site username."+ Color.RESET);
            } else {
                break;
            }
        }

        // Validate password
        while (true) {
            System.out.print(Color.YELLOW+"     Enter password: "+ Color.RESET);
            password = scanner.nextLine();
            if (password == null || password.isEmpty()) {
                System.out.println(Color.RED + Color.BOLD + "     Password cannot be null or empty. Please enter a valid password."+ Color.RESET);
            } else {
                break;
            }
        }

        // Check if the data already exists in the database
        if (passwordExists(user.getUserName(), siteURL, siteName, siteUserName)) {
            System.out.println(Color.RED + Color.BOLD + "     A password entry for this Site URL, Site Name, and Username already exists." + Color.RESET);
            System.out.println();
            return; // Abort the add password process
        }

        int encryptionKey = generateEncryptionKey();
        String encryptedPassword = encryptPassword(password, encryptionKey);

        PasswordEntry entry = new PasswordEntry(siteURL, siteName, siteUserName, encryptedPassword, encryptionKey);

        int generatedId = DatabaseConnection.savePassword(user.getUserName(), entry);
        if (generatedId > 0) {
            entry.setId(generatedId); // Set the generated ID to the entry
            passwordList.add(entry);
            System.out.println(Color.GREEN + Color.BOLD +"     Password added successfully!" + Color.RESET);
        } else {
            System.out.println(Color.RED + Color.BOLD +"     Failed to add password. Please try again." + Color.RESET);
        }
        System.out.println();
    }




    public void searchPassword(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.print(Color.YELLOW + "     Enter search criteria (Site URL or Site Name): " + Color.RESET);
        String criteria = scanner.nextLine();
        System.out.println(); // For new Line

        boolean found = false;
        for (int i = 0; i < passwordList.size(); i++) {
            PasswordEntry entry = passwordList.get(i);
            if (entry.getSiteURL().toLowerCase().startsWith(criteria) || entry.getSiteName().startsWith(criteria)) {
                String decryptedPassword = decryptPassword(entry.getEncryptedPassword(), entry.getEncryptionKey());
                System.out.println(Color.WHITE + Color.BOLD + "     *******************************************************" + Color.RESET);
                System.out.println(Color.BLUE + "          Site URL: " + entry.getSiteURL());
                System.out.println("          Site Name: " + entry.getSiteName());
                System.out.println("          Site Username: " + entry.getSiteUserName());
                System.out.println("          Password: " + decryptedPassword + Color.RESET);
                System.out.println(Color.WHITE + Color.BOLD + "     *******************************************************" + Color.RESET);
                found = true;
            }
        }

        if (!found) {
            System.out.println(Color.RED + Color.BOLD + "     No password found for the given criteria." + Color.RESET);
        }
        System.out.println();
    }


    public void updatePassword(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.print(Color.YELLOW+"     Enter site URL or site name to update: "+Color.RESET);
        String criteria = scanner.nextLine();

        boolean found = false;
        for (int i = 0; i < passwordList.size(); i++) {
            PasswordEntry entry = passwordList.get(i);
            if (entry.getSiteURL().equals(criteria) || entry.getSiteName().equals(criteria)) {

                String newPassword;
                while (true) {
                    System.out.print(Color.YELLOW+"     Enter password: "+ Color.RESET);
                    newPassword = scanner.nextLine();
                    if (newPassword == null || newPassword.isEmpty()) {
                        System.out.println(Color.RED + Color.BOLD + "     Password cannot be null or empty. Please enter a valid password."+ Color.RESET);
                    } else {
                        break;
                    }
                }

                int encryptionKey = generateEncryptionKey();
                String encryptedPassword = encryptPassword(newPassword, encryptionKey);

                entry.setEncryptedPassword(encryptedPassword);
                entry.setEncryptionKey(encryptionKey);

                DatabaseConnection.updatePassword(entry);
                System.out.println(Color.GREEN + "     Password updated successfully!"+Color.RESET);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println(Color.RED+ "     No password entry found for the given criteria." + Color.RESET);
        }
        System.out.println();
    }


    public void deletePassword(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.print(Color.YELLOW + "     Enter site URL or site name to delete: " + Color.RESET);
        String criteria = scanner.nextLine().toLowerCase();

        PasswordEntry entryToDelete = null;
        int entryIndex = -1;

        // Search for the entry by URL or site name
        for (int i = 0; i < passwordList.size(); i++) {
            PasswordEntry entry = passwordList.get(i);
            if (entry.getSiteURL().equalsIgnoreCase(criteria) || entry.getSiteName().equalsIgnoreCase(criteria)) {
                entryToDelete = entry;
                entryIndex = i;
                break;
            }
        }

        if (entryToDelete != null) {
            // If an entry is found, prompt the user for the main account password
            System.out.print(Color.YELLOW + "     Enter your main account password to confirm deletion: " + Color.RESET);
            String mainAccountPassword = scanner.nextLine();

            String decryptedPassword = decryptPassword(user.getEncryptedPassword(), user.getEncryptionKey());
            if (mainAccountPassword.equals(decryptedPassword)) {
                // If the password is correct, delete the entry from the list and database
                passwordList.remove(entryIndex); // Remove from LinkedList by index
                boolean isDeletedFromDB = DatabaseConnection.deletePassword(entryToDelete.getId()); // Remove from Database by ID

                if (isDeletedFromDB) {
                    System.out.println(Color.GREEN + Color.BOLD + "     Password entry deleted successfully!" + Color.RESET);
                } else {
                    System.out.println(Color.RED + Color.BOLD + "     Failed to delete password entry from the database." + Color.RESET);
                }
            } else {
                System.out.println(Color.RED + Color.BOLD + "     Incorrect main account password. Deletion aborted." + Color.RESET);
            }
        } else {
            System.out.println(Color.RED + "     No password entry found for the given criteria." + Color.RESET);
        }
        System.out.println();
    }





    public void exportDetails(User user) {
        StringBuilder data = new StringBuilder();
        data.append("User Details:\n");
        data.append("Username: ").append(user.getUserName()).append("\n");
        data.append("Email: ").append(user.getEmail()).append("\n");
        data.append("Phone Number: ").append(user.getPhoneNumber()).append("\n");
        data.append("Date of Birth: ").append(user.getDateOfBirth()).append("\n");
        data.append("\nStored Passwords:\n");

        for (int i = 0; i < passwordList.size(); i++) {
            PasswordEntry entry = passwordList.get(i);
            String decryptedPassword = decryptPassword(entry.getEncryptedPassword(), entry.getEncryptionKey());
            data.append("Site URL: ").append(entry.getSiteURL()).append("\n");
            data.append("Site Name: ").append(entry.getSiteName()).append("\n");
            data.append("Site Username: ").append(entry.getSiteUserName()).append("\n");
            data.append("Password: ").append(decryptedPassword).append("\n");
            data.append("\n");
        }

        String fileName = user.getUserName() + ".txt";
        writeToFile(fileName, data.toString());
        System.out.println(Color.GREEN+"     Details exported to " + fileName + Color.RESET);
        System.out.println();
    }

    // Utility methods

    private int generateEncryptionKey() {
        return new Random().nextInt(9) + 1; // Generates a number between 1 and 9
    }

    public boolean passwordExists(String userName, String siteURL, String siteName, String siteUserName) {
        return DatabaseConnection.passwordExists(userName, siteURL, siteName, siteUserName);
    }

    private String encryptPassword(String password, int key) {
        StringBuilder reversedPassword = new StringBuilder(password).reverse();
        StringBuilder encryptedPassword = new StringBuilder();

        for (char ch : reversedPassword.toString().toCharArray()) {
            encryptedPassword.append((char) (ch + key));
        }

        return encryptedPassword.toString();
    }

    public String decryptPassword(String encryptedPassword, int key) {
        StringBuilder decryptedPassword = new StringBuilder();

        for (char ch : encryptedPassword.toCharArray()) {
            decryptedPassword.append((char) (ch - key));
        }

        return decryptedPassword.reverse().toString();
    }

    public static void writeToFile(String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println(Color.RED+ Color.BOLD+ "     ERROR: While Writing Data Into File!" + Color.RESET);
        }
    }
}

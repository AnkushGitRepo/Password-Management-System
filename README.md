# Secure Password Manager

## Overview
The Secure Password Manager is a console-based application developed in Java that provides a secure and user-friendly way to store, manage, and retrieve passwords. This system leverages custom encryption and decryption algorithms, ensuring that user data is protected from unauthorized access. It also includes features such as user authentication, activity logging, and dynamic search functionality.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Data Structures](#data-structures)
- [Database Schema](#database-schema)
- [Encryption and Decryption Process](#encryption-and-decryption-process)
- [Contributing](#contributing)

## Features
- **User Registration (Sign Up):** Securely register users by collecting personal details and storing encrypted passwords.
- **User Authentication (Login):** Authenticate users to access their stored passwords and account features.
- **Add Password:** Securely add and store new passwords with encryption.
- **Search Password:** Search for specific passwords using partial or complete site URLs or names.
- **Update Password:** Update existing passwords with encryption.
- **Delete Password:** Securely delete stored passwords after verification.
- **Export Passwords:** Export all stored passwords and account details to a file.
- **View All Passwords:** Display all stored passwords in a formatted table, including site URLs, usernames, and decrypted passwords.
- **Activity Logging:** Log user activities such as adding, updating, or deleting passwords.
- **View Activity Log:** View a detailed log of user activities, including timestamps.
- **Logout:** Securely end the user session.

## Prerequisites
- **Java JDK 11+**
- **PostgreSQL 13+**
- **Maven** (optional, for managing dependencies)

## Installation
1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/secure-password-manager.git
   cd secure-password-manager
   ```
2. **Set Up the Database:**
   - Create a PostgreSQL database using the following commands:
     ```sql
     CREATE DATABASE password_manager_db;
     ```
   - Run the provided SQL scripts to set up the schema:
     ```sql
     psql -U your_username -d password_manager_db -f schema.sql
     ```
   - Update the database connection details in the `DatabaseConnection` class:
     ```java
     private static final String URL = "jdbc:postgresql://localhost:5432/password_manager_db";
     private static final String USER = "your_db_username";
     private static final String PASSWORD = "your_db_password";
     ```

3. **Compile the Project:**
   ```bash
   javac -d bin src/com/passwordmanager/*.java
   ```

4. **Run the Application:**
   ```bash
   java -cp bin com.passwordmanager.PasswordManager
   ```

## Usage
Upon running the application, you will be greeted with a welcome message and a main menu. The options include:
- **1. Sign Up:** Register a new user.
- **2. Login:** Login with existing credentials.
- **3. Exit:** Close the application.

### After Login:
- **Add Password:** Enter details to securely store a new password.
- **Search Password:** Search for passwords by entering part of the site URL or name.
- **Update Password:** Modify existing passwords.
- **Delete Password:** Remove a password entry after verifying the account password.
- **Export Details:** Export all stored passwords to a `.txt` file.
- **Display All Passwords:** View all stored passwords in a formatted table.
- **History:** View the activity log for all actions performed.
- **Logout:** Safely end your session and return to the main menu.

## Data Structures
### LinkedListDSA
- **Purpose:** Custom implementation of a singly linked list for storing password entries and other sequential data.
- **Usage:** Stores and manages collections of data, allowing for efficient insertion, retrieval, and deletion.

## StackDSA
- **Purpose:** Custom implementation of a stack for managing the activity log.
- **Usage:** Follows the Last-In-First-Out (LIFO) principle, storing the most recent user activities at the top.

## Database Schema

### Users Table
```sql
CREATE TABLE Users (
    userName VARCHAR PRIMARY KEY,
    email VARCHAR UNIQUE NOT NULL,
    phoneNumber VARCHAR NOT NULL,
    dateOfBirth DATE NOT NULL,
    encryptedPassword VARCHAR NOT NULL,
    encryptionKey INT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### PasswordEntry Table
```sql
CREATE TABLE PasswordEntry (
    id SERIAL PRIMARY KEY,
    userName VARCHAR REFERENCES Users(userName),
    siteURL VARCHAR NOT NULL,
    siteName VARCHAR NOT NULL,
    siteUserName VARCHAR NOT NULL,
    encryptedPassword VARCHAR NOT NULL,
    encryptionKey INT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### ActivityLog Table
```sql
CREATE TABLE ActivityLog (
    logId SERIAL PRIMARY KEY,
    userName VARCHAR REFERENCES Users(userName) ON DELETE CASCADE,
    activity TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Triggers and Functions

### log_activity Function
```sql
CREATE FUNCTION log_activity() RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO ActivityLog(userName, activity, timestamp)
    VALUES (NEW.userName, 'Password entry added/updated/deleted', NOW());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

### password_entry_trigger Trigger
```sql
CREATE TRIGGER password_entry_trigger
AFTER INSERT OR UPDATE OR DELETE ON PasswordEntry
FOR EACH ROW EXECUTE FUNCTION log_activity();
```

### Summary:

- **Users Table**: Defines the structure for storing user information, including `userName`, `email`, and encrypted password data.
- **PasswordEntry Table**: Stores the password details for each user, linking back to the `Users` table via a foreign key.
- **ActivityLog Table**: Records user activities, such as adding, updating, or deleting passwords, with timestamps.
- **Triggers and Functions**: Automatically logs activities related to password entries by using PostgreSQL triggers and functions.

## Encryption and Decryption Process

### Encryption
1. **Input Password:** 
   - The user provides a password during sign-up or when adding a new password.
2. **Reverse String:** 
   - The password string is reversed to begin the encryption process.
3. **Generate Key:** 
   - A random encryption key (a single-digit integer between 1 and 9) is generated.
4. **Shift Characters:** 
   - Each character in the reversed password string is shifted forward in the ASCII table by the value of the encryption key.
5. **Store Encrypted Password:** 
   - The encrypted password, along with the encryption key, is securely stored in the database.

### Decryption
1. **Retrieve Data:** 
   - The encrypted password and corresponding encryption key are retrieved from the database.
2. **Reverse Shift:** 
   - Each character in the encrypted password string is shifted backward in the ASCII table by the value of the encryption key.
3. **Reverse String:** 
   - The resulting string is reversed to restore the original password.
4. **Return Decrypted Password:** 
   - The original password is now fully decrypted and ready for use.

## Contributing

Contributions are welcome! Here’s how you can help:

1. **Fork the Repository:**
   - Fork this repository to your GitHub account.

2. **Create a Branch:**
   - Create a new branch for your feature or bug fix.
   ```bash
   git checkout -b feature/AnkushGitRepo
   ```


### Summary:

- **Encryption and Decryption Process**: This section explains the steps involved in securely encrypting and decrypting user passwords, ensuring data protection.
- **Contributing**: Provides guidelines for contributing to the project, including forking the repository, creating branches, and submitting pull requests.
- **Summary**: Offers a concise overview of the project, highlighting its key features, data structures, and potential future developments.

This content rounds out the `README.md` file, giving users and contributors all the information they need to understand, use, and contribute to the Secure Password Manager project.


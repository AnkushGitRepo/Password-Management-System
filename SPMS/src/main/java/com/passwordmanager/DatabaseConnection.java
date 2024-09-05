package com.passwordmanager;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/SPMS";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1806";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(Color.RED + Color.BOLD + "ERROR OCCUR DURING LOADING OF POSTGRES DRIVER : " + e.getMessage() + Color.RESET);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveLog(String userName, String activity) {
        String sql = "INSERT INTO ActivityLog (userName, activity) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);  // Ensure userName is correctly referenced
            stmt.setString(2, activity);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(Color.RED + Color.BOLD + e.getMessage() + Color.RESET);
        }
    }


    public static boolean saveUser(User user) {
        String sql = "INSERT INTO Users (userName, email, phoneNumber, dateOfBirth, encryptedPassword, encryptionKey) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setDate(4, Date.valueOf(user.getDateOfBirth()));
            stmt.setString(5, user.getEncryptedPassword());
            stmt.setInt(6, user.getEncryptionKey());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(Color.RED + Color.BOLD + "     Registration Failed Due To Duplicate Data" + Color.RESET);
            return false;
        }
    }


    public static User getUser(String identifier) {
        String sql = "SELECT * FROM Users WHERE email = ? OR userName = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identifier.toLowerCase());
            stmt.setString(2, identifier);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),
                        rs.getDate("dateOfBirth").toString(),
                        rs.getString("encryptedPassword"),
                        rs.getInt("encryptionKey")
                );
            }

        } catch (SQLException e) {
            System.out.println(Color.RED + Color.BOLD + e.getMessage() + Color.RESET);
        }
        return null;
    }

    public static int savePassword(String userName, PasswordEntry entry) {
        String sql = "INSERT INTO PasswordEntry (userName, siteURL, siteName, siteUserName, encryptedPassword, encryptionKey) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, userName);
            stmt.setString(2, entry.getSiteURL());
            stmt.setString(3, entry.getSiteName());
            stmt.setString(4, entry.getSiteUserName());
            stmt.setString(5, entry.getEncryptedPassword());
            stmt.setInt(6, entry.getEncryptionKey());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the generated ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Return the generated ID
                }
            }
        } catch (SQLException e) {
            System.out.println(Color.RED+ Color.BOLD+ "     ERROR: Failed To Save New Password");
        }
        return -1; // Return -1 if the operation failed
    }


    public static LinkedListDSA<PasswordEntry> getPasswords(String userName) {
        LinkedListDSA<PasswordEntry> passwordList = new LinkedListDSA<>();
        String sql = "SELECT id, siteURL, siteName, siteUserName, encryptedPassword, encryptionKey FROM PasswordEntry WHERE userName = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PasswordEntry entry = new PasswordEntry(
                        rs.getInt("id"),  // Get the ID from the result set
                        rs.getString("siteURL"),
                        rs.getString("siteName"),
                        rs.getString("siteUserName"),
                        rs.getString("encryptedPassword"),
                        rs.getInt("encryptionKey")
                );
                passwordList.add(entry);
            }

        } catch (SQLException e) {
            System.out.println(Color.RED+ "     ERROR DURING FETCHING DATA FROM PasswordEntry TABLE"+Color.RESET);
        }
        return passwordList;
    }


    public static void updatePassword(PasswordEntry entry) {
        String sql = "UPDATE PasswordEntry SET encryptedPassword = ?, encryptionKey = ? WHERE siteURL = ? AND siteName = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entry.getEncryptedPassword());
            stmt.setInt(2, entry.getEncryptionKey());
            stmt.setString(3, entry.getSiteURL());
            stmt.setString(4, entry.getSiteName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(Color.RED + Color.BOLD + e.getMessage() + Color.RESET);
        }
    }



    public static boolean deletePassword(int entryId) {
        String sql = "DELETE FROM PasswordEntry WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entryId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0; // Return true if deletion was successful
        } catch (SQLException e) {
            System.out.println(Color.RED+ Color.BOLD+ "     ERROR: During Delete Password From PasswordEntry Table"+Color.RESET);
            return false; // Return false if deletion failed
        }
    }


    public static StackDSA<String> getLogs(String userName) {
        StackDSA<String> logs = new StackDSA<>(100); // Assuming a max size of 100 logs
        String sql = "SELECT logId, userName, activity, timestamp FROM ActivityLog WHERE userName = ? ORDER BY timestamp DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int logId = rs.getInt("logId");
                String user = rs.getString("userName");
                String activity = rs.getString("activity");
                Timestamp timestamp = rs.getTimestamp("timestamp");

                String logEntry = String.format("Log ID: %d | User: %s | Activity: %s | Timestamp: %s",
                        logId, user, activity, timestamp.toString());

                logs.push(logEntry); // Push the log entry onto the stack
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return logs;
    }

    public static void saveLog(String activity) {
        String sql = "INSERT INTO ActivityLog (userName, activity) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, activity);
            stmt.setString(2, activity);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean userExists(String userName) {
        String sql = "SELECT COUNT(*) FROM Users WHERE userName = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(Color.RED + Color.BOLD + e.getMessage() + Color.RESET);
        }
        return false;
    }

    public static boolean userExistsWithEmail(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(Color.RED + Color.BOLD + e.getMessage() + Color.RESET);
        }
        return false;
    }

    public static boolean passwordExists(String userName, String siteURL, String siteName, String siteUserName) {
        String sql = "SELECT COUNT(*) FROM PasswordEntry WHERE userName = ? AND siteURL = ? AND siteName = ? AND siteUserName = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);
            stmt.setString(2, siteURL);
            stmt.setString(3, siteName);
            stmt.setString(4, siteUserName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if a matching entry exists
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false; // Return false if no matching entry exists
    }

}

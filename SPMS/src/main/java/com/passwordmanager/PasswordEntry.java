package com.passwordmanager;

public class PasswordEntry {

    private int id; // Add this field
    private String siteURL;
    private String siteName;
    private String siteUserName;
    private String encryptedPassword;
    private int encryptionKey;

    public PasswordEntry(int id, String siteURL, String siteName, String siteUserName, String encryptedPassword, int encryptionKey) {
        this.id = id; // Initialize the id
        this.siteURL = siteURL;
        this.siteName = siteName;
        this.siteUserName = siteUserName;
        this.encryptedPassword = encryptedPassword;
        this.encryptionKey = encryptionKey;
    }

    public PasswordEntry(String siteURL, String siteName, String siteUserName, String encryptedPassword, int encryptionKey) {
        this.siteURL = siteURL;
        this.siteName = siteName;
        this.siteUserName = siteUserName;
        this.encryptedPassword = encryptedPassword;
        this.encryptionKey = encryptionKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSiteURL() {
        return siteURL;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getSiteUserName() {
        return siteUserName;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public int getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public void setEncryptionKey(int encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
}

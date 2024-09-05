package com.passwordmanager;

public class User {

    private String userName;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String encryptedPassword;
    private int encryptionKey;

    public User(String userName, String email, String phoneNumber, String dateOfBirth, String encryptedPassword, int encryptionKey) {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.encryptedPassword = encryptedPassword;
        this.encryptionKey = encryptionKey;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public int getEncryptionKey() {
        return encryptionKey;
    }

}

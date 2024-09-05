package com.passwordmanager;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Random;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class UserManager {

    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void signUp() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print(Color.YELLOW + "     Enter your email: " + Color.RESET);
            String email = scanner.nextLine().toLowerCase();

            if (validateEmail(email)) {
                if (DatabaseConnection.userExistsWithEmail(email)) {
                    System.out.println(Color.RED + Color.BOLD + "     A user with this email already exists." + Color.RESET);
                } else {
                    String name;
                    while (true) {
                        System.out.print(Color.YELLOW + "     Enter your name: " + Color.RESET);
                        name = scanner.nextLine();
                        if (validateName(name)) {
                            break;
                        } else {
                            System.out.println(Color.RED + Color.BOLD + "     Invalid name. Please enter a name without numbers or special characters." + Color.RESET);
                        }
                    }

                    // Phone Number
                    String phoneNumber;
                    while (true) {
                        System.out.print(Color.YELLOW + "     Enter your phone number: " + Color.RESET);
                        phoneNumber = scanner.nextLine();
                        if (validatePhoneNumber(phoneNumber)) {
                            break;
                        } else {
                            System.out.println(Color.RED + Color.BOLD + "     Invalid phone number. Please enter exactly 10 digits." + Color.RESET);
                        }
                    }

                    // Date of Birth
                    String dob;
                    while (true) {
                        System.out.print(Color.YELLOW + "     Enter your date of birth (YYYY-MM-DD): " + Color.RESET);
                        dob = scanner.nextLine();
                        if (validateDOB(dob)) {
                            break;
                        } else {
                            System.out.println(Color.RED + Color.BOLD + "     Invalid date of birth. Please enter a valid date in the past." + Color.RESET);
                        }
                    }

                    // Password validation with confirmation
                    String password = null;
                    for (int attempts = 0; attempts < 3; attempts++) {
                        System.out.print(Color.YELLOW + "     Set your password: " + Color.RESET);
                        password = scanner.nextLine();
                        if (validatePassword(password)) {
                            System.out.print(Color.YELLOW + "     Confirm your password: " + Color.RESET);
                            String confirmPassword = scanner.nextLine();
                            if (password.equals(confirmPassword)) {
                                break; // Password is valid and confirmed
                            } else {
                                System.out.println(Color.RED + Color.BOLD + "     Passwords do not match. Try again." + Color.RESET);
                                attempts--; // Do not count this attempt as a failure, just retry confirmation
                            }
                        } else {
                            System.out.println(Color.RED + Color.BOLD + "     Invalid password. Please try again." + Color.RESET);
                        }

                        if (attempts == 2) {
                            System.out.println(Color.RED + Color.BOLD + "     Maximum attempts reached. Registration failed." + Color.RESET);
                            return; // Exit sign-up due to repeated invalid password attempts
                        }
                    }

                    if (validatePassword(password)) {
                        String userName = generateUserName(name, dob, phoneNumber);
                        int encryptionKey = generateEncryptionKey();
                        String encryptedPassword = encryptPassword(password, encryptionKey);

                        currentUser = new User(userName, email, phoneNumber, dob, encryptedPassword, encryptionKey);
                        boolean isRegistrationSuccessful = DatabaseConnection.saveUser(currentUser);
                        if (isRegistrationSuccessful) {
                            System.out.println(Color.GREEN + Color.BOLD + "     User registered successfully!" + Color.RESET);
                            System.out.println(Color.PURPLE + Color.BOLD + "     Your UserName is : " + Color.RESET + Color.RED + userName + Color.RESET + Color.PURPLE + Color.BOLD + " [Please Note It Down!]" + Color.RESET);
                        }
                    } else {
                        System.out.println(Color.RED + Color.BOLD + "     Password is invalid. Please try again." + Color.RESET);
                    }
                }
            } else {
                System.out.println(Color.RED + Color.BOLD + "     Invalid email." + Color.RESET);
            }
        }catch (Exception e){
            System.out.println(Color.RED + Color.BOLD + "     Registration failed due to an unexpected error." + Color.RESET);
        }
        System.out.println();
    }

    public boolean login() {
        Scanner scanner = new Scanner(System.in);

        System.out.print(Color.YELLOW + "     Enter your email or username: " + Color.RESET);
        String identifier = scanner.nextLine();
        System.out.print(Color.YELLOW + "     Enter your password: " + Color.RESET);
        String password = scanner.nextLine();

        currentUser = DatabaseConnection.getUser(identifier);

        if (currentUser != null) {
            String decryptedPassword = decryptPassword(currentUser.getEncryptedPassword(), currentUser.getEncryptionKey());
            if (password.equals(decryptedPassword)) {
                System.out.println(Color.GREEN + Color.BOLD + "     Login successful!" + Color.RESET);
                System.out.println();
                return true;
            }
        }

        System.out.println(Color.RED + Color.BOLD + "     Invalid credentials." + Color.RESET);
        System.out.println();
        return false;
    }


    // Utility methods

    private boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char ch : password.toCharArray()) {
            if (Character.isLetter(ch)) {
                hasLetter = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(ch)) {
                hasSpecialChar = true;
            }
        }

        return hasLetter && hasDigit && hasSpecialChar;
    }

    private String generateUserName(String name, String dob, String phoneNumber) {
        String firstName = name.split(" ")[0];
        LocalDate birthDate = LocalDate.parse(dob);
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        String phonePart = phoneNumber.substring(0, 5);

        return firstName + dob.replace("-", "") + age + phonePart;
    }

    private int generateEncryptionKey() {
        return new Random().nextInt(9) + 1; // Generates a number between 1 and 9
    }

    private String encryptPassword(String password, int key) {
        StringBuilder reversedPassword = new StringBuilder(password).reverse();
        StringBuilder encryptedPassword = new StringBuilder();

        for (char ch : reversedPassword.toString().toCharArray()) {
            encryptedPassword.append((char) (ch + key));
        }

        return encryptedPassword.toString();
    }

    private String decryptPassword(String encryptedPassword, int key) {
        StringBuilder decryptedPassword = new StringBuilder();

        for (char ch : encryptedPassword.toCharArray()) {
            decryptedPassword.append((char) (ch - key));
        }

        return decryptedPassword.reverse().toString();
    }

    public static boolean validateName(String name) {
        if (name == null || name.isEmpty()) {
            return false; // Name cannot be null or empty
        }

        String nameRegex = "^[a-zA-Z\\s]+$"; // Regex for letters and spaces only
        return name.matches(nameRegex); // Returns true if name matches the regex, false otherwise
    }

    // Phone Number Validation
    public static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false; // Phone number cannot be null or empty
        }

        String phoneRegex = "^[0-9]{10}$"; // Regex for exactly 10 digits
        return phoneNumber.matches(phoneRegex); // Returns true if phone number matches the regex, false otherwise
    }

    // Date of Birth Validation
    public static boolean validateDOB(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate dateOfBirth = LocalDate.parse(dob, formatter);
            return dateOfBirth.isBefore(LocalDate.now()); // Check if the date is in the past
        } catch (DateTimeParseException e) {
            return false; // Invalid date format
        }
    }
}

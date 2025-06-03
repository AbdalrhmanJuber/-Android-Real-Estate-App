package com.example.a1211769_courseproject;

import java.util.regex.Pattern;

public class ValidationUtils {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    );
    
    // Password validation: at least 6 characters, one letter, one number, one special character
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}$"
    );
    
    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates password strength
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Validates name (minimum 3 characters)
     */
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 3;
    }
    
    /**
     * Validates phone number (basic validation)
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.trim().length() >= 10;
    }
    
    /**
     * Gets password strength description
     */
    public static String getPasswordStrengthMessage() {
        return "Password must be at least 6 characters with at least one letter, one number, and one special character";
    }
    
    /**
     * Gets name validation message
     */
    public static String getNameValidationMessage() {
        return "Name must be at least 3 characters long";
    }
}

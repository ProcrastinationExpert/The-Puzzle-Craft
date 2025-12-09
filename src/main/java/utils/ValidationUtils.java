/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author User
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    // Regex (Regular Expression) untuk email
    private static final String EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
    
    // untuk melakukan validasi email
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
    
    // untuk melakukan validasi password
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < AppConstants.MIN_PASSWORD_LENGTH) {
            // Contoh aturan: minimal 8 karakter
            return false;
        }
        // bisa ditambahin regex di sini
        return true;
    }
    
    public static boolean isPasswordConfirmationValid(String password, String repeatPassword) {
        if (!isValidPassword(password) || !isValidPassword(repeatPassword)) { // cek validitas dulu kedua passwordnya
            return false;
        }
        if (!password.equals(repeatPassword)) { // cek apakah kedua password tidak sama
            return false;
        }
        return true;
    }
    
    public static boolean isValidUsername(String username) {
        // cek minimal username harus ada 5 karakter
        if (username == null || username.length() < AppConstants.MIN_USERNAME_LENGTH) { 
            return false;
        }
        return true;
    }
    
    public static boolean isEmpty(String text) {
        // cek apakah teksnya kosong atau tidak
        return text == null || text.trim().isEmpty() || text.isBlank();
    }
    
}

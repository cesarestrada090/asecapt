package com.asecapt.app.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes
 * This is used for creating password hashes for admin users
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hash for "asecapt0226"
        String rawPassword = "Asecapt0226";
        String hashedPassword = encoder.encode(rawPassword);
        
        System.out.println("Raw password: " + rawPassword);
        System.out.println("BCrypt hash: " + hashedPassword);
        
        // Verify the hash works
        boolean matches = encoder.matches(rawPassword, hashedPassword);
        System.out.println("Verification: " + matches);
        
        // Also generate a few more for comparison
        System.out.println("\nAdditional hashes for the same password:");
        for (int i = 0; i < 3; i++) {
            System.out.println("Hash " + (i + 1) + ": " + encoder.encode(rawPassword));
        }
    }
}

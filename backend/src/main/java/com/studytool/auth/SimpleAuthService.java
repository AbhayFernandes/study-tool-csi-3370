package com.studytool.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple stub implementation of AuthService for development/testing.
 * This implementation uses hardcoded credentials and in-memory token storage.
 * Replace with a real implementation that uses database and proper security.
 */
public class SimpleAuthService implements AuthInterface {
    
    // Hardcoded credentials for testing (username -> password)
    private static final Map<String, String> USERS = new HashMap<>();
    
    // In-memory token storage (token -> username)
    private final Map<String, String> tokens = new HashMap<>();
    
    static {
        // Add some test users
        USERS.put("testuser", "password123");
        USERS.put("admin", "admin123");
    }
    
    @Override
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        String expectedPassword = USERS.get(username);
        return expectedPassword != null && expectedPassword.equals(password);
    }
    
    @Override
    public String generateToken(String username) {
        if (username == null) {
            return null;
        }
        
        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return token;
    }
    
    @Override
    public boolean validateToken(String token) {
        return token != null && tokens.containsKey(token);
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        if (token == null) {
            return null;
        }
        return tokens.get(token);
    }
} 
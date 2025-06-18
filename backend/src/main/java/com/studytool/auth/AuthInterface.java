package com.studytool.auth;

/**
 * Interface for authentication services.
 * This allows for different authentication implementations to be plugged in.
 */
public interface AuthInterface {
    
    /**
     * Authenticate a user with username and password.
     * 
     * @param username The username to authenticate
     * @param password The password to authenticate
     * @return true if authentication is successful, false otherwise
     */
    boolean authenticate(String username, String password);
    
    /**
     * Generate a token for an authenticated user.
     * 
     * @param username The username to generate a token for
     * @return A token string, or null if token generation fails
     */
    String generateToken(String username);
    
    /**
     * Validate a token.
     * 
     * @param token The token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
    
    /**
     * Get the username from a valid token.
     * 
     * @param token The token to extract username from
     * @return The username, or null if token is invalid
     */
    String getUsernameFromToken(String token);
} 
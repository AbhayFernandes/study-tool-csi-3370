package com.studytool.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.studytool.database.ScyllaManager;
import com.studytool.database.User;
import com.studytool.database.UserRepository;

/**
 * ScyllaDB-based implementation of AuthService.
 * This implementation uses ScyllaDB for user storage and authentication.
 */
public class ScyllaAuthService implements AuthInterface {
    private static final Logger logger = LoggerFactory.getLogger(ScyllaAuthService.class);
    
    private final UserRepository userRepository;
    private final Map<String, String> activeTokens = new ConcurrentHashMap<>();
    
    /**
     * Creates a new ScyllaAuthService instance.
     * 
     * @param scyllaManager The ScyllaManager instance
     */
    public ScyllaAuthService(ScyllaManager scyllaManager) {
        this.userRepository = new UserRepository(scyllaManager.getSession());
        this.userRepository.createUser(new User("admin", hashPassword("admin123")));
        this.userRepository.createUser(new User("testuser", hashPassword("password123")));

        logger.info("ScyllaAuthService initialized with ScyllaDB backend");
    }
    
    @Override
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            logger.debug("Authentication failed: username or password is null");
            return false;
        }
        
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                logger.debug("Authentication failed: user not found: {}", username);
                return false;
            }
            
            User user = userOptional.get();
            String hashedPassword = hashPassword(password);
            
            boolean isAuthenticated = hashedPassword.equals(user.getPasswordHash());
            if (isAuthenticated) {
                logger.info("User authenticated successfully: {}", username);
            } else {
                logger.debug("Authentication failed: incorrect password for user: {}", username);
            }
            
            return isAuthenticated;
        } catch (Exception e) {
            logger.error("Authentication error for user: {}", username, e);
            return false;
        }
    }
    
    @Override
    public String generateToken(String username) {
        if (username == null) {
            return null;
        }
        
        try {
            // Verify user exists
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                logger.debug("Token generation failed: user not found: {}", username);
                return null;
            }
            
            String token = UUID.randomUUID().toString();
            activeTokens.put(token, username);
            
            logger.debug("Generated token for user: {}", username);
            return token;
        } catch (Exception e) {
            logger.error("Token generation error for user: {}", username, e);
            return null;
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        if (token == null) {
            return false;
        }
        
        boolean isValid = activeTokens.containsKey(token);
        logger.debug("Token validation result: {} for token: {}", isValid, token);
        return isValid;
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        if (token == null) {
            return null;
        }
        
        String username = activeTokens.get(token);
        logger.debug("Retrieved username from token: {}", username != null ? username : "null");
        return username;
    }
    
    /**
     * Creates a new user account.
     * 
     * @param username The username
     * @param password The plain text password
     * @return true if the user was created successfully
     */
    public boolean createUser(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            logger.debug("User creation failed: invalid username or password");
            return false;
        }
        
        try {
            // Check if user already exists
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent()) {
                logger.debug("User creation failed: user already exists: {}", username);
                return false;
            }
            
            String hashedPassword = hashPassword(password);
            User newUser = new User(username, hashedPassword);
            
            boolean created = userRepository.createUser(newUser);
            if (created) {
                logger.info("User created successfully: {}", username);
            } else {
                logger.error("Failed to create user: {}", username);
            }
            
            return created;
        } catch (Exception e) {
            logger.error("User creation error for username: {}", username, e);
            return false;
        }
    }
    
    /**
     * Updates a user's password.
     * 
     * @param username The username
     * @param newPassword The new plain text password
     * @return true if the password was updated successfully
     */
    public boolean updatePassword(String username, String newPassword) {
        if (username == null || newPassword == null || newPassword.trim().isEmpty()) {
            logger.debug("Password update failed: invalid username or password");
            return false;
        }
        
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                logger.debug("Password update failed: user not found: {}", username);
                return false;
            }
            
            User user = userOptional.get();
            String hashedPassword = hashPassword(newPassword);
            
            boolean updated = userRepository.updatePassword(user.getId(), hashedPassword);
            if (updated) {
                logger.info("Password updated successfully for user: {}", username);
            } else {
                logger.error("Failed to update password for user: {}", username);
            }
            
            return updated;
        } catch (Exception e) {
            logger.error("Password update error for user: {}", username, e);
            return false;
        }
    }
    
    /**
     * Logs out a user by invalidating their token.
     * 
     * @param token The token to invalidate
     * @return true if the token was invalidated
     */
    public boolean logout(String token) {
        if (token == null) {
            return false;
        }
        
        String username = activeTokens.remove(token);
        boolean loggedOut = username != null;
        
        if (loggedOut) {
            logger.info("User logged out: {}", username);
        } else {
            logger.debug("Logout failed: invalid token");
        }
        
        return loggedOut;
    }
    
    /**
     * Hashes a password using SHA-256.
     * Note: In production, consider using bcrypt or similar for better security.
     * 
     * @param password The plain text password
     * @return The hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to hash password", e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }
} 
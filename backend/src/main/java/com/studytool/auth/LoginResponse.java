package com.studytool.auth;

/**
 * Data class for login response payload
 */
public class LoginResponse {
    private boolean success;
    private String token;
    private String message;
    private String username;
    
    // Default constructor for Jackson
    public LoginResponse() {}
    
    public LoginResponse(boolean success, String token, String message, String username) {
        this.success = success;
        this.token = token;
        this.message = message;
        this.username = username;
    }
    
    // Static factory methods for convenience
    public static LoginResponse success(String token, String username) {
        return new LoginResponse(true, token, "Login successful", username);
    }
    
    public static LoginResponse failure(String message) {
        return new LoginResponse(false, null, message, null);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
} 
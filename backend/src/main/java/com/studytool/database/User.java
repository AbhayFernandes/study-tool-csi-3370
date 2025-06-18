package com.studytool.database;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user in the ScyllaDB database.
 * This class corresponds to the 'users' table schema.
 */
public class User {
    private UUID id;
    private String username;
    private String passwordHash;
    private Instant createdAt;
    private Instant updatedAt;
    
    /**
     * Default constructor for User.
     */
    public User() {
    }
    
    /**
     * Creates a new User with the specified parameters.
     * 
     * @param id The unique identifier
     * @param username The username
     * @param passwordHash The hashed password
     * @param createdAt The creation timestamp
     * @param updatedAt The last update timestamp
     */
    public User(UUID id, String username, String passwordHash, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * Creates a new User with generated ID and timestamps.
     * 
     * @param username The username
     * @param passwordHash The hashed password
     */
    public User(String username, String passwordHash) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    // Getters and Setters
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(username, user.username) &&
               Objects.equals(passwordHash, user.passwordHash) &&
               Objects.equals(createdAt, user.createdAt) &&
               Objects.equals(updatedAt, user.updatedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username, passwordHash, createdAt, updatedAt);
    }
    
    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
} 
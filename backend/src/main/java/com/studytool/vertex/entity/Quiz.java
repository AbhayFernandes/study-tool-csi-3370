package com.studytool.vertex.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a quiz in the ScyllaDB database.
 */
public class Quiz {
    private UUID id;
    private UUID userId;
    private UUID fileId;
    private String content;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;
    
    public Quiz() {
    }
    
    public Quiz(UUID id, UUID userId, UUID fileId, String content, String title, 
               Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.fileId = fileId;
        this.content = content;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public Quiz(UUID userId, UUID fileId, String content, String title) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.fileId = fileId;
        this.content = content;
        this.title = title;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public UUID getFileId() {
        return fileId;
    }
    
    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
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
        Quiz quiz = (Quiz) o;
        return Objects.equals(id, quiz.id) &&
               Objects.equals(userId, quiz.userId) &&
               Objects.equals(fileId, quiz.fileId) &&
               Objects.equals(content, quiz.content) &&
               Objects.equals(title, quiz.title) &&
               Objects.equals(createdAt, quiz.createdAt) &&
               Objects.equals(updatedAt, quiz.updatedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, fileId, content, title, createdAt, updatedAt);
    }
    
    @Override
    public String toString() {
        return "Quiz{" +
               "id=" + id +
               ", userId=" + userId +
               ", fileId=" + fileId +
               ", title='" + title + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
} 
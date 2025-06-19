package com.studytool.vertex.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a flashcard in the ScyllaDB database.
 */
public class Flashcard {
    private UUID id;
    private UUID userId;
    private UUID fileId;
    private String content;
    private String front;
    private String back;
    private Instant createdAt;
    private Instant updatedAt;
    
    public Flashcard() {
    }
    
    public Flashcard(UUID id, UUID userId, UUID fileId, String content, String front, String back, 
                    Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.fileId = fileId;
        this.content = content;
        this.front = front;
        this.back = back;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public Flashcard(UUID userId, UUID fileId, String content, String front, String back) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.fileId = fileId;
        this.content = content;
        this.front = front;
        this.back = back;
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
    
    public String getFront() {
        return front;
    }
    
    public void setFront(String front) {
        this.front = front;
    }
    
    public String getBack() {
        return back;
    }
    
    public void setBack(String back) {
        this.back = back;
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
        Flashcard flashcard = (Flashcard) o;
        return Objects.equals(id, flashcard.id) &&
               Objects.equals(userId, flashcard.userId) &&
               Objects.equals(fileId, flashcard.fileId) &&
               Objects.equals(content, flashcard.content) &&
               Objects.equals(front, flashcard.front) &&
               Objects.equals(back, flashcard.back) &&
               Objects.equals(createdAt, flashcard.createdAt) &&
               Objects.equals(updatedAt, flashcard.updatedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, fileId, content, front, back, createdAt, updatedAt);
    }
    
    @Override
    public String toString() {
        return "Flashcard{" +
               "id=" + id +
               ", userId=" + userId +
               ", fileId=" + fileId +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
} 
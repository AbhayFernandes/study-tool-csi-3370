package com.studytool.vertex.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a summary in the ScyllaDB database.
 */
public class Summary {
    private UUID id;
    private UUID userId;
    private UUID fileId;
    private String content;
    private String summary;
    private Instant createdAt;
    private Instant updatedAt;
    
    public Summary() {
    }
    
    public Summary(UUID id, UUID userId, UUID fileId, String content, String summary, 
                  Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.fileId = fileId;
        this.content = content;
        this.summary = summary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public Summary(UUID userId, UUID fileId, String content, String summary) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.fileId = fileId;
        this.content = content;
        this.summary = summary;
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
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
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
        Summary summary1 = (Summary) o;
        return Objects.equals(id, summary1.id) &&
               Objects.equals(userId, summary1.userId) &&
               Objects.equals(fileId, summary1.fileId) &&
               Objects.equals(content, summary1.content) &&
               Objects.equals(summary, summary1.summary) &&
               Objects.equals(createdAt, summary1.createdAt) &&
               Objects.equals(updatedAt, summary1.updatedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, fileId, content, summary, createdAt, updatedAt);
    }
    
    @Override
    public String toString() {
        return "Summary{" +
               "id=" + id +
               ", userId=" + userId +
               ", fileId=" + fileId +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
} 
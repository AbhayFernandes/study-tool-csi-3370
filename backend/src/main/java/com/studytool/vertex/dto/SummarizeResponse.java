package com.studytool.vertex.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for text summarization.
 */
public class SummarizeResponse {
    private UUID id;
    private String summary;
    private Instant createdAt;
    
    public SummarizeResponse() {
    }
    
    public SummarizeResponse(UUID id, String summary, Instant createdAt) {
        this.id = id;
        this.summary = summary;
        this.createdAt = createdAt;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
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
} 
package com.studytool.vertex.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for individual flashcards.
 */
public class FlashcardDto {
    private UUID id;
    private UUID setId;
    private String front;
    private String back;
    private Instant createdAt;
    
    public FlashcardDto() {
    }
    
    public FlashcardDto(UUID id, UUID setId, String front, String back, Instant createdAt) {
        this.id = id;
        this.setId = setId;
        this.front = front;
        this.back = back;
        this.createdAt = createdAt;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getSetId() {
        return setId;
    }
    
    public void setSetId(UUID setId) {
        this.setId = setId;
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
} 
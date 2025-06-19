package com.studytool.vertex.dto;

import java.util.UUID;

/**
 * Request DTO for flashcard generation.
 */
public class FlashcardRequest {
    private String content;
    private UUID fileId;
    private UUID userId;
    private int count = 5; // Default number of flashcards
    
    public FlashcardRequest() {
    }
    
    public FlashcardRequest(String content, UUID fileId, UUID userId, int count) {
        this.content = content;
        this.fileId = fileId;
        this.userId = userId;
        this.count = count;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public UUID getFileId() {
        return fileId;
    }
    
    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
} 
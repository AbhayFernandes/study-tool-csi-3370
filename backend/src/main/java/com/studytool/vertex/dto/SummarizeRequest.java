package com.studytool.vertex.dto;

import java.util.UUID;

/**
 * Request DTO for text summarization.
 */
public class SummarizeRequest {
    private String content;
    private UUID fileId;
    private UUID userId;
    
    public SummarizeRequest() {
    }
    
    public SummarizeRequest(String content, UUID fileId, UUID userId) {
        this.content = content;
        this.fileId = fileId;
        this.userId = userId;
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
} 
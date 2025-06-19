package com.studytool.vertex.dto;

import java.util.UUID;

/**
 * Request DTO for quiz generation.
 */
public class QuizRequest {
    private String content;
    private UUID fileId;
    private UUID userId;
    private String title;
    private int questionCount = 5; // Default number of questions
    
    public QuizRequest() {
    }
    
    public QuizRequest(String content, UUID fileId, UUID userId, String title, int questionCount) {
        this.content = content;
        this.fileId = fileId;
        this.userId = userId;
        this.title = title;
        this.questionCount = questionCount;
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getQuestionCount() {
        return questionCount;
    }
    
    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }
} 
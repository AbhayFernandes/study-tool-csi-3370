package com.studytool.vertex.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for quiz responses.
 */
public class QuizDto {
    private UUID id;
    private String title;
    private List<QuizQuestionDto> questions;
    private Instant createdAt;
    
    public QuizDto() {
    }
    
    public QuizDto(UUID id, String title, List<QuizQuestionDto> questions, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.questions = questions;
        this.createdAt = createdAt;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<QuizQuestionDto> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<QuizQuestionDto> questions) {
        this.questions = questions;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
} 
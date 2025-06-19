package com.studytool.vertex.dto;

/**
 * Response DTO for concept explanation.
 */
public class ExplainResponse {
    private String explanation;
    
    public ExplainResponse() {
    }
    
    public ExplainResponse(String explanation) {
        this.explanation = explanation;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
} 
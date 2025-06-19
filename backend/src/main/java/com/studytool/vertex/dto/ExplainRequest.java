package com.studytool.vertex.dto;

/**
 * Request DTO for concept explanation.
 */
public class ExplainRequest {
    private String concept;
    private String context;
    
    public ExplainRequest() {
    }
    
    public ExplainRequest(String concept, String context) {
        this.concept = concept;
        this.context = context;
    }
    
    public String getConcept() {
        return concept;
    }
    
    public void setConcept(String concept) {
        this.concept = concept;
    }
    
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
} 
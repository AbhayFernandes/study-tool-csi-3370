package com.studytool.vertex;

/**
 * Configuration class for Google Vertex AI settings.
 */
public class VertexAiConfig {
    
    // Default configuration values
    public static final String DEFAULT_PROJECT_ID = "your-project-id";
    public static final String DEFAULT_LOCATION = "us-central1";
    public static final String DEFAULT_TEXT_MODEL = "text-bison-001";
    
    private final String projectId;
    private final String location;
    private final String textModel;
    
    /**
     * Creates a VertexAiConfig with default values.
     */
    public VertexAiConfig() {
        this(DEFAULT_PROJECT_ID, DEFAULT_LOCATION, DEFAULT_TEXT_MODEL);
    }
    
    /**
     * Creates a VertexAiConfig with specified values.
     * 
     * @param projectId The Google Cloud project ID
     * @param location The Vertex AI location
     * @param textModel The text model to use
     */
    public VertexAiConfig(String projectId, String location, String textModel) {
        this.projectId = projectId;
        this.location = location;
        this.textModel = textModel;
    }
    
    /**
     * Creates a VertexAiConfig from environment variables or uses defaults.
     * 
     * @return VertexAiConfig instance
     */
    public static VertexAiConfig fromEnvironment() {
        String projectId = System.getenv("VERTEX_PROJECT_ID");
        if (projectId == null || projectId.isEmpty()) {
            projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
        }
        if (projectId == null || projectId.isEmpty()) {
            projectId = DEFAULT_PROJECT_ID;
        }
        
        String location = System.getenv("VERTEX_LOCATION");
        if (location == null || location.isEmpty()) {
            location = DEFAULT_LOCATION;
        }
        
        String textModel = System.getenv("VERTEX_TEXT_MODEL");
        if (textModel == null || textModel.isEmpty()) {
            textModel = DEFAULT_TEXT_MODEL;
        }
        
        return new VertexAiConfig(projectId, location, textModel);
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getTextModel() {
        return textModel;
    }
    
    @Override
    public String toString() {
        return "VertexAiConfig{" +
               "projectId='" + projectId + '\'' +
               ", location='" + location + '\'' +
               ", textModel='" + textModel + '\'' +
               '}';
    }
} 
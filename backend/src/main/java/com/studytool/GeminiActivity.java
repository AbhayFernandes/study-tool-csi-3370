package com.studytool;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

public class GeminiActivity {
    private final GenerativeModel model;
    private final String modelName;

    public GeminiActivity(String modelName) {
        this.modelName = modelName;
        try (VertexAI vertexAi = new VertexAI("study-tool-444119", "us-central1"); ) {
            this.model = new GenerativeModel("gemini-1.5-flash", vertexAi);
        }
    }

}

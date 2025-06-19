package com.studytool.vertex;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.studytool.vertex.dto.ExplainRequest;
import com.studytool.vertex.dto.ExplainResponse;
import com.studytool.vertex.dto.FlashcardDto;
import com.studytool.vertex.dto.FlashcardRequest;
import com.studytool.vertex.dto.QuizDto;
import com.studytool.vertex.dto.QuizRequest;
import com.studytool.vertex.dto.SummarizeRequest;
import com.studytool.vertex.dto.SummarizeResponse;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

/**
 * REST controller for Vertex AI operations.
 */
public class VertexAiController {
    private static final Logger logger = LoggerFactory.getLogger(VertexAiController.class);
    
    private final VertexAiService vertexAiService;
    
    public VertexAiController(VertexAiService vertexAiService) {
        this.vertexAiService = vertexAiService;
    }
    
    /**
     * POST /api/ai/summarize
     * Generates a summary of the provided text content.
     */
    public void summarize(Context ctx) {
        try {
            SummarizeRequest request = ctx.bodyAsClass(SummarizeRequest.class);
            
            // Validate request
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(new ErrorResponse("Content is required"));
                return;
            }
            
            if (request.getUserId() == null) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(new ErrorResponse("User ID is required"));
                return;
            }
            
            SummarizeResponse response = vertexAiService.summarizeMaterial(request);
            ctx.status(HttpStatus.OK);
            ctx.json(response);
            
        } catch (Exception e) {
            logger.error("Error in summarize endpoint: {}", e.getMessage(), e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.json(new ErrorResponse("Failed to generate summary: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/ai/flashcards
     * Generates flashcards from the provided text content.
     */
    public void generateFlashcards(Context ctx) {
        try {
            FlashcardRequest request = ctx.bodyAsClass(FlashcardRequest.class);
            
            // Validate request
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(new ErrorResponse("Content is required"));
                return;
            }
            
            if (request.getUserId() == null) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(new ErrorResponse("User ID is required"));
                return;
            }
            
            if (request.getCount() <= 0 || request.getCount() > 20) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(new ErrorResponse("Count must be between 1 and 20"));
                return;
            }
            
            List<FlashcardDto> flashcards = vertexAiService.generateFlashcards(request);
            ctx.status(HttpStatus.OK);
            ctx.json(flashcards);
            
        } catch (Exception e) {
            logger.error("Error in generateFlashcards endpoint: {}", e.getMessage(), e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.json(new ErrorResponse("Failed to generate flashcards: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/ai/quiz
     * Creates a quiz from the provided text content.
     */
    public void createQuiz(Context ctx) {
        try {
            QuizRequest request = ctx.bodyAsClass(QuizRequest.class);
            
            // Validate request
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(new ErrorResponse("Content is required"));
                return;
            }
            
            if (request.getUserId() == null) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(new ErrorResponse("User ID is required"));
                return;
            }
            
            if (request.getQuestionCount() <= 0 || request.getQuestionCount() > 20) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(new ErrorResponse("Question count must be between 1 and 20"));
                return;
            }
            
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                request.setTitle("Generated Quiz");
            }
            
            QuizDto quiz = vertexAiService.createQuiz(request);
            ctx.status(HttpStatus.OK);
            ctx.json(quiz);
            
        } catch (Exception e) {
            logger.error("Error in createQuiz endpoint: {}", e.getMessage(), e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.json(new ErrorResponse("Failed to create quiz: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/ai/explain
     * Explains a concept within the given context.
     */
    public void explainConcept(Context ctx) {
        try {
            ExplainRequest request = ctx.bodyAsClass(ExplainRequest.class);
            
            // Validate request
            if (request.getConcept() == null || request.getConcept().trim().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(new ErrorResponse("Concept is required"));
                return;
            }
            
            ExplainResponse response = vertexAiService.explainConcept(request);
            ctx.status(HttpStatus.OK);
            ctx.json(response);
            
        } catch (Exception e) {
            logger.error("Error in explainConcept endpoint: {}", e.getMessage(), e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.json(new ErrorResponse("Failed to explain concept: " + e.getMessage()));
        }
    }
    
    /**
     * Error response DTO for API errors.
     */
    public static class ErrorResponse {
        private final String error;
        private final long timestamp;
        
        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getError() {
            return error;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
} 
package com.studytool.vertex;

import java.util.List;

import com.studytool.vertex.dto.ExplainRequest;
import com.studytool.vertex.dto.ExplainResponse;
import com.studytool.vertex.dto.FlashcardDto;
import com.studytool.vertex.dto.FlashcardRequest;
import com.studytool.vertex.dto.QuizDto;
import com.studytool.vertex.dto.QuizRequest;
import com.studytool.vertex.dto.SummarizeRequest;
import com.studytool.vertex.dto.SummarizeResponse;

/**
 * Service interface for Vertex AI operations.
 */
public interface VertexAiService {
    
    /**
     * Generates a summary of the provided text content.
     * 
     * @param request The summarization request
     * @return The generated summary response
     */
    SummarizeResponse summarizeMaterial(SummarizeRequest request);
    
    /**
     * Generates flashcards from the provided text content.
     * 
     * @param request The flashcard generation request
     * @return List of generated flashcards
     */
    List<FlashcardDto> generateFlashcards(FlashcardRequest request);
    
    /**
     * Creates a quiz from the provided text content.
     * 
     * @param request The quiz generation request
     * @return The generated quiz
     */
    QuizDto createQuiz(QuizRequest request);
    
    /**
     * Explains a concept within the given context.
     * 
     * @param request The explanation request
     * @return The explanation response
     */
    ExplainResponse explainConcept(ExplainRequest request);
} 
package com.studytool.vertex;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.studytool.vertex.dto.*;
import com.studytool.vertex.entity.*;
import com.studytool.vertex.repository.SummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of VertexAiService using Google Vertex AI.
 */
public class VertexAiServiceImpl implements VertexAiService {
    private static final Logger logger = LoggerFactory.getLogger(VertexAiServiceImpl.class);
    
    private final VertexAI vertexAI;
    private final GenerativeModel model;
    private final SummaryRepository summaryRepository;
    private final ObjectMapper objectMapper;
    
    // Prompt templates
    private final String summarizePrompt;
    private final String flashcardsPrompt;
    private final String quizPrompt;
    private final String explainPrompt;
    
    public VertexAiServiceImpl(VertexAiConfig config, SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
        this.objectMapper = new ObjectMapper();
        
        try {
            this.vertexAI = new VertexAI(config.getProjectId(), config.getLocation());
            this.model = new GenerativeModel(config.getTextModel(), vertexAI);
            
            // Load prompt templates
            this.summarizePrompt = loadPromptTemplate("prompts/summarize.txt");
            this.flashcardsPrompt = loadPromptTemplate("prompts/flashcards.txt");
            this.quizPrompt = loadPromptTemplate("prompts/quiz.txt");
            this.explainPrompt = loadPromptTemplate("prompts/explain.txt");
            
            logger.info("VertexAiServiceImpl initialized with project: {}, location: {}, model: {}", 
                       config.getProjectId(), config.getLocation(), config.getTextModel());
        } catch (Exception e) {
            logger.error("Failed to initialize VertexAI service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize VertexAI service", e);
        }
    }
    
    @Override
    public SummarizeResponse summarizeMaterial(SummarizeRequest request) {
        try {
            logger.info("Generating summary for user: {}, file: {}", request.getUserId(), request.getFileId());
            
            String prompt = summarizePrompt.replace("{content}", request.getContent());
            String summaryText = generateText(prompt);
            
            // Save to database
            Summary summary = new Summary(request.getUserId(), request.getFileId(), 
                                        request.getContent(), summaryText);
            summaryRepository.save(summary);
            
            return new SummarizeResponse(summary.getId(), summaryText, summary.getCreatedAt());
        } catch (Exception e) {
            logger.error("Failed to generate summary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate summary", e);
        }
    }
    
    @Override
    public List<FlashcardDto> generateFlashcards(FlashcardRequest request) {
        try {
            logger.info("Generating {} flashcards for user: {}, file: {}", 
                       request.getCount(), request.getUserId(), request.getFileId());
            
            String prompt = flashcardsPrompt
                .replace("{content}", request.getContent())
                .replace("{count}", String.valueOf(request.getCount()));
            
            String jsonResponse = generateText(prompt);
            List<FlashcardDto> flashcards = parseFlashcardsFromJson(jsonResponse);
            
            // Save flashcards to database
            for (FlashcardDto dto : flashcards) {
                Flashcard flashcard = new Flashcard(request.getUserId(), request.getFileId(), 
                                                   request.getContent(), dto.getFront(), dto.getBack());
                // Note: In a full implementation, you'd have a FlashcardRepository
                dto.setId(flashcard.getId());
                dto.setCreatedAt(flashcard.getCreatedAt());
            }
            
            return flashcards;
        } catch (Exception e) {
            logger.error("Failed to generate flashcards: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate flashcards", e);
        }
    }
    
    @Override
    public QuizDto createQuiz(QuizRequest request) {
        try {
            logger.info("Generating quiz with {} questions for user: {}, file: {}", 
                       request.getQuestionCount(), request.getUserId(), request.getFileId());
            
            String prompt = quizPrompt
                .replace("{content}", request.getContent())
                .replace("{questionCount}", String.valueOf(request.getQuestionCount()));
            
            String jsonResponse = generateText(prompt);
            List<QuizQuestionDto> questions = parseQuizQuestionsFromJson(jsonResponse);
            
            // Save quiz to database
            Quiz quiz = new Quiz(request.getUserId(), request.getFileId(), 
                               request.getContent(), request.getTitle());
            // Note: In a full implementation, you'd have QuizRepository and QuizQuestionRepository
            
            return new QuizDto(quiz.getId(), quiz.getTitle(), questions, quiz.getCreatedAt());
        } catch (Exception e) {
            logger.error("Failed to generate quiz: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate quiz", e);
        }
    }
    
    @Override
    public ExplainResponse explainConcept(ExplainRequest request) {
        try {
            logger.info("Explaining concept: {}", request.getConcept());
            
            String prompt = explainPrompt
                .replace("{concept}", request.getConcept())
                .replace("{context}", request.getContext() != null ? request.getContext() : "");
            
            String explanation = generateText(prompt);
            return new ExplainResponse(explanation);
        } catch (Exception e) {
            logger.error("Failed to explain concept: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to explain concept", e);
        }
    }
    
    private String generateText(String prompt) {
        try {
            GenerateContentResponse response = model.generateContent(prompt);
            return ResponseHandler.getText(response);
        } catch (Exception e) {
            logger.error("Failed to generate text from Vertex AI: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate text", e);
        }
    }
    
    private List<FlashcardDto> parseFlashcardsFromJson(String jsonResponse) {
        try {
            // Extract JSON from response if it contains additional text
            String cleanJson = extractJsonFromResponse(jsonResponse);
            
            TypeReference<List<FlashcardJson>> typeRef = new TypeReference<List<FlashcardJson>>() {};
            List<FlashcardJson> flashcardJsons = objectMapper.readValue(cleanJson, typeRef);
            
            List<FlashcardDto> flashcards = new ArrayList<>();
            for (FlashcardJson json : flashcardJsons) {
                flashcards.add(new FlashcardDto(UUID.randomUUID(), json.front, json.back, Instant.now()));
            }
            
            return flashcards;
        } catch (Exception e) {
            logger.error("Failed to parse flashcards from JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse flashcards", e);
        }
    }
    
    private List<QuizQuestionDto> parseQuizQuestionsFromJson(String jsonResponse) {
        try {
            // Extract JSON from response if it contains additional text
            String cleanJson = extractJsonFromResponse(jsonResponse);
            
            TypeReference<List<QuizQuestionJson>> typeRef = new TypeReference<List<QuizQuestionJson>>() {};
            List<QuizQuestionJson> questionJsons = objectMapper.readValue(cleanJson, typeRef);
            
            List<QuizQuestionDto> questions = new ArrayList<>();
            for (QuizQuestionJson json : questionJsons) {
                questions.add(new QuizQuestionDto(UUID.randomUUID(), json.question, 
                    json.optionA, json.optionB, json.optionC, json.optionD, 
                    json.correctOption, Instant.now()));
            }
            
            return questions;
        } catch (Exception e) {
            logger.error("Failed to parse quiz questions from JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse quiz questions", e);
        }
    }
    
    private String extractJsonFromResponse(String response) {
        // Find the first '[' and last ']' to extract JSON array
        int startIndex = response.indexOf('[');
        int endIndex = response.lastIndexOf(']');
        
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
        
        // If no array brackets found, try to find object brackets
        startIndex = response.indexOf('{');
        endIndex = response.lastIndexOf('}');
        
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
        
        return response.trim();
    }
    
    private String loadPromptTemplate(String resourcePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Prompt template not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt template: " + resourcePath, e);
        }
    }
    
    // Helper classes for JSON parsing
    private static class FlashcardJson {
        public String front;
        public String back;
    }
    
    private static class QuizQuestionJson {
        public String question;
        public String optionA;
        public String optionB;
        public String optionC;
        public String optionD;
        public int correctOption;
    }
} 
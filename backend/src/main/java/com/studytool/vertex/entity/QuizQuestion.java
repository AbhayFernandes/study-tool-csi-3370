package com.studytool.vertex.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a quiz question in the ScyllaDB database.
 */
public class QuizQuestion {
    private UUID id;
    private UUID quizId;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int correctOption; // 1-4 for A-D
    private Instant createdAt;
    
    public QuizQuestion() {
    }
    
    public QuizQuestion(UUID id, UUID quizId, String question, String optionA, String optionB, 
                      String optionC, String optionD, int correctOption, Instant createdAt) {
        this.id = id;
        this.quizId = quizId;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.createdAt = createdAt;
    }
    
    public QuizQuestion(UUID quizId, String question, String optionA, String optionB, 
                       String optionC, String optionD, int correctOption) {
        this.id = UUID.randomUUID();
        this.quizId = quizId;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.createdAt = Instant.now();
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getQuizId() {
        return quizId;
    }
    
    public void setQuizId(UUID quizId) {
        this.quizId = quizId;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getOptionA() {
        return optionA;
    }
    
    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }
    
    public String getOptionB() {
        return optionB;
    }
    
    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }
    
    public String getOptionC() {
        return optionC;
    }
    
    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }
    
    public String getOptionD() {
        return optionD;
    }
    
    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }
    
    public int getCorrectOption() {
        return correctOption;
    }
    
    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuizQuestion that = (QuizQuestion) o;
        return correctOption == that.correctOption &&
               Objects.equals(id, that.id) &&
               Objects.equals(quizId, that.quizId) &&
               Objects.equals(question, that.question) &&
               Objects.equals(optionA, that.optionA) &&
               Objects.equals(optionB, that.optionB) &&
               Objects.equals(optionC, that.optionC) &&
               Objects.equals(optionD, that.optionD) &&
               Objects.equals(createdAt, that.createdAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, quizId, question, optionA, optionB, optionC, optionD, correctOption, createdAt);
    }
    
    @Override
    public String toString() {
        return "QuizQuestion{" +
               "id=" + id +
               ", quizId=" + quizId +
               ", question='" + question + '\'' +
               ", correctOption=" + correctOption +
               ", createdAt=" + createdAt +
               '}';
    }
} 
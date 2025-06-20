package com.studytool.vertex.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing a full flashcard set, including the cards themselves.
 */
public class FlashcardSetDto {
    private UUID setId;
    private UUID fileId;
    private Instant createdAt;
    private List<FlashcardDto> cards;

    public FlashcardSetDto() {}

    public FlashcardSetDto(UUID setId, UUID fileId, Instant createdAt, List<FlashcardDto> cards) {
        this.setId = setId;
        this.fileId = fileId;
        this.createdAt = createdAt;
        this.cards = cards;
    }

    public UUID getSetId() {
        return setId;
    }

    public void setSetId(UUID setId) {
        this.setId = setId;
    }

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<FlashcardDto> getCards() {
        return cards;
    }

    public void setCards(List<FlashcardDto> cards) {
        this.cards = cards;
    }
} 
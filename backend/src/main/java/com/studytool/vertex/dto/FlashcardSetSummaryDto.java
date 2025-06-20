package com.studytool.vertex.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Lightweight DTO summarizing a flashcard set for list views.
 */
public class FlashcardSetSummaryDto {
    private UUID setId;
    private UUID fileId;
    private Instant createdAt;
    private int cardCount;

    public FlashcardSetSummaryDto() {}

    public FlashcardSetSummaryDto(UUID setId, UUID fileId, Instant createdAt, int cardCount) {
        this.setId = setId;
        this.fileId = fileId;
        this.createdAt = createdAt;
        this.cardCount = cardCount;
    }

    public UUID getSetId() { return setId; }
    public void setSetId(UUID setId) { this.setId = setId; }

    public UUID getFileId() { return fileId; }
    public void setFileId(UUID fileId) { this.fileId = fileId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public int getCardCount() { return cardCount; }
    public void setCardCount(int cardCount) { this.cardCount = cardCount; }
} 
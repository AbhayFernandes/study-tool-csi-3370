package com.studytool.vertex.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.studytool.vertex.entity.Flashcard;

/**
 * Repository handling Flashcard persistence.
 */
public class FlashcardRepository {
    private static final Logger logger = LoggerFactory.getLogger(FlashcardRepository.class);

    private final CqlSession session;
    private final PreparedStatement insertStatement;
    private final PreparedStatement findBySetIdStatement;
    private final PreparedStatement findByUserIdStatement;

    public FlashcardRepository(CqlSession session) {
        this.session = session;
        this.insertStatement = session.prepare(
                "INSERT INTO flashcards (id, set_id, user_id, file_id, content, front, back, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
        this.findBySetIdStatement = session.prepare(
                "SELECT * FROM flashcards WHERE set_id = ? ALLOW FILTERING"
        );
        this.findByUserIdStatement = session.prepare(
                "SELECT * FROM flashcards WHERE user_id = ?"
        );
    }

    public Flashcard save(Flashcard flashcard) {
        try {
            session.execute(insertStatement.bind(
                    flashcard.getId(),
                    flashcard.getSetId(),
                    flashcard.getUserId(),
                    flashcard.getFileId(),
                    flashcard.getContent(),
                    flashcard.getFront(),
                    flashcard.getBack(),
                    flashcard.getCreatedAt(),
                    flashcard.getUpdatedAt()
            ));
            return flashcard;
        } catch (Exception e) {
            logger.error("Failed to save flashcard {}", flashcard.getId(), e);
            throw new RuntimeException("Failed to save flashcard", e);
        }
    }

    public void saveAll(List<Flashcard> cards) {
        cards.forEach(this::save);
    }

    public List<Flashcard> findBySetId(UUID setId) {
        try {
            ResultSet rs = session.execute(findBySetIdStatement.bind(setId));
            List<Flashcard> list = new ArrayList<>();
            for (Row row : rs) {
                list.add(mapRow(row));
            }
            return list;
        } catch (Exception e) {
            logger.error("Failed to fetch flashcards for set {}", setId, e);
            throw new RuntimeException("Failed to fetch flashcards", e);
        }
    }

    public Map<UUID, List<Flashcard>> findByUserGrouped(UUID userId) {
        try {
            ResultSet rs = session.execute(findByUserIdStatement.bind(userId));
            Map<UUID, List<Flashcard>> grouped = new HashMap<>();
            for (Row row : rs) {
                Flashcard card = mapRow(row);
                grouped.computeIfAbsent(card.getSetId(), k -> new ArrayList<>()).add(card);
            }
            return grouped;
        } catch (Exception e) {
            logger.error("Failed to fetch flashcards for user {}", userId, e);
            throw new RuntimeException("Failed to fetch flashcards", e);
        }
    }

    private Flashcard mapRow(Row row) {
        return new Flashcard(
                row.getUuid("id"),
                row.getUuid("set_id"),
                row.getUuid("user_id"),
                row.getUuid("file_id"),
                row.getString("content"),
                row.getString("front"),
                row.getString("back"),
                row.getInstant("created_at"),
                row.getInstant("updated_at")
        );
    }
} 
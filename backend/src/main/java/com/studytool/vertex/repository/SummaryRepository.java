package com.studytool.vertex.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.studytool.vertex.entity.Summary;

/**
 * Repository for Summary entity operations.
 */
public class SummaryRepository {
    private static final Logger logger = LoggerFactory.getLogger(SummaryRepository.class);
    
    private final CqlSession session;
    private final PreparedStatement insertStatement;
    private final PreparedStatement findByIdStatement;
    private final PreparedStatement findByUserIdStatement;
    private final PreparedStatement findByFileIdStatement;
    
    public SummaryRepository(CqlSession session) {
        this.session = session;
        this.insertStatement = session.prepare(
            "INSERT INTO summaries (id, user_id, file_id, content, summary, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)"
        );
        this.findByIdStatement = session.prepare(
            "SELECT id, user_id, file_id, content, summary, created_at, updated_at " +
            "FROM summaries WHERE id = ?"
        );
        this.findByUserIdStatement = session.prepare(
            "SELECT id, user_id, file_id, content, summary, created_at, updated_at " +
            "FROM summaries WHERE user_id = ?"
        );
        this.findByFileIdStatement = session.prepare(
            "SELECT id, user_id, file_id, content, summary, created_at, updated_at " +
            "FROM summaries WHERE file_id = ?"
        );
    }
    
    public Summary save(Summary summary) {
        try {
            session.execute(insertStatement.bind(
                summary.getId(),
                summary.getUserId(),
                summary.getFileId(),
                summary.getContent(),
                summary.getSummary(),
                summary.getCreatedAt(),
                summary.getUpdatedAt()
            ));
            logger.info("Summary saved with ID: {}", summary.getId());
            return summary;
        } catch (Exception e) {
            logger.error("Failed to save summary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save summary", e);
        }
    }
    
    public Optional<Summary> findById(UUID id) {
        try {
            ResultSet result = session.execute(findByIdStatement.bind(id));
            Row row = result.one();
            if (row != null) {
                return Optional.of(mapRowToSummary(row));
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to find summary by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to find summary", e);
        }
    }
    
    public List<Summary> findByUserId(UUID userId) {
        try {
            ResultSet result = session.execute(findByUserIdStatement.bind(userId));
            List<Summary> summaries = new ArrayList<>();
            for (Row row : result) {
                summaries.add(mapRowToSummary(row));
            }
            return summaries;
        } catch (Exception e) {
            logger.error("Failed to find summaries by user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to find summaries", e);
        }
    }
    
    public List<Summary> findByFileId(UUID fileId) {
        try {
            ResultSet result = session.execute(findByFileIdStatement.bind(fileId));
            List<Summary> summaries = new ArrayList<>();
            for (Row row : result) {
                summaries.add(mapRowToSummary(row));
            }
            return summaries;
        } catch (Exception e) {
            logger.error("Failed to find summaries by file ID {}: {}", fileId, e.getMessage(), e);
            throw new RuntimeException("Failed to find summaries", e);
        }
    }
    
    private Summary mapRowToSummary(Row row) {
        return new Summary(
            row.getUuid("id"),
            row.getUuid("user_id"),
            row.getUuid("file_id"),
            row.getString("content"),
            row.getString("summary"),
            row.getInstant("created_at"),
            row.getInstant("updated_at")
        );
    }
} 
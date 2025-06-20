package com.studytool.vertex.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.studytool.vertex.entity.Quiz;

/**
 * Repository for persisting Quiz metadata.
 */
public class QuizRepository {
    private static final Logger logger = LoggerFactory.getLogger(QuizRepository.class);

    private final CqlSession session;
    private final PreparedStatement insertStmt;

    public QuizRepository(CqlSession session) {
        this.session = session;
        this.insertStmt = session.prepare(
                "INSERT INTO quizzes (id, user_id, file_id, content, title, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)"
        );
    }

    public Quiz save(Quiz quiz) {
        try {
            session.execute(insertStmt.bind(
                    quiz.getId(),
                    quiz.getUserId(),
                    quiz.getFileId(),
                    quiz.getContent(),
                    quiz.getTitle(),
                    quiz.getCreatedAt(),
                    quiz.getUpdatedAt()
            ));
            return quiz;
        } catch (Exception e) {
            logger.error("Failed to save quiz {}", quiz.getId(), e);
            throw new RuntimeException("Failed to save quiz", e);
        }
    }
} 
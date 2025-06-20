package com.studytool.vertex.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.studytool.vertex.entity.QuizQuestion;

/**
 * Repository for persisting individual quiz questions.
 */
public class QuizQuestionRepository {
    private static final Logger logger = LoggerFactory.getLogger(QuizQuestionRepository.class);

    private final CqlSession session;
    private final PreparedStatement insertStmt;

    public QuizQuestionRepository(CqlSession session) {
        this.session = session;
        this.insertStmt = session.prepare(
                "INSERT INTO quiz_questions (id, quiz_id, question, option_a, option_b, option_c, option_d, correct_option, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
    }

    public QuizQuestion save(QuizQuestion q) {
        try {
            session.execute(insertStmt.bind(
                    q.getId(),
                    q.getQuizId(),
                    q.getQuestion(),
                    q.getOptionA(),
                    q.getOptionB(),
                    q.getOptionC(),
                    q.getOptionD(),
                    q.getCorrectOption(),
                    q.getCreatedAt()
            ));
            return q;
        } catch (Exception e) {
            logger.error("Failed to save quiz question {}", q.getId(), e);
            throw new RuntimeException("Failed to save quiz question", e);
        }
    }

    public void saveAll(java.util.List<QuizQuestion> list) {
        list.forEach(this::save);
    }
} 
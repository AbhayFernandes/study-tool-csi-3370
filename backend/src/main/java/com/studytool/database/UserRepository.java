package com.studytool.database;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.insertInto;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.update;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.select.Select;

/**
 * Repository class for User database operations using ScyllaDB Query Builder.
 * This class provides type-safe methods for CRUD operations on the users table.
 */
public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    
    private final CqlSession session;
    private final PreparedStatement insertUserStatement;
    private final PreparedStatement findByUsernameStatement;
    private final PreparedStatement findByIdStatement;
    private final PreparedStatement updatePasswordStatement;
    
    /**
     * Creates a new UserRepository instance.
     * 
     * @param session The ScyllaDB CqlSession
     */
    public UserRepository(CqlSession session) {
        this.session = session;
        
        // Prepare statements using Query Builder for better performance
        this.insertUserStatement = prepareInsertUserStatement();
        this.findByUsernameStatement = prepareFindByUsernameStatement();
        this.findByIdStatement = prepareFindByIdStatement();
        this.updatePasswordStatement = prepareUpdatePasswordStatement();
        
        logger.info("UserRepository initialized with prepared statements");
    }
    
    /**
     * Creates a new user in the database.
     * 
     * @param user The user to create
     * @return true if the user was created successfully
     */
    public boolean createUser(User user) {
        try {
            session.execute(insertUserStatement.bind(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getCreatedAt(),
                user.getUpdatedAt()
            ));
            
            logger.debug("Created user: {}", user.getUsername());
            return true;
        } catch (Exception e) {
            logger.error("Failed to create user: {}", user.getUsername(), e);
            return false;
        }
    }
    
    /**
     * Finds a user by username.
     * 
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<User> findByUsername(String username) {
        try {
            ResultSet resultSet = session.execute(findByUsernameStatement.bind(username));
            Row row = resultSet.one();
            
            if (row != null) {
                User user = mapRowToUser(row);
                logger.debug("Found user by username: {}", username);
                return Optional.of(user);
            }
            
            logger.debug("User not found by username: {}", username);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to find user by username: {}", username, e);
            return Optional.empty();
        }
    }
    
    /**
     * Finds a user by ID.
     * 
     * @param id The user ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<User> findById(UUID id) {
        try {
            ResultSet resultSet = session.execute(findByIdStatement.bind(id));
            Row row = resultSet.one();
            
            if (row != null) {
                User user = mapRowToUser(row);
                logger.debug("Found user by ID: {}", id);
                return Optional.of(user);
            }
            
            logger.debug("User not found by ID: {}", id);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to find user by ID: {}", id, e);
            return Optional.empty();
        }
    }
    
    /**
     * Updates a user's password.
     * 
     * @param userId The user ID
     * @param newPasswordHash The new password hash
     * @return true if the password was updated successfully
     */
    public boolean updatePassword(UUID userId, String newPasswordHash) {
        try {
            session.execute(updatePasswordStatement.bind(
                newPasswordHash,
                Instant.now(),
                userId
            ));
            
            logger.debug("Updated password for user ID: {}", userId);
            return true;
        } catch (Exception e) {
            logger.error("Failed to update password for user ID: {}", userId, e);
            return false;
        }
    }
    
    /**
     * Prepares the INSERT statement using Query Builder.
     */
    private PreparedStatement prepareInsertUserStatement() {
        Insert insert = insertInto("users")
            .value("id", bindMarker())
            .value("username", bindMarker())
            .value("password_hash", bindMarker())
            .value("created_at", bindMarker())
            .value("updated_at", bindMarker());
            
        return session.prepare(insert.build());
    }
    
    /**
     * Prepares the SELECT by username statement using Query Builder.
     */
    private PreparedStatement prepareFindByUsernameStatement() {
        Select select = selectFrom("users")
            .all()
            .whereColumn("username").isEqualTo(bindMarker());
            
        return session.prepare(select.build());
    }
    
    /**
     * Prepares the SELECT by ID statement using Query Builder.
     */
    private PreparedStatement prepareFindByIdStatement() {
        Select select = selectFrom("users")
            .all()
            .whereColumn("id").isEqualTo(bindMarker());
            
        return session.prepare(select.build());
    }
    
    /**
     * Prepares the UPDATE password statement using Query Builder.
     */
    private PreparedStatement prepareUpdatePasswordStatement() {
        return session.prepare(
            update("users")
                .setColumn("password_hash", bindMarker())
                .setColumn("updated_at", bindMarker())
                .whereColumn("id").isEqualTo(bindMarker())
                .build()
        );
    }
    
    /**
     * Maps a database row to a User object.
     * 
     * @param row The database row
     * @return The User object
     */
    private User mapRowToUser(Row row) {
        return new User(
            row.getUuid("id"),
            row.getString("username"),
            row.getString("password_hash"),
            row.getInstant("created_at"),
            row.getInstant("updated_at")
        );
    }
} 
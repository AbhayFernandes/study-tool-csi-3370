package com.studytool.database;

import java.time.Instant;
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

/**
 * Repository class for handling file database operations.
 */
public class FileRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileRepository.class);
    
    private final CqlSession session;
    private final PreparedStatement insertStatement;
    private final PreparedStatement findByIdStatement;
    private final PreparedStatement findByUserIdStatement;
    private final PreparedStatement findByStoredFilenameStatement;
    private final PreparedStatement deleteByIdStatement;
    private final PreparedStatement updateStatement;
    
    public FileRepository(CqlSession session) {
        this.session = session;
        
        // Prepare statements for better performance
        this.insertStatement = session.prepare(
            "INSERT INTO files (id, user_id, original_filename, stored_filename, file_size, " +
            "upload_time, file_path, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
        
        this.findByIdStatement = session.prepare(
            "SELECT * FROM files WHERE id = ?"
        );
        
        this.findByUserIdStatement = session.prepare(
            "SELECT * FROM files WHERE user_id = ?"
        );
        
        this.findByStoredFilenameStatement = session.prepare(
            "SELECT * FROM files WHERE stored_filename = ? ALLOW FILTERING"
        );
        
        this.deleteByIdStatement = session.prepare(
            "DELETE FROM files WHERE id = ?"
        );
        
        this.updateStatement = session.prepare(
            "UPDATE files SET original_filename = ?, stored_filename = ?, file_size = ?, " +
            "upload_time = ?, file_path = ?, updated_at = ? WHERE id = ?"
        );
        
        logger.info("FileRepository initialized with prepared statements");
    }
    
    /**
     * Saves a file record to the database.
     */
    public File save(File file) {
        try {
            session.execute(insertStatement.bind(
                file.getId(),
                file.getUserId(),
                file.getOriginalFilename(),
                file.getStoredFilename(),
                file.getFileSize(),
                file.getUploadTime(),
                file.getFilePath(),
                file.getCreatedAt(),
                file.getUpdatedAt()
            ));
            
            logger.debug("File record saved: {}", file.getId());
            return file;
        } catch (Exception e) {
            logger.error("Failed to save file record: {}", file.getId(), e);
            throw new RuntimeException("Failed to save file record", e);
        }
    }
    
    /**
     * Finds a file by its ID.
     */
    public Optional<File> findById(UUID id) {
        try {
            ResultSet result = session.execute(findByIdStatement.bind(id));
            Row row = result.one();
            
            if (row != null) {
                return Optional.of(mapRowToFile(row));
            }
            
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to find file by ID: {}", id, e);
            throw new RuntimeException("Failed to find file", e);
        }
    }
    
    /**
     * Finds all files uploaded by a specific user.
     */
    public List<File> findByUserId(UUID userId) {
        try {
            ResultSet result = session.execute(findByUserIdStatement.bind(userId));
            List<File> files = new ArrayList<>();
            
            for (Row row : result) {
                files.add(mapRowToFile(row));
            }
            
            logger.debug("Found {} files for user: {}", files.size(), userId);
            return files;
        } catch (Exception e) {
            logger.error("Failed to find files for user: {}", userId, e);
            throw new RuntimeException("Failed to find user files", e);
        }
    }
    
    /**
     * Finds a file by its stored filename.
     */
    public Optional<File> findByStoredFilename(String storedFilename) {
        try {
            ResultSet result = session.execute(findByStoredFilenameStatement.bind(storedFilename));
            Row row = result.one();
            
            if (row != null) {
                return Optional.of(mapRowToFile(row));
            }
            
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to find file by stored filename: {}", storedFilename, e);
            throw new RuntimeException("Failed to find file", e);
        }
    }
    
    /**
     * Deletes a file record by its ID.
     */
    public boolean deleteById(UUID id) {
        try {
            session.execute(deleteByIdStatement.bind(id));
            logger.debug("File record deleted: {}", id);
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete file record: {}", id, e);
            return false;
        }
    }
    
    /**
     * Updates an existing file record.
     */
    public File update(File file) {
        try {
            file.setUpdatedAt(Instant.now());
            
            session.execute(updateStatement.bind(
                file.getOriginalFilename(),
                file.getStoredFilename(),
                file.getFileSize(),
                file.getUploadTime(),
                file.getFilePath(),
                file.getUpdatedAt(),
                file.getId()
            ));
            
            logger.debug("File record updated: {}", file.getId());
            return file;
        } catch (Exception e) {
            logger.error("Failed to update file record: {}", file.getId(), e);
            throw new RuntimeException("Failed to update file record", e);
        }
    }
    
    /**
     * Maps a database row to a File object.
     */
    private File mapRowToFile(Row row) {
        File file = new File();
        file.setId(row.getUuid("id"));
        file.setUserId(row.getUuid("user_id"));
        file.setOriginalFilename(row.getString("original_filename"));
        file.setStoredFilename(row.getString("stored_filename"));
        file.setFileSize(row.getLong("file_size"));
        file.setUploadTime(row.getInstant("upload_time"));
        file.setFilePath(row.getString("file_path"));
        file.setCreatedAt(row.getInstant("created_at"));
        file.setUpdatedAt(row.getInstant("updated_at"));
        return file;
    }
} 
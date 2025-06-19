package com.studytool.database;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a file record in the database.
 * This entity tracks uploaded files with their metadata.
 */
public class File {
    private UUID id;
    private UUID userId;
    private String originalFilename;
    private String storedFilename;
    private long fileSize;
    private Instant uploadTime;
    private String filePath;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Default constructor
    public File() {}
    
    // Constructor for creating new file records
    public File(UUID userId, String originalFilename, String storedFilename, 
                long fileSize, String filePath) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.fileSize = fileSize;
        this.uploadTime = Instant.now();
        this.filePath = filePath;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    
    public String getStoredFilename() { return storedFilename; }
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }
    
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    
    public Instant getUploadTime() { return uploadTime; }
    public void setUploadTime(Instant uploadTime) { this.uploadTime = uploadTime; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", userId=" + userId +
                ", originalFilename='" + originalFilename + '\'' +
                ", storedFilename='" + storedFilename + '\'' +
                ", fileSize=" + fileSize +
                ", uploadTime=" + uploadTime +
                ", filePath='" + filePath + '\'' +
                '}';
    }
} 
package com.studytool.filestorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    
    private final String baseStoragePath;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".txt", ".pdf");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    public FileStorageService(String baseStoragePath) {
        this.baseStoragePath = baseStoragePath;
        initializeStorageDirectory();
    }
    
    private void initializeStorageDirectory() {
        try {
            Path storagePath = Paths.get(baseStoragePath);
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
                logger.info("Created storage directory: {}", baseStoragePath);
            }
        } catch (IOException e) {
            logger.error("Failed to create storage directory: {}", baseStoragePath, e);
            throw new RuntimeException("Failed to initialize file storage", e);
        }
    }
    
    public FileUploadResult storeFile(String userId, String originalFilename, InputStream fileContent, long fileSize) {
        validateFile(originalFilename, fileSize);
        
        try {
            // Create user-specific directory
            Path userDirectory = createUserDirectory(userId);
            
            // Generate unique filename
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path targetPath = userDirectory.resolve(uniqueFilename);
            
            // Store the file
            Files.copy(fileContent, targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            logger.info("File stored successfully: {} for user: {}", uniqueFilename, userId);
            
            return new FileUploadResult(
                uniqueFilename,
                originalFilename,
                targetPath.toString(),
                fileSize,
                userId
            );
            
        } catch (IOException e) {
            logger.error("Failed to store file: {} for user: {}", originalFilename, userId, e);
            throw new RuntimeException("Failed to store file", e);
        }
    }
    
    public File getFile(String userId, String filename) {
        Path userDirectory = Paths.get(baseStoragePath, userId);
        Path filePath = userDirectory.resolve(filename);
        
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found: " + filename);
        }
        
        return filePath.toFile();
    }
    
    public List<FileInfo> getUserFiles(String userId) {
        try {
            Path userDirectory = Paths.get(baseStoragePath, userId);
            
            if (!Files.exists(userDirectory)) {
                return Arrays.asList();
            }
            
            return Files.list(userDirectory)
                .filter(Files::isRegularFile)
                .map(path -> {
                    try {
                        return new FileInfo(
                            path.getFileName().toString(),
                            Files.size(path),
                            Files.getLastModifiedTime(path).toInstant()
                        );
                    } catch (IOException e) {
                        logger.error("Error reading file info for: {}", path, e);
                        return null;
                    }
                })
                .filter(fileInfo -> fileInfo != null)
                .toList();
                
        } catch (IOException e) {
            logger.error("Failed to list files for user: {}", userId, e);
            throw new RuntimeException("Failed to list user files", e);
        }
    }
    
    public boolean deleteFile(String userId, String filename) {
        try {
            Path userDirectory = Paths.get(baseStoragePath, userId);
            Path filePath = userDirectory.resolve(filename);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("File deleted: {} for user: {}", filename, userId);
                return true;
            }
            
            return false;
        } catch (IOException e) {
            logger.error("Failed to delete file: {} for user: {}", filename, userId, e);
            return false;
        }
    }
    
    private Path createUserDirectory(String userId) throws IOException {
        Path userDirectory = Paths.get(baseStoragePath, userId);
        if (!Files.exists(userDirectory)) {
            Files.createDirectories(userDirectory);
            logger.info("Created user directory for: {}", userId);
        }
        return userDirectory;
    }
    
    private void validateFile(String filename, long fileSize) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        
        if (fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + MAX_FILE_SIZE + " bytes");
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("File type not allowed. Supported types: " + ALLOWED_EXTENSIONS);
        }
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
} 
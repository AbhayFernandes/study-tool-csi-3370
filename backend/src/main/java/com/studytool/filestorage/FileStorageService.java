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
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.studytool.database.FileRepository;
import com.studytool.database.User;
import com.studytool.database.UserRepository;

public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    
    private final String baseStoragePath;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".txt", ".pdf");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    public FileStorageService(String baseStoragePath, FileRepository fileRepository, UserRepository userRepository) {
        this.baseStoragePath = baseStoragePath;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
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
    
    /**
     * Resolves a username to a UUID by looking up the user in the database.
     * 
     * @param username The username to resolve
     * @return The user's UUID
     * @throws RuntimeException if the user is not found
     */
    private UUID resolveUserIdFromUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }
        return user.get().getId();
    }

    public FileUploadResult storeFile(String userId, String originalFilename, InputStream fileContent, long fileSize) {
        validateFile(originalFilename, fileSize);
        
        try {
            // Resolve username to UUID
            UUID userUuid = resolveUserIdFromUsername(userId);
            
            // Create user-specific directory
            Path userDirectory = createUserDirectory(userId);
            
            // Generate unique filename
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path targetPath = userDirectory.resolve(uniqueFilename);
            
            // Store the file
            Files.copy(fileContent, targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Create file record in database
            com.studytool.database.File fileRecord = new com.studytool.database.File(
                userUuid,
                originalFilename,
                uniqueFilename,
                fileSize,
                targetPath.toString()
            );
            
            // Save to database
            fileRecord = fileRepository.save(fileRecord);
            
            logger.info("File stored successfully: {} for user: {} with ID: {}", 
                       uniqueFilename, userId, fileRecord.getId());
            
            return new FileUploadResult(
                fileRecord.getId(),
                uniqueFilename,
                originalFilename,
                targetPath.toString(),
                fileSize,
                userId,
                fileRecord.getUploadTime()
            );
            
        } catch (IOException e) {
            logger.error("Failed to store file: {} for user: {}", originalFilename, userId, e);
            throw new RuntimeException("Failed to store file", e);
        } catch (Exception e) {
            logger.error("Failed to save file record to database: {} for user: {}", originalFilename, userId, e);
            throw new RuntimeException("Failed to save file record", e);
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
            UUID userUuid = resolveUserIdFromUsername(userId);
            List<com.studytool.database.File> fileRecords = fileRepository.findByUserId(userUuid);
            
            return fileRecords.stream()
                .map(fileRecord -> new FileInfo(
                    fileRecord.getId(),
                    fileRecord.getOriginalFilename(),
                    fileRecord.getStoredFilename(),
                    fileRecord.getFileSize(),
                    fileRecord.getUploadTime(),
                    fileRecord.getFilePath()
                ))
                .toList();
                
        } catch (Exception e) {
            logger.error("Failed to list files for user: {}", userId, e);
            throw new RuntimeException("Failed to list user files", e);
        }
    }
    
    public boolean deleteFile(String userId, String filename) {
        try {
            // Resolve username to UUID
            UUID userUuid = resolveUserIdFromUsername(userId);
            
            // Find file record by stored filename
            Optional<com.studytool.database.File> fileRecord = fileRepository.findByStoredFilename(filename);
            
            if (fileRecord.isPresent()) {
                com.studytool.database.File file = fileRecord.get();
                
                // Verify the file belongs to the user
                if (!file.getUserId().equals(userUuid)) {
                    logger.warn("User {} attempted to delete file {} that doesn't belong to them", userId, filename);
                    return false;
                }
                
                // Delete physical file
                Path filePath = Paths.get(file.getFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
                
                // Delete database record
                fileRepository.deleteById(file.getId());
                
                logger.info("File deleted: {} for user: {}", filename, userId);
                return true;
            }
            
            return false;
        } catch (IOException e) {
            logger.error("Failed to delete file: {} for user: {}", filename, userId, e);
            return false;
        } catch (Exception e) {
            logger.error("Failed to delete file record: {} for user: {}", filename, userId, e);
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
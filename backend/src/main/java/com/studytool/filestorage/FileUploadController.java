package com.studytool.filestorage;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    private final FileStorageService fileStorageService;
    
    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
    
    public void registerRoutes(Javalin app) {
        app.post("/api/files/upload", this::uploadFile);
        app.get("/api/files", this::listFiles);
        app.get("/api/files/{filename}", this::downloadFile);
        app.delete("/api/files/{filename}", this::deleteFile);
    }
    
    private void uploadFile(Context ctx) {
        try {
            // Get user ID from session or auth header (for now, we'll use a placeholder)
            String userId = getUserId(ctx);
            
            UploadedFile uploadedFile = ctx.uploadedFile("file");
            if (uploadedFile == null) {
                ctx.status(400).json(Map.of("error", "No file uploaded"));
                return;
            }
            
            FileUploadResult result = fileStorageService.storeFile(
                userId,
                uploadedFile.filename(),
                uploadedFile.content(),
                uploadedFile.size()
            );
            
            ctx.status(200).json(Map.of(
                "message", "File uploaded successfully",
                "file", Map.of(
                    "id", result.fileId(),
                    "filename", result.storedFilename(),
                    "originalFilename", result.originalFilename(),
                    "size", result.fileSize(),
                    "uploadTime", result.uploadTime(),
                    "userId", result.userId()
                )
            ));
            
        } catch (IllegalArgumentException e) {
            logger.warn("File upload validation error: {}", e.getMessage());
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("File upload error", e);
            ctx.status(500).json(Map.of("error", "File upload failed"));
        }
    }
    
    private void listFiles(Context ctx) {
        try {
            String userId = getUserId(ctx);
            List<FileInfo> files = fileStorageService.getUserFiles(userId);
            
            // Convert FileInfo to FileDto for better API response
            List<FileDto> fileDtos = files.stream()
                .map(fileInfo -> FileDto.fromFileInfo(fileInfo, userId))
                .toList();
            
            ctx.status(200).json(Map.of("files", fileDtos));
            
        } catch (Exception e) {
            logger.error("Error listing files", e);
            ctx.status(500).json(Map.of("error", "Failed to list files"));
        }
    }
    
    private void downloadFile(Context ctx) {
        try {
            String userId = getUserId(ctx);
            String filename = ctx.pathParam("filename");
            
            var file = fileStorageService.getFile(userId, filename);
            
            ctx.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            ctx.result(java.nio.file.Files.newInputStream(file.toPath()));
            
        } catch (RuntimeException e) {
            logger.warn("File download error: {}", e.getMessage());
            ctx.status(404).json(Map.of("error", "File not found"));
        } catch (Exception e) {
            logger.error("File download error", e);
            ctx.status(500).json(Map.of("error", "File download failed"));
        }
    }
    
    private void deleteFile(Context ctx) {
        try {
            String userId = getUserId(ctx);
            String filename = ctx.pathParam("filename");
            
            boolean deleted = fileStorageService.deleteFile(userId, filename);
            
            if (deleted) {
                ctx.status(200).json(Map.of("message", "File deleted successfully"));
            } else {
                ctx.status(404).json(Map.of("error", "File not found"));
            }
            
        } catch (Exception e) {
            logger.error("File deletion error", e);
            ctx.status(500).json(Map.of("error", "File deletion failed"));
        }
    }
    
    private String getUserId(Context ctx) {
        // TODO: Extract user ID from JWT token or session
        // For now, we'll use a header or default to "anonymous"
        String userId = ctx.header("X-User-ID");
        if (userId == null || userId.trim().isEmpty()) {
            userId = "anonymous";
        }
        return userId;
    }
} 
package com.studytool.filestorage;

public record FileUploadResult(
    String storedFilename,
    String originalFilename,
    String filePath,
    long fileSize,
    String userId
) {
} 
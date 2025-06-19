package com.studytool.filestorage;

import java.time.Instant;
import java.util.UUID;

public record FileUploadResult(
    UUID fileId,
    String storedFilename,
    String originalFilename,
    String filePath,
    long fileSize,
    String userId,
    Instant uploadTime
) {
} 
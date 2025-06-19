package com.studytool.filestorage;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for file information in API responses.
 */
public record FileDto(
    UUID id,
    String originalFilename,
    String storedFilename,
    long fileSize,
    Instant uploadTime,
    String userId
) {
    /**
     * Creates a FileDto from a FileInfo object.
     */
    public static FileDto fromFileInfo(FileInfo fileInfo, String userId) {
        return new FileDto(
            fileInfo.fileId(),
            fileInfo.originalFilename(),
            fileInfo.storedFilename(),
            fileInfo.size(),
            fileInfo.uploadTime(),
            userId
        );
    }
} 
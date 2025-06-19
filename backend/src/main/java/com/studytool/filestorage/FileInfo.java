package com.studytool.filestorage;

import java.time.Instant;
import java.util.UUID;

public record FileInfo(
    UUID fileId,
    String originalFilename,
    String storedFilename,
    long size,
    Instant uploadTime,
    String filePath
) {
} 
package com.studytool.filestorage;

import java.time.Instant;

public record FileInfo(
    String filename,
    long size,
    Instant lastModified
) {
} 
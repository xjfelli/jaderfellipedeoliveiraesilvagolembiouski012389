package com.gerenciadorartistas.backend.shared.dto;

import java.time.LocalDateTime;

public record UploadResult(
    String fileName,
    String filePath,
    String presignedUrl,
    Long fileSize,
    String contentType,
    LocalDateTime uploadedAt,
    Integer presignedUrlExpiryMinutes
) {
    public static UploadResult of(
        String fileName,
        String filePath,
        String presignedUrl,
        Long fileSize,
        String contentType,
        Integer presignedUrlExpiryMinutes
    ) {
        return new UploadResult(
            fileName,
            filePath,
            presignedUrl,
            fileSize,
            contentType,
            LocalDateTime.now(),
            presignedUrlExpiryMinutes
        );
    }
}

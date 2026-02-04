package com.gerenciadorartistas.backend.features.album.dto;

import java.time.LocalDateTime;

public record AlbumNotificationDTO(
    String action, // CREATE, UPDATE, DELETE
    Long id,
    String title,
    Integer releaseYear,
    String coverUrl,
    LocalDateTime createdAt
) {}

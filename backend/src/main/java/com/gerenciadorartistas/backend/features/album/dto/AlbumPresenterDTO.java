package com.gerenciadorartistas.backend.features.album.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record AlbumPresenterDTO(
    Long id,
    String title,
    Integer releaseYear,
    String recordLabel,
    Integer trackCount,
    String coverUrl,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Set<ArtistSimplifiedPresenterDTO> artists
) {
    public record ArtistSimplifiedPresenterDTO(
        Long id,
        String name,
        String musicalGenre,
        String photoUrl
    ) {}
}

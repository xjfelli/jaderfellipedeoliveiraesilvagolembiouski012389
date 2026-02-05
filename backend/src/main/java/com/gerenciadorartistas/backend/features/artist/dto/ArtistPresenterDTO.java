package com.gerenciadorartistas.backend.features.artist.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record ArtistPresenterDTO(
    Long id,
    String name,
    String musicalGenre,
    String biography,
    String countryOfOrigin,
    String photoUrl,
    String storageId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Set<AlbumSimplifiedPresenterDTO> albums
) {
    public record AlbumSimplifiedPresenterDTO(
        Long id,
        String title,
        Integer releaseYear,
        String coverUrl
    ) {}
}

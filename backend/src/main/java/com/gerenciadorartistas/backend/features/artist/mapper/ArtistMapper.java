package com.gerenciadorartistas.backend.features.artist.mapper;

import com.gerenciadorartistas.backend.features.artist.dto.ArtistDTO;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistPresenterDTO;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistPresenterDTO.AlbumSimplifiedPresenterDTO;
import com.gerenciadorartistas.backend.features.artist.entity.Artist;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {

    public ArtistPresenterDTO toDto(Artist artista) {
        return toDto(artista, false);
    }

    public ArtistPresenterDTO toDto(Artist artista, boolean includeAlbums) {
        if (artista == null) {
            return null;
        }

        Set<AlbumSimplifiedPresenterDTO> albums = null;
        if (includeAlbums && artista.getAlbums() != null) {
            albums = artista
                .getAlbums()
                .stream()
                .map(album ->
                    new AlbumSimplifiedPresenterDTO(
                        album.getId(),
                        album.getTitle(),
                        album.getReleaseYear(),
                        album.getCoverUrl()
                    )
                )
                .collect(Collectors.toSet());
        }

        return new ArtistPresenterDTO(
            artista.getId(),
            artista.getName(),
            artista.getMusicGenre(),
            artista.getBiography(),
            artista.getCountryOfOrigin(),
            artista.getPhotoUrl(),
            artista.getStorageId(),
            artista.getCreatedAt(),
            artista.getUpdatedAt(),
            albums
        );
    }

    public Artist fromDto(ArtistDTO dto) {
        if (dto == null) {
            return null;
        }

        Artist artist = new Artist();
        artist.setName(dto.getName());
        artist.setMusicGenre(dto.getMusicalGenre());
        artist.setBiography(dto.getBiography());
        artist.setCountryOfOrigin(dto.getCountryOfOrigin());
        artist.setPhotoUrl(dto.getPhotoUrl());
        // Note: createdAt and updatedAt are typically managed by the entity itself

        return artist;
    }
}

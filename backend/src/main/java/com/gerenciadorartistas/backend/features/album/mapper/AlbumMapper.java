package com.gerenciadorartistas.backend.features.album.mapper;

import com.gerenciadorartistas.backend.features.album.dto.AlbumDTO;
import com.gerenciadorartistas.backend.features.album.dto.AlbumPresenterDTO;
import com.gerenciadorartistas.backend.features.album.dto.AlbumPresenterDTO.ArtistSimplifiedPresenterDTO;
import com.gerenciadorartistas.backend.features.album.entity.Album;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AlbumMapper {

    public AlbumPresenterDTO toPresenterDTO(Album album) {
        return toPresenterDTO(album, false);
    }

    public Album toEntity(AlbumDTO dto) {
        if (dto == null) {
            return null;
        }

        Album album = new Album();
        album.setTitle(dto.getTitle());
        album.setReleaseYear(dto.getReleaseYear());
        album.setRecordLabel(dto.getRecordLabel());
        album.setTrackCount(dto.getTrackCount());
        album.setCoverUrl(dto.getCoverUrl());
        album.setDescription(dto.getDescription());
        return album;
    }

    public AlbumPresenterDTO toPresenterDTO(
        Album album,
        boolean includeArtists
    ) {
        if (album == null) {
            return null;
        }

        Set<ArtistSimplifiedPresenterDTO> artists = null;
        if (includeArtists && album.getArtists() != null) {
            artists = album
                .getArtists()
                .stream()
                .map(artist ->
                    new ArtistSimplifiedPresenterDTO(
                        artist.getId(),
                        artist.getName(),
                        artist.getMusicGenre(),
                        artist.getPhotoUrl()
                    )
                )
                .collect(Collectors.toSet());
        }

        return new AlbumPresenterDTO(
            album.getId(),
            album.getTitle(),
            album.getReleaseYear(),
            album.getRecordLabel(),
            album.getTrackCount(),
            album.getCoverUrl(),
            album.getDescription(),
            album.getCreatedAt(),
            album.getUpdatedAt(),
            artists
        );
    }
}

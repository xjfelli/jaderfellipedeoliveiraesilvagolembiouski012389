package com.gerenciadorartistas.backend.features.artist.mapper;

import com.gerenciadorartistas.backend.features.album.entity.Album;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistDTO;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistPresenterDTO;
import com.gerenciadorartistas.backend.features.artist.entity.Artist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ArtistMapperTest {

    private ArtistMapper mapper;
    private Artist artist;
    private ArtistDTO artistDTO;

    @BeforeEach
    void setUp() {
        mapper = new ArtistMapper();

        artist = new Artist();
        artist.setId(1L);
        artist.setName("Test Artist");
        artist.setMusicGenre("Rock");
        artist.setBiography("Bio");
        artist.setCountryOfOrigin("USA");
        artist.setPhotoUrl("photo.jpg");
        artist.setStorageId("storage-id");
        artist.setCreatedAt(LocalDateTime.now());
        artist.setUpdatedAt(LocalDateTime.now());

        artistDTO = new ArtistDTO();
        artistDTO.setName("Test Artist");
        artistDTO.setMusicalGenre("Rock");
        artistDTO.setBiography("Bio");
        artistDTO.setCountryOfOrigin("USA");
        artistDTO.setPhotoUrl("photo.jpg");
    }

    @Test
    void toDto_WithoutAlbums_ShouldMapCorrectly() {
        ArtistPresenterDTO result = mapper.toDto(artist, false);

        assertNotNull(result);
        assertEquals(artist.getId(), result.id());
        assertEquals(artist.getName(), result.name());
        assertEquals(artist.getMusicGenre(), result.musicalGenre());
        assertNull(result.albums());
    }

    @Test
    void toDto_WithAlbums_ShouldIncludeAlbums() {
        Album album = new Album();
        album.setId(1L);
        album.setTitle("Album Title");
        album.setReleaseYear(2023);
        album.setCoverUrl("cover.jpg");
        
        artist.getAlbums().add(album);

        ArtistPresenterDTO result = mapper.toDto(artist, true);

        assertNotNull(result);
        assertNotNull(result.albums());
        assertEquals(1, result.albums().size());
    }

    @Test
    void toDto_WithNullArtist_ShouldReturnNull() {
        ArtistPresenterDTO result = mapper.toDto(null, false);
        assertNull(result);
    }

    @Test
    void fromDto_ShouldMapCorrectly() {
        Artist result = mapper.fromDto(artistDTO);

        assertNotNull(result);
        assertEquals(artistDTO.getName(), result.getName());
        assertEquals(artistDTO.getMusicalGenre(), result.getMusicGenre());
        assertEquals(artistDTO.getBiography(), result.getBiography());
        assertEquals(artistDTO.getCountryOfOrigin(), result.getCountryOfOrigin());
    }

    @Test
    void fromDto_WithNullDTO_ShouldReturnNull() {
        Artist result = mapper.fromDto(null);
        assertNull(result);
    }
}

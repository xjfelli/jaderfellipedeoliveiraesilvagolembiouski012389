package com.gerenciadorartistas.backend.features.artist.entity;

import com.gerenciadorartistas.backend.features.album.entity.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    private Artist artist;
    private Album album;

    @BeforeEach
    void setUp() {
        artist = new Artist();
        artist.setName("Test Artist");
        artist.setMusicGenre("Rock");
        
        album = new Album();
        album.setId(1L);
    }

    @Test
    void update_ShouldUpdateArtistFields() {
        artist.update("New Name", "Jazz", "New Bio", "Brazil", "new-photo.jpg");

        assertEquals("New Name", artist.getName());
        assertEquals("Jazz", artist.getMusicGenre());
        assertEquals("New Bio", artist.getBiography());
        assertEquals("Brazil", artist.getCountryOfOrigin());
        assertEquals("new-photo.jpg", artist.getPhotoUrl());
    }

    @Test
    void addAlbum_ShouldAddAlbumToArtist() {
        artist.addAlbum(album);

        assertTrue(artist.getAlbums().contains(album));
        assertTrue(album.getArtists().contains(artist));
    }

    @Test
    void removeAlbum_ShouldRemoveAlbumFromArtist() {
        artist.addAlbum(album);
        artist.removeAlbum(album);

        assertFalse(artist.getAlbums().contains(album));
        assertFalse(album.getArtists().contains(artist));
    }

    @Test
    void prePersist_ShouldSetCreatedAtAndUpdatedAt() {
        artist.prePersist();

        assertNotNull(artist.getCreatedAt());
        assertNotNull(artist.getUpdatedAt());
    }

    @Test
    void preUpdate_ShouldUpdateUpdatedAt() throws InterruptedException {
        artist.prePersist();
        LocalDateTime oldUpdatedAt = artist.getUpdatedAt();
        
        Thread.sleep(10);
        artist.preUpdate();

        assertNotEquals(oldUpdatedAt, artist.getUpdatedAt());
    }
}

package com.gerenciadorartistas.backend.features.album.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

    @Test
    void settersAndGetters_ShouldWork() {
        Album album = new Album();
        album.setTitle("Test");
        album.setReleaseYear(2023);
        
        assertEquals("Test", album.getTitle());
        assertEquals(2023, album.getReleaseYear());
    }
}

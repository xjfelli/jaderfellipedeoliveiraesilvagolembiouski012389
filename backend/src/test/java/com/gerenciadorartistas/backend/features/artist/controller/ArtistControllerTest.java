package com.gerenciadorartistas.backend.features.artist.controller;

import com.gerenciadorartistas.backend.features.artist.dto.ArtistDTO;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistPresenterDTO;
import com.gerenciadorartistas.backend.features.artist.service.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistControllerTest {

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private ArtistController artistController;

    private ArtistPresenterDTO artistPresenterDTO;
    private ArtistDTO artistDTO;

    @BeforeEach
    void setUp() {
        artistPresenterDTO = new ArtistPresenterDTO(
            1L,
            "Test Artist",
            "Rock",
            "Bio",
            "USA",
            "photo.jpg",
            "storage-id",
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );

        artistDTO = new ArtistDTO();
        artistDTO.setName("Test Artist");
        artistDTO.setMusicalGenre("Rock");
        artistDTO.setBiography("Bio");
        artistDTO.setCountryOfOrigin("USA");
    }

    @Test
    void findAll_ShouldReturnArtistsList() {
        List<ArtistPresenterDTO> artists = Arrays.asList(artistPresenterDTO);
        when(artistService.findAll()).thenReturn(artists);

        ResponseEntity<List<ArtistPresenterDTO>> response = artistController.findAll(false);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void findById_WhenExists_ShouldReturnArtist() {
        when(artistService.findById(1L)).thenReturn(artistPresenterDTO);

        ResponseEntity<ArtistPresenterDTO> response = artistController.findById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Test Artist", response.getBody().name());
    }

    @Test
    void findById_WhenNotExists_ShouldReturnNotFound() {
        when(artistService.findById(1L)).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<ArtistPresenterDTO> response = artistController.findById(1L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void delete_WhenExists_ShouldDeleteArtist() {
        doNothing().when(artistService).delete(1L);

        ResponseEntity<Void> response = artistController.delete(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(artistService).delete(1L);
    }

    @Test
    void searchByName_ShouldReturnMatchingArtists() {
        when(artistService.searchByName(anyString(), anyString()))
            .thenReturn(Arrays.asList(artistPresenterDTO));

        ResponseEntity<List<ArtistPresenterDTO>> response = artistController.searchByName("Test", "asc");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void findByGenre_ShouldReturnArtistsByGenre() {
        when(artistService.findByGenre("Rock"))
            .thenReturn(Arrays.asList(artistPresenterDTO));

        ResponseEntity<List<ArtistPresenterDTO>> response = artistController.findByGenre("Rock");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }
}

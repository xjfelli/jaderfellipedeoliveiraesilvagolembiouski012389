package com.gerenciadorartistas.backend.features.album.service;

import com.gerenciadorartistas.backend.features.album.dto.AlbumDTO;
import com.gerenciadorartistas.backend.features.album.dto.AlbumPresenterDTO;
import com.gerenciadorartistas.backend.features.album.entity.Album;
import com.gerenciadorartistas.backend.features.album.mapper.AlbumMapper;
import com.gerenciadorartistas.backend.features.album.repository.AlbumRepository;
import com.gerenciadorartistas.backend.shared.dto.UploadResult;
import com.gerenciadorartistas.backend.shared.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private AlbumService albumService;

    private Album album;
    private AlbumDTO albumDTO;
    private AlbumPresenterDTO albumPresenterDTO;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        album = new Album();
        album.setId(1L);
        album.setTitle("Test Album");
        album.setReleaseYear(2023);
        album.setRecordLabel("Label");
        album.setCoverUrl("albums/test/cover.jpg");
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());

        albumDTO = new AlbumDTO();
        albumDTO.setTitle("Test Album");
        albumDTO.setReleaseYear(2023);
        albumDTO.setRecordLabel("Label");

        albumPresenterDTO = new AlbumPresenterDTO(
            1L, "Test Album", 2023, "Label", 10, "cover.jpg", "Desc",
            LocalDateTime.now(), LocalDateTime.now(), null
        );

        file = mock(MultipartFile.class);
    }

    @Test
    void findAll_ShouldReturnListOfAlbums() {
        when(albumRepository.findAll()).thenReturn(Arrays.asList(album));
        when(albumMapper.toPresenterDTO(any(), eq(false))).thenReturn(albumPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "path", "presigned", 1000L, "image/jpeg", 30));

        List<AlbumPresenterDTO> result = albumService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(albumRepository).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnAlbum() {
        when(albumRepository.findByIdWithArtists(1L)).thenReturn(album);
        when(albumMapper.toPresenterDTO(any(), eq(true))).thenReturn(albumPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "path", "presigned", 1000L, "image/jpeg", 30));

        AlbumPresenterDTO result = albumService.findById(1L);

        assertNotNull(result);
        assertEquals("Test Album", result.title());
    }

    @Test
    void create_WithValidData_ShouldCreateAlbum() {
        when(file.isEmpty()).thenReturn(false);
        when(fileUploadService.uploadFile(any(), anyString()))
            .thenReturn(UploadResult.of("file.jpg", "path", "presigned", 1000L, "image/jpeg", 30));
        when(albumMapper.toEntity(any())).thenReturn(album);
        when(albumRepository.save(any())).thenReturn(album);
        when(albumMapper.toPresenterDTO(any(), eq(false))).thenReturn(albumPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "path", "presigned", 1000L, "image/jpeg", 30));

        AlbumPresenterDTO result = albumService.create(albumDTO, file);

        assertNotNull(result);
        verify(albumRepository).save(any());
    }

    @Test
    void delete_WhenExists_ShouldDeleteAlbum() {
        when(albumRepository.existsById(1L)).thenReturn(true);

        albumService.delete(1L);

        verify(albumRepository).deleteById(1L);
    }
}

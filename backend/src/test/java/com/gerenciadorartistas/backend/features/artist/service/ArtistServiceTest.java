package com.gerenciadorartistas.backend.features.artist.service;

import com.gerenciadorartistas.backend.features.album.entity.Album;
import com.gerenciadorartistas.backend.features.album.repository.AlbumRepository;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistDTO;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistPresenterDTO;
import com.gerenciadorartistas.backend.features.artist.entity.Artist;
import com.gerenciadorartistas.backend.features.artist.mapper.ArtistMapper;
import com.gerenciadorartistas.backend.features.artist.repository.ArtistRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistMapper artistMapper;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private ArtistService artistService;

    private Artist artist;
    private ArtistDTO artistDTO;
    private ArtistPresenterDTO artistPresenterDTO;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        artist = new Artist();
        artist.setId(1L);
        artist.setName("Test Artist");
        artist.setMusicGenre("Rock");
        artist.setBiography("Test Bio");
        artist.setCountryOfOrigin("USA");
        artist.setPhotoUrl("artists/test-id/photo.jpg");
        artist.setStorageId("test-id");
        artist.setCreatedAt(LocalDateTime.now());
        artist.setUpdatedAt(LocalDateTime.now());

        artistDTO = new ArtistDTO();
        artistDTO.setName("Test Artist");
        artistDTO.setMusicalGenre("Rock");
        artistDTO.setBiography("Test Bio");
        artistDTO.setCountryOfOrigin("USA");

        artistPresenterDTO = new ArtistPresenterDTO(
            1L,
            "Test Artist",
            "Rock",
            "Test Bio",
            "USA",
            "presigned-url",
            "test-id",
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );

        file = mock(MultipartFile.class);
    }

    @Test
    void findAll_ShouldReturnListOfArtists() {
        List<Artist> artists = Arrays.asList(artist);
        when(artistRepository.findAll()).thenReturn(artists);
        when(artistMapper.toDto(any(Artist.class), eq(false))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        List<ArtistPresenterDTO> result = artistService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(artistRepository).findAll();
    }

    @Test
    void findAllWithAlbums_ShouldReturnListOfArtistsWithAlbums() {
        List<Artist> artists = Arrays.asList(artist);
        when(artistRepository.findAllWithAlbums()).thenReturn(artists);
        when(artistMapper.toDto(any(Artist.class), eq(true))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        List<ArtistPresenterDTO> result = artistService.findAllWithAlbums();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(artistRepository).findAllWithAlbums();
    }

    @Test
    void findAllPaginated_ShouldReturnPagedArtists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> artistPage = new PageImpl<>(Arrays.asList(artist));
        
        when(artistRepository.findAll(any(Pageable.class))).thenReturn(artistPage);
        when(artistMapper.toDto(any(Artist.class), eq(false))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        Page<ArtistPresenterDTO> result = artistService.findAllPaginated(pageable, false);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(artistRepository).findAll(pageable);
    }

    @Test
    void findById_WhenArtistExists_ShouldReturnArtist() {
        when(artistRepository.findByIdWithAlbums(1L)).thenReturn(artist);
        when(artistMapper.toDto(any(Artist.class), eq(true))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        ArtistPresenterDTO result = artistService.findById(1L);

        assertNotNull(result);
        assertEquals("Test Artist", result.name());
        verify(artistRepository).findByIdWithAlbums(1L);
    }

    @Test
    void findById_WhenArtistNotExists_ShouldThrowException() {
        when(artistRepository.findByIdWithAlbums(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> artistService.findById(1L));
    }

    @Test
    void create_WithValidData_ShouldCreateArtist() {
        when(artistRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(Collections.emptyList());
        when(file.isEmpty()).thenReturn(false);
        when(fileUploadService.uploadFile(any(), anyString()))
            .thenReturn(UploadResult.of("photo.jpg", "artists/test-id/photo.jpg", "presigned-url", 1000L, "image/jpeg", 30));
        when(artistMapper.fromDto(any(ArtistDTO.class))).thenReturn(artist);
        when(artistRepository.save(any(Artist.class))).thenReturn(artist);
        when(artistMapper.toDto(any(Artist.class), eq(false))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        ArtistPresenterDTO result = artistService.create(artistDTO, file);

        assertNotNull(result);
        assertEquals("Test Artist", result.name());
        verify(artistRepository).save(any(Artist.class));
    }

    @Test
    void create_WithDuplicateName_ShouldThrowException() {
        when(artistRepository.findByNameContainingIgnoreCase(anyString()))
            .thenReturn(Arrays.asList(artist));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> artistService.create(artistDTO, file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void create_WithoutFile_ShouldThrowException() {
        when(artistRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(Collections.emptyList());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> artistService.create(artistDTO, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void update_WithValidData_ShouldUpdateArtist() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistRepository.save(any(Artist.class))).thenReturn(artist);
        when(artistMapper.toDto(any(Artist.class), eq(false))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        ArtistPresenterDTO result = artistService.update(1L, artistDTO);

        assertNotNull(result);
        verify(artistRepository).save(artist);
    }

    @Test
    void update_WhenArtistNotExists_ShouldThrowException() {
        when(artistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> artistService.update(1L, artistDTO));
    }

    @Test
    void delete_WhenArtistExists_ShouldDeleteArtist() {
        when(artistRepository.existsById(1L)).thenReturn(true);

        artistService.delete(1L);

        verify(artistRepository).deleteById(1L);
    }

    @Test
    void delete_WhenArtistNotExists_ShouldThrowException() {
        when(artistRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> artistService.delete(1L));
    }

    @Test
    void addAlbum_WithValidData_ShouldAddAlbumToArtist() {
        Album album = new Album();
        album.setId(1L);
        album.setArtists(new HashSet<>());

        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(artistRepository.save(any(Artist.class))).thenReturn(artist);
        when(artistMapper.toDto(any(Artist.class), eq(true))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        ArtistPresenterDTO result = artistService.addAlbum(1L, 1L);

        assertNotNull(result);
        verify(artistRepository).save(artist);
    }

    @Test
    void removeAlbum_WithValidData_ShouldRemoveAlbumFromArtist() {
        Album album = new Album();
        album.setId(1L);
        album.setArtists(new HashSet<>());
        artist.getAlbums().add(album);

        when(artistRepository.findByIdWithAlbums(1L)).thenReturn(artist);
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(artistRepository.save(any(Artist.class))).thenReturn(artist);
        when(artistMapper.toDto(any(Artist.class), eq(true))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        ArtistPresenterDTO result = artistService.removeAlbum(1L, 1L);

        assertNotNull(result);
        verify(artistRepository).save(artist);
    }

    @Test
    void searchByName_ShouldReturnMatchingArtists() {
        when(artistRepository.findByNameContainingIgnoreCase(anyString()))
            .thenReturn(Arrays.asList(artist));
        when(artistMapper.toDto(any(Artist.class), eq(false))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        List<ArtistPresenterDTO> result = artistService.searchByName("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchByNameWithSort_ShouldReturnSortedArtists() {
        when(artistRepository.findByNameContainingIgnoreCase(anyString(), any(Sort.class)))
            .thenReturn(Arrays.asList(artist));
        when(artistMapper.toDto(any(Artist.class), eq(false))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        List<ArtistPresenterDTO> result = artistService.searchByName("Test", "asc");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByGenre_ShouldReturnArtistsByGenre() {
        when(artistRepository.findByMusicGenreIgnoreCase(anyString()))
            .thenReturn(Arrays.asList(artist));
        when(artistMapper.toDto(any(Artist.class), eq(false))).thenReturn(artistPresenterDTO);
        when(fileUploadService.refreshPresignedUrl(anyString(), anyInt()))
            .thenReturn(UploadResult.of("file.jpg", "file-path", "presigned-url", 1000L, "image/jpeg", 30));

        List<ArtistPresenterDTO> result = artistService.findByGenre("Rock");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(artistRepository).findByMusicGenreIgnoreCase("Rock");
    }
}

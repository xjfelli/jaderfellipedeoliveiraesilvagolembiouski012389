package com.gerenciadorartistas.backend.features.album.service;

import com.gerenciadorartistas.backend.features.album.dto.AlbumDTO;
import com.gerenciadorartistas.backend.features.album.dto.AlbumPresenterDTO;
import com.gerenciadorartistas.backend.features.album.dto.AlbumPresenterDTO.ArtistSimplifiedPresenterDTO;
import com.gerenciadorartistas.backend.features.album.entity.Album;
import com.gerenciadorartistas.backend.features.album.mapper.AlbumMapper;
import com.gerenciadorartistas.backend.features.album.repository.AlbumRepository;
import com.gerenciadorartistas.backend.shared.dto.UploadResult;
import com.gerenciadorartistas.backend.shared.service.FileUploadService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private AlbumMapper albumMapper;

    @Autowired
    private FileUploadService fileUploadService;

    private AlbumPresenterDTO refreshUrls(AlbumPresenterDTO dto) {
        String refreshedCoverUrl =
            dto.coverUrl() != null
                ? fileUploadService
                      .refreshPresignedUrl(dto.coverUrl(), 30)
                      .presignedUrl()
                : null;

        Set<ArtistSimplifiedPresenterDTO> refreshedArtists = null;
        if (dto.artists() != null) {
            refreshedArtists = dto
                .artists()
                .stream()
                .map(artist -> {
                    String refreshedPhotoUrl =
                        artist.photoUrl() != null
                            ? fileUploadService
                                  .refreshPresignedUrl(artist.photoUrl(), 30)
                                  .presignedUrl()
                            : null;
                    return new ArtistSimplifiedPresenterDTO(
                        artist.id(),
                        artist.name(),
                        artist.musicalGenre(),
                        refreshedPhotoUrl
                    );
                })
                .collect(Collectors.toSet());
        }

        return new AlbumPresenterDTO(
            dto.id(),
            dto.title(),
            dto.releaseYear(),
            dto.recordLabel(),
            dto.trackCount(),
            refreshedCoverUrl,
            dto.description(),
            dto.createdAt(),
            dto.updatedAt(),
            refreshedArtists
        );
    }

    public List<AlbumPresenterDTO> findAll() {
        return albumRepository
            .findAll()
            .stream()
            .map(album -> refreshUrls(albumMapper.toPresenterDTO(album, false)))
            .collect(Collectors.toList());
    }

    public List<AlbumPresenterDTO> findAllWithArtists() {
        return albumRepository
            .findAllWithArtists()
            .stream()
            .map(album -> refreshUrls(albumMapper.toPresenterDTO(album, true)))
            .collect(Collectors.toList());
    }

    public Page<AlbumPresenterDTO> findAllPaginated(
        Pageable pageable,
        boolean includeArtists
    ) {
        Page<Album> albums = albumRepository.findAll(pageable);

        return albums.map(album ->
            refreshUrls(albumMapper.toPresenterDTO(album, includeArtists))
        );
    }

    public AlbumPresenterDTO findById(Long id) {
        Album album = albumRepository.findByIdWithArtists(id);
        if (album == null) {
            throw new RuntimeException("Álbum não encontrado com id: " + id);
        }
        return refreshUrls(albumMapper.toPresenterDTO(album, true));
    }

    public AlbumPresenterDTO create(AlbumDTO albumDTO, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Capa do álbum é obrigatória."
            );
        }

        String storageId = UUID.randomUUID().toString();

        UploadResult uploadResult = fileUploadService.uploadFile(
            file,
            "albums/" + storageId
        );

        if (uploadResult == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Erro ao fazer upload da capa do álbum"
            );
        }

        String coverUrl = uploadResult.filePath();

        Album album = albumMapper.toEntity(albumDTO);
        album.setCoverUrl(coverUrl);
        album.setStorageId(storageId);

        album = albumRepository.save(album);
        return refreshUrls(albumMapper.toPresenterDTO(album, false));
    }

    public AlbumPresenterDTO update(Long id, AlbumDTO albumDTO) {
        Album album = albumRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("Álbum não encontrado com id: " + id)
            );

        album = albumRepository.save(albumMapper.toEntity(albumDTO));
        return refreshUrls(albumMapper.toPresenterDTO(album, false));
    }

    public void delete(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new RuntimeException("Álbum não encontrado com id: " + id);
        }
        albumRepository.deleteById(id);
    }

    public List<AlbumPresenterDTO> searchByTitle(String title) {
        return albumRepository
            .findByTitleContainingIgnoreCase(title)
            .stream()
            .map(album -> refreshUrls(albumMapper.toPresenterDTO(album, false)))
            .collect(Collectors.toList());
    }

    public List<AlbumPresenterDTO> findByYear(Integer year) {
        return albumRepository
            .findByReleaseYear(year)
            .stream()
            .map(album -> refreshUrls(albumMapper.toPresenterDTO(album, false)))
            .collect(Collectors.toList());
    }

    public List<AlbumPresenterDTO> findByArtistId(Long artistId) {
        return albumRepository
            .findByArtistId(artistId)
            .stream()
            .map(album -> refreshUrls(albumMapper.toPresenterDTO(album, true)))
            .collect(Collectors.toList());
    }

    public List<AlbumPresenterDTO> findByArtistName(String artistName) {
        return albumRepository
            .findByArtistNameContaining(artistName)
            .stream()
            .map(album -> refreshUrls(albumMapper.toPresenterDTO(album, true)))
            .collect(Collectors.toList());
    }
}

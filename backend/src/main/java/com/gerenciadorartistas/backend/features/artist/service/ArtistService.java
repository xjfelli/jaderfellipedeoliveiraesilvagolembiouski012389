package com.gerenciadorartistas.backend.features.artist.service;

import com.gerenciadorartistas.backend.features.album.entity.Album;
import com.gerenciadorartistas.backend.features.album.repository.AlbumRepository;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistDTO;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistPresenterDTO;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistPresenterDTO.AlbumSimplifiedPresenterDTO;
import com.gerenciadorartistas.backend.features.artist.entity.Artist;
import com.gerenciadorartistas.backend.features.artist.mapper.ArtistMapper;
import com.gerenciadorartistas.backend.features.artist.repository.ArtistRepository;
import com.gerenciadorartistas.backend.shared.dto.UploadResult;
import com.gerenciadorartistas.backend.shared.service.FileUploadService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private FileUploadService fileUploadService;

    private ArtistPresenterDTO refreshUrls(ArtistPresenterDTO dto) {
        String refreshedPhotoUrl =
            dto.photoUrl() != null
                ? fileUploadService
                      .refreshPresignedUrl(dto.photoUrl(), 30)
                      .presignedUrl()
                : null;

        Set<AlbumSimplifiedPresenterDTO> refreshedAlbums = null;
        if (dto.albums() != null) {
            refreshedAlbums = dto
                .albums()
                .stream()
                .map(album -> {
                    String refreshedCoverUrl =
                        album.coverUrl() != null
                            ? fileUploadService
                                  .refreshPresignedUrl(album.coverUrl(), 30)
                                  .presignedUrl()
                            : null;
                    return new AlbumSimplifiedPresenterDTO(
                        album.id(),
                        album.title(),
                        album.releaseYear(),
                        refreshedCoverUrl
                    );
                })
                .collect(Collectors.toSet());
        }

        return new ArtistPresenterDTO(
            dto.id(),
            dto.name(),
            dto.musicalGenre(),
            dto.biography(),
            dto.countryOfOrigin(),
            refreshedPhotoUrl,
            dto.storageId(),
            dto.createdAt(),
            dto.updatedAt(),
            refreshedAlbums
        );
    }

    public List<ArtistPresenterDTO> findAll() {
        return artistRepository
            .findAll()
            .stream()
            .map(artist -> refreshUrls(artistMapper.toDto(artist, false)))
            .collect(Collectors.toList());
    }

    public List<ArtistPresenterDTO> findAllWithAlbums() {
        return artistRepository
            .findAllWithAlbums()
            .stream()
            .map(artist -> refreshUrls(artistMapper.toDto(artist, true)))
            .collect(Collectors.toList());
    }

    public Page<ArtistPresenterDTO> findAllPaginated(
        Pageable pageable,
        boolean includeAlbums
    ) {
        Page<Artist> artists = artistRepository.findAll(pageable);

        return artists.map(artist -> {
            ArtistPresenterDTO dto = includeAlbums
                ? artistMapper.toDto(artist, true)
                : artistMapper.toDto(artist, false);

            return refreshUrls(dto);
        });
    }

    public ArtistPresenterDTO findById(Long id) {
        Artist artist = artistRepository.findByIdWithAlbums(id);
        if (artist == null) {
            throw new RuntimeException("Artista não encontrado com id: " + id);
        }
        return refreshUrls(artistMapper.toDto(artist, true));
    }

    public ArtistPresenterDTO create(ArtistDTO artistDTO, MultipartFile file) {
        List<Artist> existsByName =
            artistRepository.findByNameContainingIgnoreCase(
                artistDTO.getName()
            );

        if (!existsByName.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Já existe um artista cadastrado com esse nome."
            );
        }

        String storageId = UUID.randomUUID().toString();
        String photoUrl = null;

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Foto do artista é obrigatória."
            );
        }

        UploadResult uploadResult = fileUploadService.uploadFile(
            file,
            "artists/" + storageId
        );

        if (uploadResult == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Erro ao fazer upload da foto do artista"
            );
        }

        photoUrl = uploadResult.filePath();

        Artist artist = artistMapper.fromDto(artistDTO);
        artist.setPhotoUrl(photoUrl);
        artist.setStorageId(storageId);

        artist = artistRepository.save(artist);
        return refreshUrls(artistMapper.toDto(artist, false));
    }

    public ArtistPresenterDTO update(Long id, ArtistDTO artistDTO, MultipartFile file) {
        Artist artist = artistRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("Artista não encontrado com id: " + id)
            );

        // Atualiza dados básicos (sem tocar na photoUrl ainda)
        artist.update(
            artistDTO.getName(),
            artistDTO.getMusicalGenre(),
            artistDTO.getBiography(),
            artistDTO.getCountryOfOrigin(),
            artist.getPhotoUrl() // Mantém a foto atual
        );

        // Se foi enviado um novo arquivo de foto, faz o upload
        if (file != null && !file.isEmpty()) {
            String storageId = artist.getStorageId();
            if (storageId == null) {
                storageId = UUID.randomUUID().toString();
                artist.setStorageId(storageId);
            }

            UploadResult uploadResult = fileUploadService.uploadFile(
                file,
                "artists/" + storageId
            );

            if (uploadResult == null) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Erro ao fazer upload da foto do artista"
                );
            }

            artist.setPhotoUrl(uploadResult.filePath());
        }

        artist = artistRepository.save(artist);
        return refreshUrls(artistMapper.toDto(artist, false));
    }

    public void delete(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new RuntimeException("Artista não encontrado com id: " + id);
        }
        artistRepository.deleteById(id);
    }

    public ArtistPresenterDTO addAlbum(Long artistId, Long albumId) {
        Artist artist = artistRepository
            .findById(artistId)
            .orElseThrow(() ->
                new RuntimeException(
                    "Artista não encontrado com id: " + artistId
                )
            );
        Album album = albumRepository
            .findById(albumId)
            .orElseThrow(() ->
                new RuntimeException("Álbum não encontrado com id: " + albumId)
            );

        artist.addAlbum(album);
        artist = artistRepository.save(artist);
        return refreshUrls(artistMapper.toDto(artist, true));
    }

    public ArtistPresenterDTO removeAlbum(Long artistId, Long albumId) {
        Artist artist = artistRepository.findByIdWithAlbums(artistId);
        if (artist == null) {
            throw new RuntimeException(
                "Artista não encontrado com id: " + artistId
            );
        }
        Album album = albumRepository
            .findById(albumId)
            .orElseThrow(() ->
                new RuntimeException("Álbum não encontrado com id: " + albumId)
            );

        artist.removeAlbum(album);
        artist = artistRepository.save(artist);
        return refreshUrls(artistMapper.toDto(artist, true));
    }

    public List<ArtistPresenterDTO> searchByName(String name) {
        return artistRepository
            .findByNameContainingIgnoreCase(name)
            .stream()
            .map(artist -> refreshUrls(artistMapper.toDto(artist, false)))
            .collect(Collectors.toList());
    }

    public List<ArtistPresenterDTO> searchByName(
        String name,
        String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "name");
        return artistRepository
            .findByNameContainingIgnoreCase(name, sort)
            .stream()
            .map(artist -> refreshUrls(artistMapper.toDto(artist, false)))
            .collect(Collectors.toList());
    }

    public List<ArtistPresenterDTO> findByGenre(String genre) {
        return artistRepository
            .findByMusicGenreIgnoreCase(genre)
            .stream()
            .map(artist -> refreshUrls(artistMapper.toDto(artist, false)))
            .collect(Collectors.toList());
    }
}

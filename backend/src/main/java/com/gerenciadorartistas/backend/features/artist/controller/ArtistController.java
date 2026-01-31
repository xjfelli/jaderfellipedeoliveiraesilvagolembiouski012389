package com.gerenciadorartistas.backend.features.artist.controller;

import com.gerenciadorartistas.backend.features.artist.dto.ArtistDTO;
import com.gerenciadorartistas.backend.features.artist.dto.ArtistPresenterDTO;
import com.gerenciadorartistas.backend.features.artist.service.ArtistService;
import com.gerenciadorartistas.backend.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/api/artists")
@Tag(name = "Artists", description = "Endpoints para gerenciamento de artistas")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @GetMapping
    @Operation(
        summary = "Listar todos os artistas",
        description = "Retorna uma lista de todos os artistas cadastrados"
    )
    public ResponseEntity<List<ArtistPresenterDTO>> findAll(
        @RequestParam(
            required = false,
            defaultValue = "false"
        ) boolean includeAlbums
    ) {
        List<ArtistPresenterDTO> artists = includeAlbums
            ? artistService.findAllWithAlbums()
            : artistService.findAll();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/paginated")
    @Operation(
        summary = "Listar artistas com paginação",
        description = "Retorna uma lista paginada de artistas"
    )
    public ResponseEntity<Page<ArtistPresenterDTO>> findAllPaginated(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(
            required = false,
            defaultValue = "asc"
        ) String sortDirection,
        @RequestParam(
            required = false,
            defaultValue = "false"
        ) boolean includeAlbums
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(direction, sortBy)
        );

        Page<ArtistPresenterDTO> artists = artistService.findAllPaginated(
            pageable,
            includeAlbums
        );
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar artista por ID",
        description = "Retorna um artista específico pelo seu ID, incluindo seus álbuns"
    )
    public ResponseEntity<ArtistPresenterDTO> findById(@PathVariable Long id) {
        try {
            ArtistPresenterDTO artist = artistService.findById(id);
            return ResponseEntity.ok(artist);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(
        summary = "Criar novo artista",
        description = "Cria um novo artista no sistema"
    )
    public ResponseEntity<ApiResponse<ArtistPresenterDTO>> create(
        @Valid @RequestPart("artist") ArtistDTO artistDTO,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        ArtistPresenterDTO created = artistService.create(artistDTO, file);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar artista",
        description = "Atualiza os dados de um artista existente"
    )
    public ResponseEntity<ArtistPresenterDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody ArtistDTO artistDTO
    ) {
        try {
            ArtistPresenterDTO updated = artistService.update(id, artistDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar artista",
        description = "Remove um artista do sistema"
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            artistService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{artistId}/albums/{albumId}")
    @Operation(
        summary = "Adicionar álbum ao artista",
        description = "Associa um álbum existente a um artista"
    )
    public ResponseEntity<ArtistPresenterDTO> addAlbum(
        @PathVariable Long artistId,
        @PathVariable Long albumId
    ) {
        try {
            ArtistPresenterDTO updated = artistService.addAlbum(
                artistId,
                albumId
            );
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{artistId}/albums/{albumId}")
    @Operation(
        summary = "Remover álbum do artista",
        description = "Remove a associação entre um artista e um álbum"
    )
    public ResponseEntity<ArtistPresenterDTO> removeAlbum(
        @PathVariable Long artistId,
        @PathVariable Long albumId
    ) {
        try {
            ArtistPresenterDTO updated = artistService.removeAlbum(
                artistId,
                albumId
            );
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(
        summary = "Buscar artistas por nome",
        description = "Busca artistas cujo nome contenha o termo informado com ordenação alfabética"
    )
    public ResponseEntity<List<ArtistPresenterDTO>> searchByName(
        @RequestParam String name,
        @RequestParam(
            required = false,
            defaultValue = "asc"
        ) String sortDirection
    ) {
        List<ArtistPresenterDTO> artists = artistService.searchByName(
            name,
            sortDirection
        );
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/genre/{genre}")
    @Operation(
        summary = "Buscar artistas por gênero",
        description = "Retorna todos os artistas de um gênero musical específico"
    )
    public ResponseEntity<List<ArtistPresenterDTO>> findByGenre(
        @PathVariable String genre
    ) {
        List<ArtistPresenterDTO> artists = artistService.findByGenre(genre);
        return ResponseEntity.ok(artists);
    }
}

package com.gerenciadorartistas.backend.features.album.controller;

import com.gerenciadorartistas.backend.features.album.dto.AlbumDTO;
import com.gerenciadorartistas.backend.features.album.dto.AlbumPresenterDTO;
import com.gerenciadorartistas.backend.features.album.service.AlbumService;
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
@RequestMapping("/api/v1/albums")
@Tag(name = "Albums", description = "Endpoints para gerenciamento de álbuns")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @GetMapping
    @Operation(
        summary = "Listar todos os álbuns",
        description = "Retorna uma lista de todos os álbuns cadastrados"
    )
    public ResponseEntity<List<AlbumPresenterDTO>> findAll(
        @RequestParam(
            required = false,
            defaultValue = "false"
        ) boolean includeArtists
    ) {
        List<AlbumPresenterDTO> albums = includeArtists
            ? albumService.findAllWithArtists()
            : albumService.findAll();
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/paginated")
    @Operation(
        summary = "Listar álbuns com paginação",
        description = "Retorna uma lista paginada de álbuns"
    )
    public ResponseEntity<Page<AlbumPresenterDTO>> findAllPaginated(
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
        ) boolean includeArtists
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(direction, sortBy)
        );

        Page<AlbumPresenterDTO> albums = albumService.findAllPaginated(
            pageable,
            includeArtists
        );
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar álbum por ID",
        description = "Retorna um álbum específico pelo seu ID, incluindo seus artistas"
    )
    public ResponseEntity<AlbumPresenterDTO> findById(@PathVariable Long id) {
        try {
            AlbumPresenterDTO album = albumService.findById(id);
            return ResponseEntity.ok(album);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(
        summary = "Criar novo álbum",
        description = "Cria um novo álbum no sistema"
    )
    public ResponseEntity<AlbumPresenterDTO> create(
        @Valid @RequestPart("album") AlbumDTO albumDTO,
        @RequestPart(value = "file", required = true) MultipartFile file
    ) {
        try {
            AlbumPresenterDTO created = albumService.create(albumDTO, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar álbum",
        description = "Atualiza os dados de um álbum existente"
    )
    public ResponseEntity<AlbumPresenterDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody AlbumDTO albumDTO
    ) {
        try {
            AlbumPresenterDTO updated = albumService.update(id, albumDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar álbum",
        description = "Remove um álbum do sistema"
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            albumService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(
        summary = "Buscar álbuns por título",
        description = "Busca álbuns cujo título contenha o termo informado"
    )
    public ResponseEntity<List<AlbumPresenterDTO>> searchByTitle(
        @RequestParam String title
    ) {
        List<AlbumPresenterDTO> albums = albumService.searchByTitle(title);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/year/{year}")
    @Operation(
        summary = "Buscar álbuns por ano",
        description = "Retorna todos os álbuns lançados em um ano específico"
    )
    public ResponseEntity<List<AlbumPresenterDTO>> findByYear(
        @PathVariable Integer year
    ) {
        List<AlbumPresenterDTO> albums = albumService.findByYear(year);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/artist/{artistId}")
    @Operation(
        summary = "Buscar álbuns por artista (ID)",
        description = "Retorna todos os álbuns de um artista específico pelo ID"
    )
    public ResponseEntity<List<AlbumPresenterDTO>> findByArtistId(
        @PathVariable Long artistId
    ) {
        List<AlbumPresenterDTO> albums = albumService.findByArtistId(artistId);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/artist/search")
    @Operation(
        summary = "Buscar álbuns por nome do artista",
        description = "Retorna todos os álbuns cujo artista contenha o nome informado"
    )
    public ResponseEntity<List<AlbumPresenterDTO>> findByArtistName(
        @RequestParam String name
    ) {
        List<AlbumPresenterDTO> albums = albumService.findByArtistName(name);
        return ResponseEntity.ok(albums);
    }
}

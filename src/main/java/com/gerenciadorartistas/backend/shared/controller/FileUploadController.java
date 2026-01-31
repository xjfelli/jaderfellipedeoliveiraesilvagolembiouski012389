package com.gerenciadorartistas.backend.shared.controller;

import com.gerenciadorartistas.backend.shared.dto.UploadResult;
import com.gerenciadorartistas.backend.shared.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/api/files")
@Tag(
    name = "Upload de Arquivos",
    description = "Endpoints para gerenciamento de arquivos e imagens"
)
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload/artist")
    @Operation(
        summary = "Upload de foto de artista",
        description = "Faz upload de uma foto de artista para o storage"
    )
    public ResponseEntity<UploadResult> uploadArtistPhoto(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            UploadResult result = fileUploadService.uploadFile(file, "artists");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/upload/album")
    @Operation(
        summary = "Upload de capa de álbum",
        description = "Faz upload de uma capa de álbum para o storage"
    )
    public ResponseEntity<UploadResult> uploadAlbumCover(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            UploadResult result = fileUploadService.uploadFile(file, "albums");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/upload/album/multiple")
    @Operation(
        summary = "Upload múltiplo de capas de álbum",
        description = "Faz upload de múltiplas capas de álbum para o storage"
    )
    public ResponseEntity<List<UploadResult>> uploadMultipleAlbumCovers(
        @RequestParam("files") MultipartFile[] files
    ) {
        try {
            List<UploadResult> results = fileUploadService.uploadMultipleFiles(
                files,
                "albums"
            );
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/upload/{folder}")
    @Operation(
        summary = "Upload genérico de arquivo",
        description = "Faz upload de um arquivo para uma pasta específica"
    )
    public ResponseEntity<UploadResult> uploadFile(
        @PathVariable String folder,
        @RequestParam("file") MultipartFile file
    ) {
        try {
            UploadResult result = fileUploadService.uploadFile(file, folder);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/upload/{folder}/multiple")
    @Operation(
        summary = "Upload múltiplo genérico de arquivos",
        description = "Faz upload de múltiplos arquivos para uma pasta específica"
    )
    public ResponseEntity<List<UploadResult>> uploadMultipleFiles(
        @PathVariable String folder,
        @RequestParam("files") MultipartFile[] files
    ) {
        try {
            List<UploadResult> results = fileUploadService.uploadMultipleFiles(
                files,
                folder
            );
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/presigned-url")
    @Operation(
        summary = "Gerar URL pré-assinada",
        description = "Gera uma URL pré-assinada com expiração de 30 minutos para acessar um arquivo"
    )
    public ResponseEntity<UploadResult> getPresignedUrl(
        @RequestParam String filePath
    ) {
        try {
            UploadResult result = fileUploadService.refreshPresignedUrl(
                filePath
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/presigned-url/{expiryMinutes}")
    @Operation(
        summary = "Gerar URL pré-assinada customizada",
        description = "Gera uma URL pré-assinada com tempo de expiração customizado"
    )
    public ResponseEntity<UploadResult> getPresignedUrlWithExpiry(
        @RequestParam String filePath,
        @PathVariable int expiryMinutes
    ) {
        try {
            UploadResult result = fileUploadService.refreshPresignedUrl(
                filePath,
                expiryMinutes
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/exists")
    @Operation(
        summary = "Verificar existência de arquivo",
        description = "Verifica se um arquivo existe no storage"
    )
    public ResponseEntity<Boolean> fileExists(@RequestParam String filePath) {
        try {
            boolean exists = fileUploadService.fileExists(filePath);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    @Operation(
        summary = "Deletar arquivo",
        description = "Remove um arquivo do storage"
    )
    public ResponseEntity<Void> deleteFile(@RequestParam String filePath) {
        try {
            if (!fileUploadService.fileExists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            fileUploadService.deleteFile(filePath);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

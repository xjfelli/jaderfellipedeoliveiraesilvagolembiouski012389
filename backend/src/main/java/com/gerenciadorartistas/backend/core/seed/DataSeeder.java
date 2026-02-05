package com.gerenciadorartistas.backend.core.seed;

import com.gerenciadorartistas.backend.features.album.entity.Album;
import com.gerenciadorartistas.backend.features.album.repository.AlbumRepository;
import com.gerenciadorartistas.backend.features.artist.entity.Artist;
import com.gerenciadorartistas.backend.features.artist.repository.ArtistRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(
        DataSeeder.class
    );

    private static final String IMAGES_PATH = "seed/images/";

    // Mapeamento: nome do artista no banco -> nome do arquivo de imagem
    private static final Map<String, String> ARTIST_IMAGES = Map.of(
        "The Beatles",
        "thebeatles.jpg",
        "Queen",
        "queen.jpg",
        "Michael Jackson",
        "michaeljackson.jpg",
        "Pink Floyd",
        "pinkfloyd.jpg",
        "Bob Marley",
        "bobmarley.jpg"
    );

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    @Qualifier("internalMinioClient")
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${seed.images.enabled:true}")
    private boolean seedImagesEnabled;

    @PostConstruct
    @Transactional
    public void seedImages() {
        if (!seedImagesEnabled) {
            logger.info("Seed de imagens desabilitado");
            return;
        }

        logger.info("Iniciando seed de imagens para artistas e álbuns...");

        try {
            seedArtistImages();
            seedAlbumImages();
            logger.info("Seed de imagens concluído com sucesso!");
        } catch (Exception e) {
            logger.error(
                "Erro ao executar seed de imagens: {}",
                e.getMessage(),
                e
            );
        }
    }

    private void seedArtistImages() {
        List<Artist> artistsWithoutPhoto = artistRepository
            .findAll()
            .stream()
            .filter(
                artist ->
                    artist.getPhotoUrl() == null ||
                    artist.getPhotoUrl().isBlank()
            )
            .toList();

        logger.info(
            "Encontrados {} artistas sem foto",
            artistsWithoutPhoto.size()
        );

        for (Artist artist : artistsWithoutPhoto) {
            String imageFileName = ARTIST_IMAGES.get(artist.getName());

            if (imageFileName == null) {
                logger.warn(
                    "Imagem não encontrada para artista: {}",
                    artist.getName()
                );
                continue;
            }

            try {
                String storageId = UUID.randomUUID().toString();
                String minioPath = String.format(
                    "artists/%s/%s.jpg",
                    storageId,
                    UUID.randomUUID()
                );

                boolean uploaded = uploadLocalImage(
                    IMAGES_PATH + imageFileName,
                    minioPath
                );

                if (uploaded) {
                    artist.setPhotoUrl(minioPath);
                    artist.setStorageId(storageId);
                    artistRepository.save(artist);
                    logger.info(
                        "Imagem carregada para artista: {}",
                        artist.getName()
                    );
                }
            } catch (Exception e) {
                logger.warn(
                    "Falha ao carregar imagem para artista {}: {}",
                    artist.getName(),
                    e.getMessage()
                );
            }
        }
    }

    private void seedAlbumImages() {
        List<Album> albumsWithoutCover = albumRepository
            .findAll()
            .stream()
            .filter(
                album ->
                    album.getCoverUrl() == null || album.getCoverUrl().isBlank()
            )
            .toList();

        logger.info(
            "Encontrados {} álbuns sem capa",
            albumsWithoutCover.size()
        );

        // Por enquanto, álbuns não têm imagens de seed
        // Quando adicionar imagens de álbuns em seed/images/, adicionar lógica similar aqui
    }

    private boolean uploadLocalImage(String resourcePath, String minioPath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);

            if (!resource.exists()) {
                logger.warn("Arquivo não encontrado: {}", resourcePath);
                return false;
            }

            try (InputStream inputStream = resource.getInputStream()) {
                long fileSize = resource.contentLength();

                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(minioPath)
                        .stream(inputStream, fileSize, -1)
                        .contentType("image/jpeg")
                        .build()
                );
                return true;
            }
        } catch (Exception e) {
            logger.error(
                "Erro ao fazer upload da imagem {}: {}",
                resourcePath,
                e.getMessage()
            );
            return false;
        }
    }
}
